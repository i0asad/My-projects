package com.mattercom.salesOrders.dto;

import com.mattercom.salesOrders.entities.SalesOrder;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentAddressDto {

    private String recipientName;

    private String companyName;
    private String phoneNumber;
    private String streetLine1;
    private String streetLine2;
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String country;

    private String landmark;


}
