package com.hms.service;

import com.hms.entity.Location;
import com.hms.payload.LocationDto;
import com.hms.repository.CityRepository;
import com.hms.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final CityRepository cityRepository;

    // --------------------- Constructor -------------------- //

    public LocationService(LocationRepository locationRepository, CityRepository cityRepository) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
    }

    // ---------------------- Convert ----------------------- //

    public Location convertDtoToEntity(LocationDto locationDto) {
        Location location = new Location();
        location.setLocationName(locationDto.getLocationName());
        location.setPinCode(locationDto.getPinCode());
        cityRepository.findByCityName(locationDto.getCityName()).ifPresent(location::setCityId);
        return location;
    }

    public LocationDto convertEntityToDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLocationName(location.getLocationName());
        locationDto.setPinCode(location.getPinCode());
        locationDto.setCityName(location.getCityId().getCityName());
        return locationDto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyCity(LocationDto locationDto) {
        return cityRepository.findByCityName(locationDto.getCityName()).isEmpty();
    }

    public boolean verifyPin(LocationDto locationDto) {
        return locationRepository.findByPinCode(locationDto.getPinCode()).isEmpty();
    }

    public boolean verifyLocation(LocationDto locationDto) {
        return locationRepository.findByLocationName(locationDto.getLocationName()).isEmpty();
    }

    public LocationDto addLocation(LocationDto locationDto) {
        return convertEntityToDto(locationRepository.save(convertDtoToEntity(locationDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // ----------------------- Finder ----------------------- //

    public boolean verifyLocationById(Long id) {
        return locationRepository.findById(id).isPresent();
    }

    public boolean verifyLocationByName(String locationName) {
        return locationRepository.findByLocationName(locationName).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public LocationDto updateLocationId(Long id, LocationDto locationDto) {
        Location location = locationRepository.findById(id).get();
        location.setPinCode(locationDto.getPinCode());
        location.setLocationName(locationDto.getLocationName());
        cityRepository.findByCityName(locationDto.getCityName()).ifPresent(location::setCityId);
        return convertEntityToDto(locationRepository.save(location));
    }

    public LocationDto updateLocationName(String locationName, LocationDto locationDto) {
        Location location = locationRepository.findByLocationName(locationName).get();
        location.setPinCode(locationDto.getPinCode());
        location.setLocationName(locationDto.getLocationName());
        cityRepository.findByCityName(locationDto.getCityName()).ifPresent(location::setCityId);
        return convertEntityToDto(locationRepository.save(location));
    }

    // ----------------------- Delete ----------------------- //

    @Transactional
    public void deleteLocationById(Long id) {
        if (locationRepository.findById(id).isPresent()) {
            locationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Location with ID " + id + " does not exist.");
        }
    }

    @Transactional
    public void deleteLocationByName(String locationName) {
        if (locationRepository.findByLocationName(locationName).isPresent()) {
            locationRepository.deleteById(locationRepository.findByLocationName(locationName).get().getId());
        } else {
            throw new RuntimeException("Location with name ( " + locationName + " ) does not exist.");
        }
    }
}
