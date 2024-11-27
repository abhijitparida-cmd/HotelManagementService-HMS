package com.hms.service;

import com.hms.entity.*;
import com.hms.payload.*;
import com.hms.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertiesService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final HotelsRepository hotelsRepository;
    private final RoomRepository roomsRepository;
    private final PriceRepository priceRepository;
    private final PropertyRepository propertyRepository;

    // --------------------- Constructor -------------------- //

    public PropertiesService(PropertyRepository propertyRepository, CityRepository cityRepository,
                             CountryRepository countryRepository, StateRepository stateRepository,
                             HotelsRepository hotelsRepository, LocationRepository locationRepository,
                             RoomRepository roomsRepository, PriceRepository priceRepository) {
        this.propertyRepository = propertyRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.hotelsRepository = hotelsRepository;
        this.locationRepository = locationRepository;
        this.roomsRepository = roomsRepository;
        this.priceRepository = priceRepository;
    }

    // --------------------- Converting --------------------- //

    public Property convertDtoToEntity(PropertyDto propertyDto) {
        Property property = new Property();
        property.setNoOfGuests(propertyDto.getNoOfGuests());
        property.setNoOfBedrooms(propertyDto.getNoOfBedrooms());
        property.setNoOfBeds(propertyDto.getNoOfBeds());
        property.setNoOfBathrooms(propertyDto.getNoOfBathrooms());
        countryRepository.findByCountryName(propertyDto.getCountryName()).ifPresent(property::setCountryId);
        stateRepository.findByStateName(propertyDto.getStateName()).ifPresent(property::setStateId);
        cityRepository.findByCityName(propertyDto.getCityName()).ifPresent(property::setCityId);
        locationRepository.findByLocationName(propertyDto.getLocationName()).ifPresent(property::setLocationId);
        property.setPinCode(locationRepository.findByPinCode(propertyDto.getPinCode()).get().getPinCode());
        hotelsRepository.findByHotelName(propertyDto.getHotelName()).ifPresent(property::setHotelId);
        roomsRepository.findByRoomTypes(propertyDto.getRoomTypes()).ifPresent(property::setRoomId);
        priceRepository.findByPriceOfRooms(propertyDto.getPriceOfRooms()).ifPresent(property::setPriceId);
        return property;
    }

    public PropertyDto convertEntityToDto(Property property) {
        PropertyDto dto = new PropertyDto();
        dto.setNoOfGuests(property.getNoOfGuests());
        dto.setNoOfBedrooms(property.getNoOfBedrooms());
        dto.setNoOfBeds(property.getNoOfBeds());
        dto.setNoOfBathrooms(property.getNoOfBathrooms());
        dto.setCountryName(property.getCountryId().getCountryName());
        dto.setStateName(property.getStateId().getStateName());
        dto.setCityName(property.getCityId().getCityName());
        dto.setLocationName(property.getLocationId().getLocationName());
        dto.setPinCode(property.getLocationId().getPinCode());
        dto.setHotelName(property.getHotelId().getHotelName());
        dto.setRoomTypes(property.getRoomId().getRoomTypes());
        dto.setPriceOfRooms(property.getPriceId().getPriceOfRooms());
        return dto;
    }

    // ----------------------- Verify ----------------------- //

    public boolean verifyDetails(PropertyDto propertyDto) {
        Optional<Price> priceOpt = priceRepository.findByPriceOfRooms(propertyDto.getPriceOfRooms());
        if (priceOpt.isEmpty()) {
            return false;
        }
        Price price = priceOpt.get();
        if (!price.getRoomId().getRoomTypes().equals(propertyDto.getRoomTypes()) ||
                !price.getHotelId().getHotelName().equals(propertyDto.getHotelName())) {
            return false;
        }
        Optional<Location> locationOpt = locationRepository.findByLocationName(propertyDto.getLocationName());
        if (locationOpt.isEmpty()) {
            return false;
        }
        Location location = locationOpt.get();
        if (!location.getPinCode().equals(propertyDto.getPinCode()) ||
                !location.getCityId().getCityName().equals(propertyDto.getCityName())) {
            return false;
        }
        Optional<City> cityOpt = cityRepository.findByCityName(location.getCityId().getCityName());
        if (cityOpt.isEmpty()) {
            return false;
        }
        City city = cityOpt.get();
        if (!city.getStateId().getStateName().equals(propertyDto.getStateName())) {
            return false;
        }
        Optional<State> stateOpt = stateRepository.findByStateName(city.getStateId().getStateName());
        if (stateOpt.isEmpty()) {
            return false;
        }
        State state = stateOpt.get();
        return state.getCountryId().getCountryName().equals(propertyDto.getCountryName());
    }

    public boolean verifyProperties(PropertyDto propertyDto) {
        return propertyRepository.existsByNoOfGuestsAndNoOfBedroomsAndNoOfBedsAndNoOfBathroomsAndCityId_IdAndCountryId_IdAndStateId_IdAndLocationId_IdAndPinCodeAndHotelId_IdAndRoomId_IdAndPriceId_Id(
                propertyDto.getNoOfGuests(),
                propertyDto.getNoOfBedrooms(),
                propertyDto.getNoOfBeds(),
                propertyDto.getNoOfBathrooms(),
                cityRepository.findByCityName(propertyDto.getCityName()).map(City::getId).orElse(-1L),
                countryRepository.findByCountryName(propertyDto.getCountryName()).map(Country::getId).orElse(-1L),
                stateRepository.findByStateName(propertyDto.getStateName()).map(State::getId).orElse(-1L),
                locationRepository.findByLocationName(propertyDto.getLocationName()).map(Location::getId).orElse(-1L),
                String.valueOf(propertyDto.getPinCode()),
                hotelsRepository.findByHotelName(propertyDto.getHotelName()).map(Hotels::getId).orElse(-1L),
                roomsRepository.findByRoomTypes(propertyDto.getRoomTypes()).map(Room::getId).orElse(-1L),
                priceRepository.findByPriceOfRooms(propertyDto.getPriceOfRooms()).map(Price::getId).orElse(-1L));
    }

    // ----------------------- Create ----------------------- //

    public PropertyDto addProperties (PropertyDto propertyDto) {
        return convertEntityToDto(propertyRepository.save(convertDtoToEntity(propertyDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<PropertyDto> getAllProperties() {
        return propertyRepository.findAll().stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // ----------------------- Update ----------------------- //

    // ----------------------- Delete ----------------------- //

    public boolean verifyDeleteId(Long id) {
        return propertyRepository.findById(id).isPresent();
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    // ----------------------- Search ----------------------- //

    public List<PropertyDto> searchHotelByName(String name) {
        return propertyRepository.searchHotels(name).stream().map(this::convertEntityToDto).toList();
    }
}