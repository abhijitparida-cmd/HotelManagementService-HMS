package com.hms.service;

import com.hms.payload.OTPDataDto;
import com.hms.util.OTPUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OTPService {

    private final OTPUtil otpUtil;
    private final TwilioService twilioService;

    public OTPService(OTPUtil otpUtil, TwilioService twilioService) {
        this.otpUtil = otpUtil;
        this.twilioService = twilioService;
    }

    // ------------------- Temporary Storage OTP ------------------- //

    private final Map<String, OTPDataDto> otpStorage = new HashMap<>();
    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes

    // ------------------- Generate and Send OTP ------------------- //

    public void generateAndSendOTP(String mobileNum) {
        String otp = otpUtil.generateOTP();
        otpStorage.put(mobileNum, new OTPDataDto(otp, System.currentTimeMillis() + OTP_EXPIRY_TIME));
        twilioService.sendOTPViaSMS(mobileNum, otp);
        twilioService.sendOTPViaWhatsAppMessage(mobileNum, otp);
    }

    // ------------------- Validate OTP Address ------------------- //

    public boolean validateOTP(String mobileNum, String otp) {
        OTPDataDto storedOTPData = otpStorage.get(mobileNum);
        if (storedOTPData == null) {
            return false;
        }
        if (System.currentTimeMillis() > storedOTPData.getExpiryTime()) {
            otpStorage.remove(mobileNum);
            return false;
        }
        if (storedOTPData.getOtp().equals(otp)) {
            otpStorage.remove(mobileNum); // clear otp
            return true;
        }
        return false;
    }
}
