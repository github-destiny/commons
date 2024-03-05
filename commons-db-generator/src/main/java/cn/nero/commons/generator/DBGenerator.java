package cn.nero.commons.generator;

import cn.nero.commons.generator.domain.Table;
import cn.nero.commons.helper.WordHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/5
 */
public class DBGenerator {

    private static final Logger LOGGER = Logger.getLogger(DBGenerator.class.getName());

    private static final String PO_TEMPLATE_NAME = "entity-po.java.ftl";
    private static final String MAPPER_TEMPLATE_NAME = "mapper.java.ftl";

    private static String entityGenerator(Configuration cfg, Table table, String parent) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", WordHelper.underline2ClassName(table.getName()));
        map.put("columns", table.getColumns());
        map.put("package", parent);
        map.put("hasLocalDateTime", table.getHasLocalDateTime());
        map.put("hasLocalDate", table.getHasLocalDate());

        String formattedLocalDate = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                                     .format(LocalDate.now());

        map.put("author", "Nero Claudius");
        map.put("currentDate", formattedLocalDate);
        Writer writer = null;
        try {
            Template template = cfg.getTemplate(PO_TEMPLATE_NAME);
            writer = new StringWriter();
            template.process(map, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private static String mapperGenerator(Configuration cfg, Table table, String clazzName, String parent, String entityPath) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("clazzName", clazzName);
        map.put("tableName", WordHelper.underline2ClassName(table.getName()));
        map.put("parent", parent);
        map.put("entityPath", entityPath);
        map.put("author", "Nero Claudius");
        String formattedLocalDate = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                                     .format(LocalDate.now());
        map.put("currentDate", formattedLocalDate);
        Template template = cfg.getTemplate(MAPPER_TEMPLATE_NAME);
        Writer writer = new StringWriter();
        template.process(map, writer);
        return writer.toString();
    }

    @SneakyThrows
    public static void generator(DatabaseOptions databaseOptions, GeneratorOptions generatorOptions) {
        MysqlCommandExecutor commandExecutor = new MysqlCommandExecutor(databaseOptions.getUrl(),
                                                                        databaseOptions.getUsername(),
                                                                        databaseOptions.getPassword());
        List<Table> tables = commandExecutor.getTablesByDatabaseName(generatorOptions.databaseName);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(
                System.getProperty("user.dir") + "/commons-db-generator" + "/src/main/resources/ftls"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        LOGGER.info("=============================开始生成实体对象=============================");
        tables.forEach(table -> {
            // generate entity

            String packageStr = generatorOptions.getParent() + "." + generatorOptions.getDatabaseName();
            String entityPackage = packageStr + ".entity";
            String mapperPackage = packageStr + ".mappers";
            String tableName = WordHelper.underline2ClassName(table.getName());
            String clazzName = tableName + "PO";
            String fileName = clazzName + ".java";


            String baseParent = generatorOptions.getParent() + "." + generatorOptions.getDatabaseName();
            String mergedTemplate = entityGenerator(cfg,
                                                    table,
                                                    entityPackage);
            FileGenerator.generateFile(generatorOptions.getModule(), entityPackage, clazzName + ".java", mergedTemplate);
            LOGGER.info("====>生成实体类 : " + clazzName);
            // generate mapper
            String mergedMapper = mapperGenerator(cfg,
                                                  table,
                                                  clazzName,
                                                  mapperPackage,
                                                  entityPackage);
            FileGenerator.generateFile(generatorOptions.getModule(), mapperPackage, tableName + "MplusMapper.java", mergedMapper);
            LOGGER.info("====>生成Mapper : " + clazzName);

        });
    }

    @Data
    @Builder
    public static class DatabaseOptions {

        private String url;

        private String username;

        private String password;

    }

    @Data
    @Builder
    public static class GeneratorOptions {

        private String parent;

        private Boolean generateMapper = Boolean.TRUE;

        private Boolean generateService = Boolean.TRUE;

        private String module;

        private String databaseName;

        private String author;

    }

    public static void main(String[] args) {
        DatabaseOptions dbOptions = DatabaseOptions.builder()
                                                   .url("jdbc:mysql://localhost:3306/user")
                                                   .username("root")
                                                   .password("2018023931")
                                                   .build();
        GeneratorOptions generatorOptions = GeneratorOptions.builder()
                                                            .databaseName("user")
                                                            .module("commons-db-generator")
                                                            .parent("cn.nero.commons.generator.database")
                                                            .build();
        DBGenerator.generator(dbOptions, generatorOptions);
    }

}
