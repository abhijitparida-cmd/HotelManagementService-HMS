package com.hms.WorkRelatedCodeTest;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncryptPass {
    public static void main(String[] args) {
        EncryptPass.firstWay();
        EncryptPass.secondWay();
    }

    public static void firstWay() {
        PasswordEncoder en = new BCryptPasswordEncoder();
        String enPass = en.encode("testing");
        System.out.println(enPass);
    }

    public static void secondWay() {
        String enPass = BCrypt.hashpw("testing", BCrypt.gensalt(5)); //log_round: min 4 - max 31
        System.out.println(enPass);
    }
}
