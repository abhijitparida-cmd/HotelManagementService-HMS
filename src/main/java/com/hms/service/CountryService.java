package com.hms.service;

import com.hms.entity.Country;
import com.hms.payload.CountryDto;
import com.hms.repository.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;

    // --------------------- Constructor -------------------- //

    public CountryService(CountryRepository countryRepository, ModelMapper modelMapper) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
    }

    // ---------------------- Mapping ----------------------- //

    Country mapToEntity(CountryDto countryDto) {
        return modelMapper.map(countryDto, Country.class);
    }

    CountryDto mapToDto(Country country) {
        return modelMapper.map(country, CountryDto.class);
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyCountry(CountryDto countryDto) {
        return countryRepository.findByCountryName(countryDto.getCountryName()).isPresent();
    }

    public CountryDto addCountryName(CountryDto countryDto) {
        return mapToDto(countryRepository.save(mapToEntity(countryDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<CountryDto> getCountryName() {
        return countryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // ----------------------- Update ----------------------- //

    public boolean verifyCountryId(Long id) {
        return countryRepository.findById(id).isPresent();
    }

    public CountryDto updateCountryId(Long id, String updateCountry) {
        Country country = countryRepository.findById(id).get();
        country.setCountryName(updateCountry);
        return mapToDto(countryRepository.save(country));
    }

    public boolean verifyCountryName(String countryName) {
        return countryRepository.findByCountryName(countryName).isPresent();
    }

    public CountryDto updateCountryName(String countryName, CountryDto countryDto) {
        Country country = countryRepository.findByCountryName(countryName).get();
        country.setCountryName(countryDto.getCountryName());
        return mapToDto(countryRepository.save(country));
    }

    // ----------------------- Delete ----------------------- //

    public void deleteCountryById(Long id) {
        if (countryRepository.findById(id).isPresent()) {
            countryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Country with ID " + id + " does not exist.");
        }
    }

    public void deleteCountryByName(String countryName) {
        if (countryRepository.findByCountryName(countryName).isPresent()) {
            countryRepository.deleteById(countryRepository.findByCountryName(countryName).get().getId());
        } else {
            throw new RuntimeException("Country with name ( " + countryName + " ) does not exist.");
        }
    }
}