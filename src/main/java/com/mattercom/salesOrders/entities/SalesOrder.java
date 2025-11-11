package com.mattercom.salesOrders.entities;



import com.mattercom.salesOrders.entities.status.SalesOrderStatus;
import com.mattercom.salesOrders.enums.DeliverySpeed;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;



import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrder {



    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false)
    private UUID salesOrderId;

    @NotNull
    @Column(nullable = false, updatable = false, name = "customer_id")
    private UUID customerId;

    @NotNull
    @NotBlank
    @Column(name = "customer_name",nullable = false)
    private String customerName; //Keeps the name of customer at the time of ordering



    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL , mappedBy = "salesOrder", orphanRemoval = true)
    private List<SalesItem> salesItems;

    @OneToOne( cascade = CascadeType.ALL, mappedBy = "salesOrder", orphanRemoval=true, optional = false)
    private ShipmentAddress shipmentAddress;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "salesOrder", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SalesOrderStatus> statusList;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Boolean recurrent=false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean inTransit=false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private DeliverySpeed deliverySpeed=DeliverySpeed.NORMAL;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "salesOrder")
    private RecurrentOrderDetails recurrentOrderDetails;
    


}
