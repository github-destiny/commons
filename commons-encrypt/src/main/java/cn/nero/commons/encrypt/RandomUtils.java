package cn.nero.commons.encrypt;

import java.util.Objects;
import java.util.Random;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/2/29
 */
public class RandomUtils {

    public static final String SEED_OF_NUMBER = "1234567890";

    public static final String SEED_OF_LOWER_CASE_LETTER = "abcdefghijklmnopqrstuvwxyz";

    public static final String SEED_OF_UPPER_CASE_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String SEED_OF_ALL_SYMBOL_ON_KEYBOARD = "~`!@#$%^&*A()_+-=[]{}|\\;:\"',.<>/?";

    public static final String SEED_OF_COMMON_SYMBOL = "!@#$%^&*()_+-=";

    private static final Integer DEFAULT_LENGTH = 32;

    public static String allSeed () {
        StringBuilder sb = new StringBuilder();
        sb.append(SEED_OF_NUMBER);
        sb.append(SEED_OF_LOWER_CASE_LETTER);
        sb.append(SEED_OF_UPPER_CASE_LETTER);
        sb.append(SEED_OF_ALL_SYMBOL_ON_KEYBOARD);
        return sb.toString();
    }

    public static String simpleSeed () {
        StringBuilder sb = new StringBuilder();
        sb.append(SEED_OF_NUMBER);
        sb.append(SEED_OF_LOWER_CASE_LETTER);
        sb.append(SEED_OF_UPPER_CASE_LETTER);
        sb.append(SEED_OF_COMMON_SYMBOL);
        return sb.toString();
    }

    public static String random () {
        return random(DEFAULT_LENGTH);
    }

    public static String random (int length) {
        return random(allSeed(), length);
    }

    public static String random (String seed, int length) {
        if (Objects.isNull(seed) || seed.isEmpty()) {
            seed = allSeed();
        }
        char[] charArray = seed.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(charArray[new Random().nextInt(charArray.length - 1)]);
        }
        return sb.toString();
    }

    public static String simpleRandom () {
        return random(simpleSeed(), DEFAULT_LENGTH);
    }

    public static String simpleRandom (int length) {
        return random(simpleSeed(), length);
    }

    public static void main(String[] args) {
        System.out.println(RandomUtils.simpleRandom());
    }

}
