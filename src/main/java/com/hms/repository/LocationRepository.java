package com.hms.repository;

import com.hms.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationName(String locationName);

    Optional<Location> findByPinCode(Integer pinCode);
}