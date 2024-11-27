package com.hms.repository;

import com.hms.entity.Hotels;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelsRepository extends JpaRepository<Hotels, Long> {

    Optional<Hotels> findByHotelName(String hotelName);
}