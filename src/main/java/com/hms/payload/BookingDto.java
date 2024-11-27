package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class BookingDto {

    private String noOfAdults;

    private String noOfChildren;

    private String noOfRooms;

    private String bookingDate;

    private Date checkIn;

    private Date checkOut;

    private BigDecimal totalPrice;

    private String bookingCode;

    private AppUserDto appUserId;

    private PropertyDto propertyId;
}
