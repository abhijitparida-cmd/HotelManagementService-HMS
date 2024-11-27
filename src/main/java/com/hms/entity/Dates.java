package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "dates")
public class Dates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_lists", nullable = false)
    private Date date_lists;

    @OneToMany(mappedBy = "datesId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RoomAvailability> roomAvailabilities;
}