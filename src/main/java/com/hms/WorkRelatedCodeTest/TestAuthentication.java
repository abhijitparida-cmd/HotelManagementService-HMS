package com.hms.WorkRelatedCodeTest;

import com.hms.entity.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestAuthentication {

    @PostMapping("/add")
    public AppUser addAnything(@AuthenticationPrincipal AppUser appUser) {
        return appUser;
    }
}
