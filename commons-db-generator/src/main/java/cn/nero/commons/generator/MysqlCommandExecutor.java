package cn.nero.commons.generator;

import cn.nero.commons.generator.constant.JavaTypeEnums;
import cn.nero.commons.generator.domain.Column;
import cn.nero.commons.generator.domain.Table;
import cn.nero.commons.helper.WordHelper;
import com.google.common.base.Joiner;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/1
 */
@Setter
public class MysqlCommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(MysqlCommandExecutor.class.getName());

    private String url;
    private String username;
    private String password;

    private static final String GET_TABLES = "select TABLE_SCHEMA, table_name, TABLE_COMMENT from information_schema.TABLES where TABLE_SCHEMA = ?";
    private static final String GET_COLUMNS = "select COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT, COLUMN_KEY from information_schema.COLUMNS where TABLE_SCHEMA = ? and TABLE_NAME = ?;";

    private static final Integer MAX_SIZE = 10;

    private ArrayBlockingQueue<Connection> connections = new ArrayBlockingQueue<>(MAX_SIZE);

    MysqlCommandExecutor() {

    }

    private static Connection createConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public MysqlCommandExecutor(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < MAX_SIZE; i++) {
            try {
                connections.offer(createConnection(url, username, password));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Table> getTablesByDatabaseName(String databaseName) throws InterruptedException, SQLException {
        List<Table> tables;
        Connection connection = connections.take();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_TABLES);
            preparedStatement.setString(1, databaseName);
            ResultSet resultSet = preparedStatement.executeQuery();
            tables = new ArrayList<>();
            while (resultSet.next()) {
                String tableSchema = resultSet.getString("TABLE_SCHEMA");
                String tableName = resultSet.getString("table_name");
                String tableComment = resultSet.getString("TABLE_COMMENT");

                Table table = new Table();
                table.setSchema(tableSchema);
                table.setName(tableName);
                table.setComment(tableComment);

                List<Column> columns = getColumnsByTableName(tableSchema, tableName);
                table.setColumns(columns);

                tables.add(table);
            }
        } finally {
            connections.offer(connection);
        }
        return tables;
    }

    private String formatField(String columnName) {
        String[] words = columnName.split("_");
        return words[0] + Arrays.stream(words)
                .map(word -> Objects.nonNull(word) && !word.isEmpty() ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : "")
                .skip(1)
                .collect(Collectors.joining());
    }

    public List<Column> getColumnsByTableName(Table table) throws InterruptedException {
        return getColumnsByTableName(table.getSchema(), table.getName());
    }

    public List<Column> getColumnsByTableName(String tableSchema, String tableName) throws InterruptedException {
        List<Column> columns = new ArrayList<>();
        Connection connection = connections.take();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_COLUMNS);
            preparedStatement.setString(1, tableSchema);
            preparedStatement.setString(2, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("DATA_TYPE");
                String columnComment = resultSet.getString("COLUMN_COMMENT");
                String columnKey = resultSet.getString("COLUMN_KEY");

                Column column = new Column();
                column.setColumnName(formatField(columnName));
                column.setDataType(dataType);
                column.setColumnComment(columnComment);
                column.setColumnKey(columnKey);
                column.setJavaType(JavaTypeEnums.fromMysqlType(dataType).getJavaType());
                columns.add(column);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connections.offer(connection);
        }
        return columns;
    }

    @SneakyThrows
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/user";
        String username = "root";
        String password = "2018023931";
        MysqlCommandExecutor executor = new MysqlCommandExecutor(url, username, password);
        try {
            List<Table> tables = executor.getTablesByDatabaseName("user");
            //System.out.println(JSON.toJSONString(tables));

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/commons-db-generator" + "/src/main/resources/ftls"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Table table = tables.get(0);
            Map<String, Object> map = new HashMap<>();
            map.put("tableName", WordHelper.underline2ClassName(table.getName()));
            map.put("columns", table.getColumns());
            map.put("package", "cn.nero.commons.generator.domain.po");
            map.put("hasLocalDateTime", table.getHasLocalDateTime());
            map.put("hasLocalDate", table.getHasLocalDate());

            String formattedLocalDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now());

            map.put("author", "Nero Claudius");
            map.put("currentDate", formattedLocalDate);

            Template template = cfg.getTemplate("entity-po.java.ftl");
            Writer writer = new StringWriter();
            template.process(map, writer);

            String module = "commons-db-generator";
            String parent = "cn.nero.commons.generator.domain";
            String fileName = WordHelper.underline2ClassName(table.getName()) + "PO.java";
            EntityGenerator.generateFile(module, parent, fileName, writer.toString());

            LOGGER.info("生成文件成功!");

        } catch (InterruptedException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
