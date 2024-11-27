package com.hms.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AppUserDto {

    private String name;

    private String email;

    private String mobileNum;

    private String role;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
