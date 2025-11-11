package com.mattercom.salesOrders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackOrderedItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backOrderId;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_id", nullable = false)
    private SalesItem salesItem;

    @Positive
    @Column(nullable = false)
    private Integer bckQty;


}
