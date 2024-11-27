package com.hms.controller;

import com.hms.payload.CityDto;
import com.hms.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/city-name")
    public ResponseEntity<?> addCity(@RequestBody CityDto cityDto) {
        if (cityService.verifyState(cityDto)) {
            return new ResponseEntity<>("State Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (cityService.verifyCity(cityDto)) {
            return new ResponseEntity<>(cityService.addCityName(cityDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("City Already Exists", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<CityDto>> getAllCity() {
        return new ResponseEntity<>(cityService.getCityName(), HttpStatus.OK);
    }

    // ----------------------- Update ----------------------- //

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCity(@PathVariable Long id,
                                        @RequestBody CityDto cityDto){
        if (cityService.verifyState(cityDto)) {
            return new ResponseEntity<>("State Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (cityService.verifyCityId(id)) {
            return new ResponseEntity<>(cityService.updateCityId(id,cityDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("City Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateCity(@RequestParam String cityName,
                                        @RequestBody CityDto cityDto) {
        if (cityService.verifyState(cityDto)) {
            return new ResponseEntity<>("State Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(cityService.verifyCityName(cityName)) {
            return new ResponseEntity<>(cityService.updateCityName(cityName,cityDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("City Not Found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        try {
            cityService.deleteCityById(id);
            return new ResponseEntity<>("City deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/name")
    public ResponseEntity<?> deleteCity(@RequestParam String cityName) {
        try {
            cityService.deleteCityByName(cityName);
            return new ResponseEntity<>("City deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
