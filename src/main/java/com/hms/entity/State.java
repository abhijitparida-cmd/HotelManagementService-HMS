package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country countryId;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<City> cities;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}