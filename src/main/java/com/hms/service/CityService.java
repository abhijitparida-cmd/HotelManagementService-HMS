package com.hms.service;

import com.hms.entity.City;
import com.hms.payload.CityDto;
import com.hms.repository.CityRepository;
import com.hms.repository.StateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    // --------------------- Constructor -------------------- //

    public CityService(CityRepository cityRepository, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    // ---------------------- Convert ----------------------- //

    public City convertDtoToEntity (CityDto cityDto) {
        City city = new City();
        city.setCityName(cityDto.getCityName());
        stateRepository.findByStateName(cityDto.getStateName()).ifPresent(city::setStateId);
        return city;
    }

    public CityDto convertEntityToDto(City city) {
        CityDto cityDto = new CityDto();
        cityDto.setCityName(city.getCityName());
        cityDto.setStateName(city.getStateId().getStateName());
        return cityDto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyState(CityDto cityDto) {
        return stateRepository.findByStateName(cityDto.getStateName()).isEmpty();
    }

    public boolean verifyCity(CityDto cityDto) {
        return cityRepository.findByCityName(cityDto.getCityName()).isEmpty();
    }

    public CityDto addCityName(CityDto cityDto) {
        return convertEntityToDto(cityRepository.save(convertDtoToEntity(cityDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<CityDto> getCityName() {
        return cityRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // ----------------------- Finder ----------------------- //

    public boolean verifyCityId(Long id) {
        return cityRepository.findById(id).isPresent();
    }

    public boolean verifyCityName(String cityName) {
        return cityRepository.findByCityName(cityName).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public CityDto updateCityId(Long id, CityDto cityDto) {
        City city = cityRepository.findById(id).get();
        city.setCityName(cityDto.getCityName());
        city.setStateId(stateRepository.findByStateName(cityDto.getStateName()).get());
        return convertEntityToDto(cityRepository.save(city));
    }

    public CityDto updateCityName(String cityName, CityDto cityDto) {
        City city = cityRepository.findByCityName(cityName).get();
        city.setCityName(cityDto.getCityName());
        city.setStateId(stateRepository.findByStateName(cityDto.getStateName()).get());
        return convertEntityToDto(cityRepository.save(city));
    }

    // ----------------------- Delete ----------------------- //

    public void deleteCityById(Long id) {
        if (cityRepository.findById(id).isPresent()) {
            cityRepository.deleteById(id);
        } else {
            throw new RuntimeException("City with ID " + id + " does not exist.");
        }
    }

    public void deleteCityByName(String cityName) {
        if (cityRepository.findByCityName(cityName).isPresent()) {
            cityRepository.deleteById(cityRepository.findByCityName(cityName).get().getId());
        } else {
            throw new RuntimeException("City with name ( " + cityName + " ) does not exist.");
        }
    }
}
