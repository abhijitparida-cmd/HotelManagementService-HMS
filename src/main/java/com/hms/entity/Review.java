package com.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ratings", nullable = false)
    private Integer ratings;

    @Column(name = "descriptions")
    private String descriptions;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUserId;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property propertyId;
}