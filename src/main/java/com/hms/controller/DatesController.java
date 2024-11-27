package com.hms.controller;

import com.hms.payload.DatesDto;
import com.hms.service.DatesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dates")
public class DatesController {

    private final DatesService datesService;

    public DatesController(DatesService datesService) {
        this.datesService = datesService;
    }

    @PostMapping("/add-monthly-dates")
    public ResponseEntity<?> addMonthlyDates(@RequestParam int year, @RequestParam int month) {
        List<DatesDto> addedDates = datesService.addDates(year, month);
        if (addedDates.isEmpty()) {
            return new ResponseEntity<>("No new dates were added. All dates may already exist.", HttpStatus.OK);
        }
        return new ResponseEntity<>(addedDates, HttpStatus.CREATED);
    }

}
