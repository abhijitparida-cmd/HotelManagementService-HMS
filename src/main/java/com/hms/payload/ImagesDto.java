package com.hms.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagesDto {

    private String imageUrl;

    private AppUserDto appUserDto;

    private PropertyDto propertyDto;
}
