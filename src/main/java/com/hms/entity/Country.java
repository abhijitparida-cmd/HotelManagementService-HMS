package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @OneToMany(mappedBy = "countryId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<State> states;

    @OneToMany(mappedBy = "countryId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}