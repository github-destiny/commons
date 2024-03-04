package cn.nero.commons.helper;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @Date 2024/3/4
 */
public class WordHelper {

    public static String titleCase (String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String underline2CamelCase (String word) {
        String[] words = word.split("_");
        return words[0] + Arrays.stream(words).skip(1).map(it -> it.substring(0, 1).toUpperCase() + it.substring(1)).collect(Collectors.joining());
    }

    public static String underline2ClassName (String word) {
        return Arrays.stream(word.split("_")).map(it -> it.substring(0, 1).toUpperCase() + it.substring(1)).collect(Collectors.joining());
    }

    public static void main(String[] args) {
        String word = "underline_naming_method";
        System.out.println(underline2CamelCase(word));

        System.out.println(titleCase(word));
    }

}
