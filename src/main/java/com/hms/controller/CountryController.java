package com.hms.controller;

import com.hms.payload.CountryDto;
import com.hms.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/country")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/country-name")
    public ResponseEntity<?> addCountry(@RequestBody CountryDto countryDto) {
        if(countryService.verifyCountry(countryDto)) {
            return new ResponseEntity<>("Country Already Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(countryService.addCountryName(countryDto), HttpStatus.CREATED);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("get/all-data")
    public ResponseEntity<List<CountryDto>> getAllCountry() {
        return new ResponseEntity<>(countryService.getCountryName(), HttpStatus.OK);
    }

    // ----------------------- Update ----------------------- //

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCountryName(@PathVariable Long id,
                                            @RequestParam String updateCountry){
        if (countryService.verifyCountryId(id)) {
            return new ResponseEntity<>(countryService.updateCountryId(id,updateCountry), HttpStatus.OK);
        }
        return new ResponseEntity<>("Id of the country is not found", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateCountryName(@RequestParam String countryName,
                                            @RequestBody CountryDto countryDto){
        if(countryService.verifyCountryName(countryName)) {
            return new ResponseEntity<>(countryService.updateCountryName(countryName,countryDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Name of the country is not found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Long id) {
        try {
            countryService.deleteCountryById(id);
            return new ResponseEntity<>("Country deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/name")
    public ResponseEntity<?> deleteCountry(@RequestParam String countryName) {
        try {
            countryService.deleteCountryByName(countryName);
            return new ResponseEntity<>("Country deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
