package com.hms.controller;

import com.hms.payload.LocationDto;
import com.hms.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/add-location")
    public ResponseEntity<?> addLocation(@RequestBody LocationDto locationDto) {
        if (locationService.verifyCity(locationDto)) {
            return new ResponseEntity<>("City Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (locationService.verifyPin(locationDto)) {
            if (locationService.verifyLocation(locationDto)) {
                return new ResponseEntity<>(locationService.addLocation(locationDto), HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Location Already Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("PinCode Already Exists", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<LocationDto>> getAllLocationList() {
        return new ResponseEntity<>(locationService.getAllLocations(), HttpStatus.OK);
    }
    // ----------------------- Update ----------------------- //

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id,
                                            @RequestBody LocationDto locationDto){
        if (locationService.verifyCity(locationDto)) {
            return new ResponseEntity<>("City Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (locationService.verifyLocationById(id)) {
            return new ResponseEntity<>(locationService.updateLocationId(id,locationDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Location Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateLocation(@RequestParam String locationName,
                                            @RequestBody LocationDto locationDto) {
        if (locationService.verifyCity(locationDto)) {
            return new ResponseEntity<>("City Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(locationService.verifyLocationByName(locationName)) {
            return new ResponseEntity<>(locationService.updateLocationName(locationName,locationDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Location Not Found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocationById(id);
            return new ResponseEntity<>("Location deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/name")
    public ResponseEntity<?> deleteLocation(@RequestParam String locationName) {
        try {
            locationService.deleteLocationByName(locationName);
            return new ResponseEntity<>("Location deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
