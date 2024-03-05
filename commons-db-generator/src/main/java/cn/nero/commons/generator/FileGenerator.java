package cn.nero.commons.generator;

import com.google.common.base.Joiner;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/1
 */
@Setter
public class FileGenerator {

    /**
     * 模块名
     */
    private String module;

    /**
     * 包名
     * e.g. com.example
     */
    private String parent;

    /**
     * 类路径
     */
    private String sourcePath;

    /**
     * 输出文件名
     */
    private String fileName;

    /**
     * 文件内容
     */
    private String content;

    public static void generateFile (String module, String parent, String fileName, String content) {
        generateFile(module, parent, "\\src\\main\\java", fileName, content);
    }

    @SneakyThrows
    public static void generateFile(String module, String parent, String sourcePath, String fileName, String content) {

        String path = Joiner.on("\\").join(parent.split("\\."));

        String dirPath = System.getProperty("user.dir") + "\\" + module + sourcePath + "\\" + path;

        File dirFile = new File(dirPath);

        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        String generatePath = dirPath + "\\" + fileName;

        File file = new File(generatePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        channel.close();
        randomAccessFile.close();
    }

    public static void main(String[] args) {
        FileGenerator.generateFile("commons-db-generator", "cn.nero.commons.generator.domain", "text.txt", "你好谢谢小笼包再见111");
    }

}
