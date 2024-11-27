package com.hms.service;

import com.hms.entity.AppUser;
import com.hms.payload.AppUserDto;
import com.hms.payload.LoginDto;
import com.hms.payload.TokenDto;
import com.hms.repository.AppUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;
    private final OTPService otpService;

    public AppUserService(AppUserRepository appUserRepository,
                          ModelMapper modelMapper, JWTService jwtService,
                          OTPService otpService) {
        this.appUserRepository = appUserRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.otpService = otpService;
    }

    // ---------------------- Mapping ----------------------- //

    AppUser mapToEntity(AppUserDto appUserDto) {
        return modelMapper.map(appUserDto, AppUser.class);
    }
    AppUserDto mapToDto(AppUser appUser) {
        return modelMapper.map(appUser, AppUserDto.class);
    }

    // ----------------------- SignUp ----------------------- //

    public boolean verifySignupUsername(AppUserDto appUserDto) {
        return appUserRepository.findByUsername(appUserDto.getUsername()).isPresent();
    }
    public boolean verifySignupEmail(AppUserDto appUserDto) {
        return appUserRepository.findByEmail(appUserDto.getEmail()).isPresent();
    }

    public boolean verifySignupMobile(AppUserDto appUserDto) {
        return appUserRepository.findByMobileNum(appUserDto.getMobileNum()).isPresent();
    }

    public void encryptPassword(AppUserDto appUserDto) {
        appUserDto.setPassword(BCrypt.hashpw(appUserDto.getPassword(), BCrypt.gensalt(5)));
    }

    public void setRoleForOwner(AppUserDto appUserDto) {
        appUserDto.setRole("ROLE_OWNER");
    }
    public void setRoleForUser(AppUserDto appUserDto) {
        appUserDto.setRole("ROLE_USER");
    }
    public AppUserDto addUser(AppUserDto appUserDto) {
        return mapToDto(appUserRepository.save(mapToEntity(appUserDto)));
    }

    // ----------------------- SignIn ----------------------- //

    public boolean verifyLogin(LoginDto loginDto) {
        try {
            Optional<AppUser> opUsername = appUserRepository.findByUsername(loginDto.getUsername());
            if (opUsername.isPresent()) {
                AppUser appUser = opUsername.get();
                return BCrypt.checkpw(loginDto.getPassword(), appUser.getPassword());
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void generateOtp(LoginDto loginDto) {
        Optional<AppUser> opAppUser = appUserRepository.findByUsername(loginDto.getUsername());
        if (opAppUser.isPresent()) {
            AppUser appUser = opAppUser.get();
            otpService.generateAndSendOTP(appUser.getMobileNum());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    // --------------- OTP Generate And Verify --------------- //

    public Object validateOtp(String mobileNum, String otp) {
        Optional<AppUser> byMobileNum = appUserRepository.findByMobileNum(mobileNum);
        if (byMobileNum.isPresent()) {
            AppUser appUser = byMobileNum.get();
            if (otpService.validateOTP(mobileNum, otp)) {
                return tokenNumber(jwtService.generateToken(appUser.getUsername()));
            }
            return "Invalid OTP";
        }
        return "invalid mobile number format";
    }

    public Object tokenNumber(String token) {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);
        tokenDto.setType("JWT");
        return tokenDto;
    }

    // ------------------- GetUserDetails ------------------- //

    public AppUserDto findUsername(String username) {
        return mapToDto(appUserRepository.findByUsername(username).get());
    }
    public AppUserDto findEmail(String email) {
        return mapToDto(appUserRepository.findByEmail(email).get());
    }
    public List<AppUserDto> getAllUserDetails() {
        return appUserRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ----------------------- Finder ----------------------- //

    public boolean findById(Long id) {
        return appUserRepository.findById(id).isPresent();
    }

    public boolean findByUsername(String username) {
        return appUserRepository.findByUsername(username).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public AppUserDto updateUserById(Long id, AppUserDto appUserDto) {
        AppUser appUser = appUserRepository.findById(id).get();
        if (appUserDto.getPassword()!= null &&!appUserDto.getPassword().isEmpty()) {
            encryptPassword(appUserDto);
            appUser.setPassword(appUserDto.getPassword());
        } else {
            appUser.setPassword(appUser.getPassword());
        }
        appUser.setName(appUserDto.getName());
        appUser.setUsername(appUserDto.getUsername());
        appUser.setEmail(appUserDto.getEmail());
        appUser.setMobileNum(appUserDto.getMobileNum());
        appUser.setRole(appUser.getRole());
        return mapToDto(appUserRepository.save(appUser));
    }

    public AppUserDto updateUserByUsername(String username, AppUserDto appUserDto) {
        AppUser appUser = appUserRepository.findByUsername(username).get();
        if (appUserDto.getPassword()!= null &&!appUserDto.getPassword().isEmpty()) {
            encryptPassword(appUserDto);
            appUser.setPassword(appUserDto.getPassword());
        } else {
            appUser.setPassword(appUser.getPassword());
        }
        appUser.setName(appUserDto.getName());
        appUser.setUsername(appUserDto.getUsername());
        appUser.setEmail(appUserDto.getEmail());
        appUser.setMobileNum(appUserDto.getMobileNum());
        appUser.setRole(appUser.getRole());
        return mapToDto(appUserRepository.save(appUser));
    }

    // ----------------------- Delete ----------------------- //

    public void deleteUserById(Long id) {
        appUserRepository.deleteById(appUserRepository.findById(id).get().getId());
    }

    public void deleteUserByUsername(String username) {
        appUserRepository.deleteById(appUserRepository.findByUsername(username).get().getId());
    }
}
