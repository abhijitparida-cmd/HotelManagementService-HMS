package com.hms.controller;

import com.hms.payload.RoomAvailabilityDto;
import com.hms.service.RoomAvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room-availability")
public class RoomAvailabilityController {

    private final RoomAvailabilityService roomAvailabilityService;

    public RoomAvailabilityController(RoomAvailabilityService roomAvailabilityService) {
        this.roomAvailabilityService = roomAvailabilityService;
    }

    // --------------------------- Create --------------------------- //

    @PostMapping("/add")
    public ResponseEntity<?> addRoomAvailability(@RequestBody RoomAvailabilityDto roomAvailabilityDto,
                                                 @RequestParam Long propertyId) {
        if (roomAvailabilityService.verifyPropertyId(propertyId)) {
            return new ResponseEntity<>("property id is not available", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (roomAvailabilityService.verifyAvailableDateOnPropertyId(roomAvailabilityDto, propertyId)) {
            return new ResponseEntity<>(roomAvailabilityService.addRoomAvailability(roomAvailabilityDto, propertyId), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Room availability already exists on this date", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --------------------------- Check --------------------------- //

    @GetMapping("/check-availability")
    public ResponseEntity<?> checkRoomAvailability(@RequestParam Long propertyId,
                                                   @RequestParam String checkInDate,
                                                   @RequestParam String checkOutDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date checkIn = sdf.parse(checkInDate);
        Date checkOut = sdf.parse(checkOutDate);

        if (roomAvailabilityService.verifyPropertyId(propertyId)) {
            return new ResponseEntity<>("No room availability data found for this property", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(roomAvailabilityService.checkRoomAvailableInDateBetween(propertyId, checkIn, checkOut), HttpStatus.OK);
    }

    // -------------------------- Read --------------------------- //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<RoomAvailabilityDto>> getAllListsOfRooms() {
        return new ResponseEntity<>(roomAvailabilityService.getAllListsOfRooms(), HttpStatus.OK);
    }
}
