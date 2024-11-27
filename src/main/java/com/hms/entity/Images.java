package com.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "images")
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "url", nullable = false, length = 2500)
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUserId;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property propertyId;
}