package com.hms.payload;

import lombok.Getter;

@Getter
public class OTPDataDto {

    private final String otp;

    private final long expiryTime;

    public OTPDataDto(String otp, long expiryTime) {
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

}
