package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "price_of_rooms", nullable = false, precision = 10, scale = 2, unique = true)
    private BigDecimal priceOfRooms;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room roomId;

    @ManyToOne
    @JoinColumn(name = "hotels_id")
    private Hotels hotelId;

    @OneToMany(mappedBy = "priceId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}