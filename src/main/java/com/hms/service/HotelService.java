package com.hms.service;


import com.hms.entity.Hotels;
import com.hms.payload.HotelDto;
import com.hms.repository.HotelsRepository;
import com.hms.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelsRepository hotelsRepository;
    private final LocationRepository locationRepository;
    // --------------------- Constructor -------------------- //

    public HotelService(HotelsRepository hotelsRepository, LocationRepository locationRepository) {
        this.hotelsRepository = hotelsRepository;
        this.locationRepository = locationRepository;
    }

    // ---------------------- Convert ----------------------- //

    public Hotels convertDtoToEntity(HotelDto hotelDto) {
        Hotels hotel = new Hotels();
        hotel.setHotelName(hotelDto.getHotelName());
        locationRepository.findByLocationName(hotelDto.getLocationName()).ifPresent(hotel::setLocationId);
        return hotel;
    }

    public HotelDto convertEntityToDto(Hotels hotels) {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setHotelName(hotels.getHotelName());
        hotelDto.setLocationName(hotels.getLocationId().getLocationName());
        return hotelDto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyLocation(HotelDto hotelDto) {
        return locationRepository.findByLocationName(hotelDto.getLocationName()).isEmpty();
    }

    public boolean verifyHotel(HotelDto hotelDto) {
        return hotelsRepository.findByHotelName(hotelDto.getHotelName()).isEmpty();
    }
    public HotelDto addHotelName(HotelDto hotelDto) {
        return convertEntityToDto(hotelsRepository.save(convertDtoToEntity(hotelDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<HotelDto> getHotelName() {
        return hotelsRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // ----------------------- Finder ----------------------- //

    public boolean verifyHotelId(Long id) {
        return hotelsRepository.findById(id).isPresent();
    }

    public boolean verifyHotelName(String hotelName) {
        return hotelsRepository.findByHotelName(hotelName).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public HotelDto updateHotelId(Long id, HotelDto hotelDto) {
        Hotels hotels = hotelsRepository.findById(id).get();
        hotels.setHotelName(hotelDto.getHotelName());
        hotels.setLocationId(locationRepository.findByLocationName(hotelDto.getLocationName()).get());
        return convertEntityToDto(hotelsRepository.save(hotels));
    }

    public HotelDto updateHotelName(String hotelName, HotelDto hotelDto) {
        Hotels hotels = hotelsRepository.findByHotelName(hotelName).get();
        hotels.setHotelName(hotelDto.getHotelName());
        hotels.setLocationId(locationRepository.findByLocationName(hotelDto.getLocationName()).get());
        return convertEntityToDto(hotelsRepository.save(hotels));
    }

    // ----------------------- Delete ----------------------- //

    @Transactional
    public void deleteHotelById(Long id) {
        if (hotelsRepository.findById(id).isPresent()) {
            hotelsRepository.deleteById(id);
        } else {
            throw new RuntimeException("Hotel with ID " + id + " does not exist.");
        }
    }

    @Transactional
    public void deleteHotelByName(String hotelName) {
        if (hotelsRepository.findByHotelName(hotelName).isPresent()) {
            hotelsRepository.deleteById(hotelsRepository.findByHotelName(hotelName).get().getId());
        } else {
            throw new RuntimeException("City with name ( " + hotelName + " ) does not exist.");
        }
    }
}
