package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "room_Types", nullable = false)
    private String roomTypes;

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Price> prices;

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Property> properties;
}