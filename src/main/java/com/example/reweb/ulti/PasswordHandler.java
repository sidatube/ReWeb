package com.example.reweb.ulti;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHandler {

    private static MessageDigest md;

    // hàm băm chuỗi.
    public static String getMd5(String input, String salt) {
        input += salt;
        try {
            // Static getInstance method is called with hashing MD5
            if (md == null) {
                md = MessageDigest.getInstance("MD5");
            }
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // hàm sinh muối
    public static String generateSalt() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    /**
     * Phương thức này dùng để <b>kiểm tra mật khẩu</b> của người khi login với mật
     * khẩu đã mã hoá trong database kèm muối (cũng từ database)
     * <a href="https://www.baeldung.com/java-password-hashing">Xem thêm</a>.
     *
     * @param password     mật khẩu người dùng nhập vào từ bàn phím.
     * @param passwordHash là mật khẩu đã băm từ trong database.
     * @param salt         là muối lấy ra từ database.
     * @return true nếu khớp mật khẩu, false nếu không
     * @author Hung
     */
    public static boolean checkPassword(String password, String passwordHash, String salt) {
        String newHash = PasswordHandler.getMd5(password, salt);
        return newHash.equals(passwordHash);
    }

}
