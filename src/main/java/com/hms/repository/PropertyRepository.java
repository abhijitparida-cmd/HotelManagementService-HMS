package com.hms.repository;

import com.hms.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT p FROM Property p JOIN p.countryId co JOIN p.cityId c JOIN p.stateId s JOIN p.hotelId ho JOIN p.locationId l " +
            "WHERE co.countryName=:name or c.cityName=:name or s.stateName=:name or ho.hotelName=:name or l.locationName=:name")
    List<Property> searchHotels(@Param("name") String name);

    boolean existsByNoOfGuestsAndNoOfBedroomsAndNoOfBedsAndNoOfBathroomsAndCityId_IdAndCountryId_IdAndStateId_IdAndLocationId_IdAndPinCodeAndHotelId_IdAndRoomId_IdAndPriceId_Id(
            int noOfGuests, int noOfBedrooms, int noOfBeds, int noOfBathrooms, Long cityId, Long countryId, Long stateId, Long locationId, String pinCode, Long hotelId,
            Long roomId, Long priceId);
}