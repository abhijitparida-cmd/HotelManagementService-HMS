package com.hms.repository;

import com.hms.entity.Hotels;
import com.hms.entity.Price;
import com.hms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Optional<Price> findByPriceOfRooms(BigDecimal priceOfRooms);

    Optional<Price> findByRoomIdAndHotelId(Room roomId, Hotels hotelId);
}