package com.hms.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OTPUtil {

    private static final int OTP_LENGTH = 4 ;

    public String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
