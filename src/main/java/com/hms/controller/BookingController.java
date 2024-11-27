package com.hms.controller;

import com.hms.entity.AppUser;
import com.hms.payload.BookingDto;
import com.hms.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //------------------ Booking and PdfGenerate ------------------ //

    @PostMapping("/create-booking")
    public ResponseEntity<?> createBookingAndGeneratePdf(@AuthenticationPrincipal AppUser appUserId,
                                                         @RequestParam Long propertyId,
                                                         @RequestBody BookingDto bookingDto) throws IOException, URISyntaxException {
        if (!bookingService.verifyProperties(propertyId)) {
            return new ResponseEntity<>("Property not found", HttpStatus.NOT_FOUND);
        }
        if (!bookingService.RoomsAvailableCheck(propertyId, bookingDto)) {
            return new ResponseEntity<>("Rooms are not available for the selected dates", HttpStatus.BAD_REQUEST);
        }
        if (!bookingService.roomPerPersonCheck(bookingDto)) {
            return new ResponseEntity<>("Maximum 2 persons allowed per room", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bookingService.createBooking(propertyId, appUserId, bookingDto), HttpStatus.CREATED);
    }
}
