package com.mattercom.salesOrders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RecurrentOrderDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recurrentOrderId;

    @Positive
    private Integer installments;

    @Positive
    private Integer gapInDays;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private SalesOrder salesOrder;

    @PositiveOrZero
    private Integer requestedOffsetInDays;
}
