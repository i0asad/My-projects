package com.mattercom.salesOrders.dto;



import com.mattercom.salesOrders.entities.BackOrderedItem;
import com.mattercom.salesOrders.entities.status.SalesItemStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreationDto
{

    @NotNull
    private UUID vendorId;

    @NotNull
    private UUID productId;

    @NotBlank
    @NotNull
    private String productName;

    @NotNull
    @Positive
    private Integer netQty;

    @NotNull
    @Positive
    private BigDecimal baseUnitPrice;

    @Builder.Default
    @PositiveOrZero
    private BigDecimal discountRate=BigDecimal.ZERO;


    private List<SalesItemStatus> statusList;

    private BackOrderedItem backOrderedItem;

}



