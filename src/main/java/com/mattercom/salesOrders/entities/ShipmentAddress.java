package com.mattercom.salesOrders.entities;


import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentAddress {

    @Id
    private UUID addressId;

    @Column(nullable = false)
    private String recipientName;

    private String companyName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String streetLine1;

    private String streetLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String stateOrProvince;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;


    private String landmark;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "order_id", updatable = false)
    private SalesOrder salesOrder ;
}