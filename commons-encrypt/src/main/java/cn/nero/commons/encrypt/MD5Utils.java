package cn.nero.commons.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/2/29
 */
public class MD5Utils {

    public static final String MD5 = "MD5";

    public static String md5 (String input) {
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] digestArray = md5.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digestArray.length; i++) {
            int val = ((int) digestArray[i] & 0xff);
            if (val < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }

    public static String md5Salt (String input) {
        String salt = RandomUtils.simpleRandom(8);
        System.out.println("salt : " + salt);
        return md5(salt + input);
    }

    public static String md5Salt (String salt, String input) {
        return md5(salt + input);
    }

    public static boolean verify (String md5, String input, String salt) {
        String result = md5Salt(salt, input);
        return result.equals(md5);
    }

}
