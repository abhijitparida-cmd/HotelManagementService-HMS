package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto {

    private String hotelName;

    private String roomTypes;

    private BigDecimal priceOfRooms;
}
