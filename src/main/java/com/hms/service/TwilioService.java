package com.hms.service;

import com.hms.entity.AppUser;
import com.hms.repository.AppUserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final AppUserRepository appUserRepository;

    @Value("${twilio.from-phone-number}")
    private String fromPhoneNumber;

    @Value("${twilio.whatsapp-sender}")
    private String whatsappSender;

    public TwilioService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }


    public void sendBookingConfirmationSms(String messageBody,String toPhoneNumber) {
        if (!toPhoneNumber.startsWith("+91")) {
            toPhoneNumber = "+91" + toPhoneNumber;
        }
        Message.creator(new PhoneNumber(toPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                messageBody)
                .create();
    }

    public void sendBookingConfirmationWhatsAppMessage(String messageBody,String toPhoneNumber) {
        if (!toPhoneNumber.startsWith("+91")) {
            toPhoneNumber = "+91" + toPhoneNumber;
        }
        Message.creator(new PhoneNumber("whatsapp:" + toPhoneNumber),
                new PhoneNumber(whatsappSender)
                , messageBody)
                .create();
    }

    public void sendOTPViaSMS(String mobileNum, String otp) {
        AppUser appUser = appUserRepository.findByMobileNum(mobileNum)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!mobileNum.startsWith("+91")) {
            mobileNum = "+91" + mobileNum;
        }
        Message.creator(new PhoneNumber(mobileNum),
                new PhoneNumber(fromPhoneNumber),
                "Hello " + appUser.getName() + ", Your HMS login OTP is " + otp +
                        ". Validate for the next 5 mins. Do not share this with anyone.")
                .create();
    }

    public void sendOTPViaWhatsAppMessage(String mobileNum, String otp) {
        AppUser appUser = appUserRepository.findByMobileNum(mobileNum)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!mobileNum.startsWith("+91")) {
            mobileNum = "+91" + mobileNum;
        }
        Message.creator(new PhoneNumber("whatsapp:" + mobileNum),
                new PhoneNumber(whatsappSender),
                "Hello "+ appUser.getName() +", Your HMS login OTP is "+ otp +
                        " .Validate for the next 5 mins. Do not share this with anyone.")
                .create();
    }
}
