package cn.nero.commons.generator;

import cn.nero.commons.generator.domain.Column;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/1
 */
@Setter
public class EntityGenerator {

    private String path;
    private String module;

    private static final String SOURCE_PATH = "src/main/java";

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/commons-db-generator/src/main/resources" + "/ftls"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        String url = "jdbc:mysql://localhost:3306/user";
        String username = "root";
        String password = "2018023931";

        MysqlCommandExecutor executor = new MysqlCommandExecutor(url, username, password);
        List<Column> columns = executor.getColumnsByTableName("user", "user_info");


    }

}
