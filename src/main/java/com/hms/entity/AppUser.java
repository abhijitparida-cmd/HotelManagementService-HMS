package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "Mobile", nullable = false, length = 10, unique = true)
    private String mobileNum;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @OneToMany(mappedBy = "appUserId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    @OneToMany(mappedBy = "appUserId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Images> images;

    @OneToMany(mappedBy = "appUserId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Bookings> bookings;
}