package com.hms.controller;

import com.hms.payload.PriceDto;
import com.hms.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/price")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    // ----------------------- Create ----------------------- //

    @PostMapping("/add-price")
    public ResponseEntity<?> addPrices(@RequestBody PriceDto priceDto) {
        if (priceService.verifyHotels(priceDto)) {
            return new ResponseEntity<>("Hotel Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (priceService.verifyPricesOfRoom(priceDto)) {
            return new ResponseEntity<>(priceService.addNewPrice(priceDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Prices are not to be same, change", HttpStatus.CREATED);
    }

    // ------------------------ Read ------------------------ //

    @GetMapping("/get/all-data")
    public ResponseEntity<List<PriceDto>> getAllDetail() {
        return new ResponseEntity<>(priceService.getAllPriceDetails(), HttpStatus.OK);
    }

    // ----------------------- Update ----------------------- //

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrices(@PathVariable Long id,
                                          @RequestBody PriceDto priceDto) {
        if (priceService.verifyHotels(priceDto)) {
            return new ResponseEntity<>("Hotel Not Exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (priceService.verifyRoomTypesName(priceDto)) {
            return new ResponseEntity<>(priceService.updateNewPrices(id, priceDto), HttpStatus.OK);
        }
        return new ResponseEntity<>("Room Types Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ----------------------- Delete ----------------------- //

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrice(@PathVariable Long id) {
        if (priceService.priceById(id)) {
            priceService.deletePricesById(id);
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Room Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
