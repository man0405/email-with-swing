package com.example.library.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Băm mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Kiểm tra mật khẩu
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}