package com.hms.repository;

import com.hms.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    @Query("SELECT ra FROM RoomAvailability ra JOIN ra.datesId d WHERE ra.property.id = :propertyId AND d.date_lists BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findRoomAvailabilityByPropertyAndDateRange(@Param("propertyId") Long propertyId,
                                                                      @Param("startDate") java.util.Date startDate,
                                                                      @Param("endDate") java.util.Date endDate);

    @Query("SELECT ra FROM RoomAvailability ra JOIN ra.datesId d WHERE ra.property.id = :propertyId AND d.date_lists = :date")
    List<RoomAvailability> findByPropertyAndDate(@Param("propertyId") Long propertyId, @Param("date") java.util.Date date);
}
