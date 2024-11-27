package com.hms.controller;


import com.hms.payload.HotelDto;
import com.hms.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/add-hotel")
    public ResponseEntity<?> addHotelName(@RequestBody HotelDto hotelDto) {
        if (hotelService.verifyLocation(hotelDto)) {
            return new ResponseEntity<>("Location Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (hotelService.verifyHotel(hotelDto)) {
            return new ResponseEntity<>(hotelService.addHotelName(hotelDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Hotel Already Exists", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<HotelDto>> getAllHotel() {
        return new ResponseEntity<>(hotelService.getHotelName(), HttpStatus.OK);
    }

    // ----------------------- Update ----------------------- //

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHotel(@PathVariable Long id,
                                         @RequestBody HotelDto hotelDto){
        if (hotelService.verifyHotelId(id)) {
            return new ResponseEntity<>(hotelService.updateHotelId(id,hotelDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Hotel Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateHotel(@RequestParam String hotelName,
                                         @RequestBody HotelDto hotelDto) {
        if(hotelService.verifyHotelName(hotelName)) {
            return new ResponseEntity<>(hotelService.updateHotelName(hotelName,hotelDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Hotel Not Found", HttpStatus.NOT_FOUND);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        try {
            hotelService.deleteHotelById(id);
            return new ResponseEntity<>("Hotel deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/name")
    public ResponseEntity<?> deleteHotel(@RequestParam String hotelName) {
        try {
            hotelService.deleteHotelByName(hotelName);
            return new ResponseEntity<>("Hotel deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}