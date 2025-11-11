package com.mattercom.salesOrders.repositories;

import com.mattercom.salesOrders.entities.SalesOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    @EntityGraph(attributePaths =
            {
                    "statusList",
                    "recurrentOrderDetails",
                    "shipmentAddress",
                    "salesItems"
            })
    Optional<SalesOrder> findWithOrderDetailsBySalesOrderId(UUID salesOrderId);

    @EntityGraph(attributePaths = {"statusList"})
    Optional<SalesOrder> findWithStatusBySalesOrderId(UUID salesOrderId);



    @EntityGraph(attributePaths = {
            "statusList",
            "salesItems"
    })
    Optional<SalesOrder> findWithStatusAndItemsBySalesOrderId(UUID salesOrderId);


}


