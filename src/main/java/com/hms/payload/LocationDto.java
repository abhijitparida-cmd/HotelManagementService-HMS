package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {

    private String cityName;

    private String locationName;

    private Integer pinCode;
}
