package com.hms.service;

import com.hms.entity.State;
import com.hms.payload.StateDto;
import com.hms.repository.CountryRepository;
import com.hms.repository.StateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateService {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    // -------------------- Constructor --------------------- //

    public StateService(StateRepository stateRepository, CountryRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    // ---------------------- Convert ----------------------- //

    public State convertDtoToEntity(StateDto stateDto) {
        State state = new State();
        state.setStateName(stateDto.getStateName());
        countryRepository.findByCountryName(stateDto.getCountryName()).ifPresent(state::setCountryId);
        return state;
    }

    public StateDto convertEntityToDto(State state) {
        StateDto stateDto = new StateDto();
        stateDto.setStateName(state.getStateName());
        stateDto.setCountryName(state.getCountryId().getCountryName());
        return stateDto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyCountry(StateDto stateDto) {
        return countryRepository.findByCountryName(stateDto.getCountryName()).isEmpty();
    }

    public boolean verifyState(StateDto stateDto) {
        return stateRepository.findByStateName(stateDto.getStateName()).isEmpty();
    }

    public StateDto addStateName(StateDto stateDto) {
        return convertEntityToDto(stateRepository.save(convertDtoToEntity(stateDto)));
    }

    // ------------------------ Read ------------------------ //

    public List<StateDto> getStateName() {
        return stateRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // ----------------------- Verify ----------------------- //

    public boolean verifyStateId(Long id) {
        return stateRepository.findById(id).isPresent();
    }

    public boolean verifyStateName(String stateName) {
        return stateRepository.findByStateName(stateName).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public StateDto updateStateId(Long id, StateDto stateDto) {
        State state = stateRepository.findById(id).get();
        state.setStateName(stateDto.getStateName());
        state.setCountryId(countryRepository.findByCountryName(stateDto.getCountryName()).get());
        return convertEntityToDto(stateRepository.save(state));
    }

    public StateDto updateStateName(String stateName, StateDto stateDto) {
        State state = stateRepository.findByStateName(stateName).get();
        state.setStateName(stateDto.getStateName());
        state.setCountryId(countryRepository.findByCountryName(stateDto.getCountryName()).get());
        return convertEntityToDto(stateRepository.save(state));
    }

    // ----------------------- Delete ----------------------- //

    public void deleteStateById(Long id) {
        if (stateRepository.findById(id).isPresent()) {
            stateRepository.deleteById(id);
        } else {
            throw new RuntimeException("State with ID " + id + " does not exist.");
        }
    }

    public void deleteStateByName(String stateName) {
        if (stateRepository.findByStateName(stateName).isPresent()) {
            stateRepository.deleteById(stateRepository.findByStateName(stateName).get().getId());
        } else {
            throw new RuntimeException("State with name ( " + stateName + " ) does not exist.");
        }
    }
}
