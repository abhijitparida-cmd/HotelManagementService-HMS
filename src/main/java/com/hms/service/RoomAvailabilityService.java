package com.hms.service;

import com.hms.entity.RoomAvailability;
import com.hms.payload.RoomAvailabilityDto;
import com.hms.repository.DatesRepository;
import com.hms.repository.PropertyRepository;
import com.hms.repository.RoomAvailabilityRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoomAvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final PropertyRepository propertyRepository;
    private final DatesRepository datesRepository;
    private final PropertiesService propertiesService;

    // ---------------------- Constructor ---------------------- //

    public RoomAvailabilityService(RoomAvailabilityRepository roomAvailabilityRepository,
                                   PropertyRepository propertyRepository,
                                   DatesRepository datesRepository,
                                   PropertiesService propertiesService) {
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.propertyRepository = propertyRepository;
        this.datesRepository = datesRepository;
        this.propertiesService = propertiesService;
    }

    // ----------------------- Converter ----------------------- //

    public RoomAvailability convertDtoToEntity(RoomAvailabilityDto roomAvailabilityDto, Long propertyId) {
        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setAvailableRooms(roomAvailabilityDto.getAvailableRooms());
        roomAvailability.setDatesId(datesRepository.findByDateLists(roomAvailabilityDto.getDate()).orElseThrow(
                () -> new IllegalArgumentException("Date not found")));
        roomAvailability.setProperty(propertyRepository.findById(propertyId).orElseThrow(
                () -> new IllegalArgumentException("Property not found")));
        return roomAvailability;
    }

    public RoomAvailabilityDto convertEntityToDto(RoomAvailability roomAvailability) {
        RoomAvailabilityDto roomAvailabilityDto = new RoomAvailabilityDto();
        roomAvailabilityDto.setAvailableRooms(roomAvailability.getAvailableRooms());
        roomAvailabilityDto.setDate(roomAvailability.getDatesId().getDate_lists());
        roomAvailabilityDto.setPropertyDto(propertiesService.convertEntityToDto(roomAvailability.getProperty()));
        return roomAvailabilityDto;
    }

    // ------------------------ Create ------------------------- //

    public boolean verifyPropertyId(Long propertyId) {
        return propertyRepository.findById(propertyId).isEmpty();
    }

    public boolean verifyAvailableDateOnPropertyId(RoomAvailabilityDto roomAvailabilityDto, Long propertyId) {
        return roomAvailabilityRepository.findByPropertyAndDate(propertyId, roomAvailabilityDto.getDate()).isEmpty();
    }

    public RoomAvailabilityDto addRoomAvailability(RoomAvailabilityDto roomAvailabilityDto, Long propertyId) {
        RoomAvailability roomAvailability = convertDtoToEntity(roomAvailabilityDto, propertyId);
        return convertEntityToDto(roomAvailabilityRepository.save(roomAvailability));
    }

    // ------------------------ Check ------------------------- //

    public RoomAvailabilityDto checkRoomAvailableInDateBetween(Long propertyId, Date checkIn, Date checkOut) {
        List<RoomAvailability> roomAvailabilityList =
                roomAvailabilityRepository.findRoomAvailabilityByPropertyAndDateRange(propertyId, checkIn, checkOut);
        for (RoomAvailability roomAvailability : roomAvailabilityList) {
            if (roomAvailability.getAvailableRooms() > 0) {
                return convertEntityToDto(roomAvailability);
            }
        }
        return null;
    }

    // ------------------------- Read -------------------------- //

    public List<RoomAvailabilityDto> getAllListsOfRooms() {
        return roomAvailabilityRepository.findAll().stream().map(this::convertEntityToDto).toList();
    }
}
