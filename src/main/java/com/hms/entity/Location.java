package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "pin_code", nullable = false)
    private Integer pinCode;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City cityId;

    @OneToMany(mappedBy = "locationId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Hotels> hotels;

    @OneToMany(mappedBy = "locationId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}