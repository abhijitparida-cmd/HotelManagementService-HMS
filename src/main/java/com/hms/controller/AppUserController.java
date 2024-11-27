package com.hms.controller;

import com.hms.payload.AppUserDto;
import com.hms.payload.LoginDto;
import com.hms.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // ----------------------- SignUp ----------------------- //

    @PostMapping("/signup-owner")
    public ResponseEntity<?> addPropertyOwner(@RequestBody AppUserDto appUserDto) {
        if (appUserService.verifySignupUsername(appUserDto)) {
            return new ResponseEntity<>("Username already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (appUserService.verifySignupEmail(appUserDto)) {
            return new ResponseEntity<>("Email already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (appUserService.verifySignupMobile(appUserDto)) {
            return new ResponseEntity<>("Mobile Number already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        appUserService.encryptPassword(appUserDto);
        appUserService.setRoleForOwner(appUserDto);
        return new ResponseEntity<>(appUserService.addUser(appUserDto),HttpStatus.CREATED);
    }

    @PostMapping("/signup-user")
    public ResponseEntity<?> addNewUser(@RequestBody AppUserDto appUserDto) {
        if (appUserService.verifySignupUsername(appUserDto)) {
            return new ResponseEntity<>("Username already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (appUserService.verifySignupEmail(appUserDto)) {
            return new ResponseEntity<>("Email already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (appUserService.verifySignupMobile(appUserDto)) {
            return new ResponseEntity<>("Mobile Number already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        appUserService.encryptPassword(appUserDto);
        appUserService.setRoleForUser(appUserDto);
        return new ResponseEntity<>(appUserService.addUser(appUserDto),HttpStatus.CREATED);
    }

    // ----------------------- SignIn ----------------------- //

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        if (appUserService.verifyLogin(loginDto)) {
            appUserService.generateOtp(loginDto);
            return new ResponseEntity<>("OTP sent to your mobile number", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid username/password", HttpStatus.FORBIDDEN);
        }
    }

    // --------------- OTP Generate And Verify --------------- //

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String mobileNum,
                                            @RequestParam String otp) {
        return new ResponseEntity<>(appUserService.validateOtp(mobileNum, otp), HttpStatus.OK);
    }

    // ------------------- GetUserDetails ------------------- //

    @GetMapping("/username")
    public ResponseEntity<AppUserDto> getByUsername(@RequestParam String username) {
       return new ResponseEntity<>(appUserService.findUsername(username), HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<AppUserDto> getByEmail(@RequestParam String email) {
        return new ResponseEntity<>(appUserService.findEmail(email), HttpStatus.OK);
    }

    @GetMapping("/all/data")
    public ResponseEntity<List<AppUserDto>> getAllInfo() {
        return new ResponseEntity<>(appUserService.getAllUserDetails(), HttpStatus.OK);
    }

    // ----------------------- Update ----------------------- //

    @PutMapping("/update/by-id/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id,
                                            @RequestBody AppUserDto appUserDto) {
        if (appUserService.findById(id)) {
            return new ResponseEntity<>(appUserService.updateUserById(id, appUserDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/by-username")
    public ResponseEntity<?> updateUserByUsername(@RequestParam String username,
                                                  @RequestBody AppUserDto appUserDto) {
        if (appUserService.findByUsername(username)) {
            return new ResponseEntity<>(appUserService.updateUserByUsername(username, appUserDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/by-id/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        if (appUserService.findById(id)) {
            appUserService.deleteUserById(id);
            return new ResponseEntity<>("Delete Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/by-username")
    public ResponseEntity<String> deleteUserByUsername(@RequestParam String username) {
        if (appUserService.findByUsername(username)) {
            appUserService.deleteUserByUsername(username);
            return new ResponseEntity<>("Delete Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}