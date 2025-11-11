package com.mattercom.salesOrders.dto;

import com.mattercom.salesOrders.entities.status.SalesOrderStatus;
import com.mattercom.salesOrders.enums.DeliverySpeed;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationDto
{
    @NotNull
    private UUID customerId;

    @NotNull
    @NotBlank
    private String customerName;

    @NotEmpty
    private List<ItemCreationDto> dto;

    @NotNull
    private ShipmentAddressDto shipmentAddress;
    private List<SalesOrderStatus> statusList;
    @Builder.Default
    private DeliverySpeed deliverySpeed=DeliverySpeed.NORMAL;

    private RecurrentOrderDetailsDto recurrentOrderDetailsDto;

}
