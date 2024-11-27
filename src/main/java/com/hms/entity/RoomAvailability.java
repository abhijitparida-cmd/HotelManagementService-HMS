package com.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room_availability")
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms;

    @Version
    private long versions;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "dates_id")
    private Dates datesId;

}
