package com.hms.repository;

import com.hms.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByStateName(String stateName);
}