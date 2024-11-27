package com.hms.controller;


import com.hms.payload.RoomDto;
import com.hms.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // -------------------------- Create -------------------------- //

    @PostMapping("/add-room")
    public ResponseEntity<?> addRoom(@RequestBody RoomDto roomDto) {
        if (roomService.verifyRoom(roomDto)) {
            return new ResponseEntity<>("Room already exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(roomService.createNewRoom(roomDto), HttpStatus.CREATED);
    }

    // -------------------------- Read --------------------------- //

    @GetMapping("/get/all-data")
    public ResponseEntity<Iterable<RoomDto>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllRoomList(), HttpStatus.OK);
    }
}
