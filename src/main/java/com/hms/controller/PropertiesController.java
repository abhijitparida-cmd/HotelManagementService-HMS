package com.hms.controller;

import com.hms.payload.PropertyDto;
import com.hms.service.PropertiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertiesController {

    private final PropertiesService propertiesService;

    public PropertiesController(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/add-properties")
    public ResponseEntity<?> addProperties(@RequestBody PropertyDto propertyDto) {
        if (!propertiesService.verifyDetails(propertyDto)) {
            return new ResponseEntity<>("Property details verification failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (propertiesService.verifyProperties(propertyDto)) {
            return new ResponseEntity<>("Property already exists in the database.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(propertiesService.addProperties(propertyDto), HttpStatus.CREATED);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<PropertyDto>> getAllData() {
        return new ResponseEntity<>(propertiesService.getAllProperties(), HttpStatus.OK);
    }


    // ----------------------- Update ----------------------- //



    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        if (propertiesService.verifyDeleteId(id)) {
            propertiesService.deleteProperty(id);
            return new ResponseEntity<>("File Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Search ----------------------- //

    @PostMapping("/search-hotels")
    public ResponseEntity<List<PropertyDto>> searchHotels(@RequestParam String name) {
        return new ResponseEntity<>(propertiesService.searchHotelByName(name), HttpStatus.OK);
    }
}
