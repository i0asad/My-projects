package com.mattercom.salesOrders.repositories;

import com.mattercom.salesOrders.entities.SalesItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesItemRepository extends JpaRepository<SalesItem, UUID>
{
    List<SalesItem> findAllBySalesOrder_SalesOrderId(UUID orderId);

    @EntityGraph(attributePaths = {
            "statusList",
            "backOrderedItem"
    })
    Optional<SalesItem> findWithDetailsByItemId(UUID itemId );

    @EntityGraph(attributePaths = {
            "statusList",
            "backOrderedItem"
    })
    List<SalesItem> findWithDetailsByItemIdInAndSalesOrder_SalesOrderId(List<UUID> itemIds, UUID orderOrderId);

    @Query("SELECT si FROM SalesItem si " +
            "WHERE si.salesOrder.salesOrderId = :orderId " +
            "AND NOT EXISTS ( " +
            "    SELECT 1 FROM SalesItemStatus ist " +
            "    WHERE ist.salesItem = si " +
            "    AND ist.itemStatusId IN (com.mattercom.salesOrders.enums.ItemStatusId.E_CNCL_CUST, " +
            "                         com.mattercom.salesOrders.enums.ItemStatusId.E_CNCL_SYS) " +
            "    AND ist.Active = true " +
            ")")
    List<SalesItem> findNonCancelledItemsByOrderId(@Param("salesOrderId") UUID orderId);

}


