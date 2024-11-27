package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State stateId;

    @OneToMany(mappedBy = "cityId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Location> locations;

    @OneToMany(mappedBy = "cityId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}