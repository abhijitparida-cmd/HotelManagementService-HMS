package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class PropertyDto {

    private String countryName;

    private String stateName;

    private String cityName;

    private String locationName;

    private Integer pinCode;

    private String hotelName;

    private String roomTypes;

    private BigDecimal priceOfRooms;

    private Integer noOfGuests;

    private Integer noOfBedrooms;

    private Integer noOfBeds;

    private Integer noOfBathrooms;
}
