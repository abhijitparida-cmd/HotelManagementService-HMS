package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RoomAvailabilityDto {

    private Date date;

    private Integer availableRooms;

    private PropertyDto propertyDto;

}

