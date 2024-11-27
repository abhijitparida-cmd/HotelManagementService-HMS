package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "hotels")
public class Hotels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hotel_name", nullable = false)
    private String hotelName;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locationId;

    @OneToMany(mappedBy = "hotelId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Price> prices;

    @OneToMany(mappedBy = "hotelId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}