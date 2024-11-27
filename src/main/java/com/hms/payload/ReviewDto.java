package com.hms.payload;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    private int ratings;

    private String description;

    private AppUserDto appUserId;

    private PropertyDto propertyId;
}

