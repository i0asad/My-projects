package com.mattercom.salesOrders.entities;


import com.mattercom.salesOrders.entities.status.SalesItemStatus;
import jakarta.persistence.*;
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
@Entity
@Table(name = "order_items")
public class SalesItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", updatable = false)
    private UUID itemId;

    @NotNull
    @Column(name = "vendor_id")
    private UUID vendorId;

    @NotNull
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @NotNull
    @NotBlank
    @Column(name = "product_name", updatable = false, nullable = false)
    private String productName; //Keeps the name of product at the time of ordering



    @Builder.Default
    private Boolean transitActive=false;

    @PositiveOrZero
    @Column(nullable = false, updatable = false)
    private Integer netQty;

    @Column(nullable = false, updatable = false)
    private BigDecimal baseUnitPrice;

    @Column(nullable = false, updatable = false)
    private BigDecimal discountRate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder salesOrder;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "salesItem", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SalesItemStatus> statusList;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true, mappedBy = "salesItem")
    private BackOrderedItem backOrderedItem;
}


