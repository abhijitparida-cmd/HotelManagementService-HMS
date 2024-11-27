package com.hms.repository;

import com.hms.entity.AppUser;
import com.hms.entity.Bookings;
import com.hms.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BookingsRepository extends JpaRepository<Bookings, Long> {

    Optional<Bookings> findByBookingCode(String bookingCode);
}