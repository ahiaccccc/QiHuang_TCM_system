package com.example.qihuangserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

//    // 使用SHA-256进行密码加密（带盐值）
//    public static String encryptPassword(String password) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] salt = generateSalt();
//            md.update(salt);
//            byte[] hashedPassword = md.digest(password.getBytes());
//            return Base64.getEncoder().encodeToString(hashedPassword);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("密码加密失败", e);
//        }
//    }
//
//    // 生成随机盐值
//    private static byte[] generateSalt() {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//        return salt;
//    }
//
//    // 验证密码
//    public static boolean verifyPassword(String inputPassword, String storedPassword) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] inputHash = md.digest(inputPassword.getBytes());
//            String inputHashStr = Base64.getEncoder().encodeToString(inputHash);
//            return inputHashStr.equals(storedPassword);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("密码验证失败", e);
//        }
//    }

    // 生成随机密码（8-12位包含大小写字母和数字）
    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        int length = 8 + random.nextInt(5); // 8-12位长度

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


}
