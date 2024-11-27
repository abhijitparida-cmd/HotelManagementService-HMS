package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "no_of_adults", nullable = false)
    private String noOfAdults;

    @Column(name = "no_of_child", nullable = false)
    private String noOfChildren;

    @Column(name = "no_of_rooms", nullable = false)
    private String noOfRooms;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "booking_date", nullable = false)
    private Date bookingDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "check_in", nullable = false)
    private Date checkIn;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "check_out", nullable = false)
    private Date checkOut;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "booking_code", unique = true, nullable = false)
    private String bookingCode;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUserId;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property propertyId;

}