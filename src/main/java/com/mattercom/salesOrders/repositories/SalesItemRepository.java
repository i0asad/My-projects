package com.mattercom.salesOrders.repositories;

import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.enums.ItemStatusId;
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

    @Query("""
  SELECT si FROM SalesItem si
  WHERE si.salesOrder.salesOrderId = :salesOrderId
    AND NOT EXISTS (
      SELECT 1 FROM SalesItemStatus ist
      WHERE ist.salesItem = si
        AND ist.itemStatusId IN :statuses
        AND ist.active = true
    )
""")
    List<SalesItem> findItemsNotHavingGivenStatusesByOrderId(
            @Param("salesOrderId") UUID salesOrderId,
            @Param("statuses") List<ItemStatusId> statuses
    );

    @Query("""
      SELECT COUNT(si) FROM SalesItem si
      WHERE si.salesOrder.salesOrderId = :salesOrderId
        AND NOT EXISTS (
          SELECT 1 FROM SalesItemStatus ist
          WHERE ist.salesItem = si
            AND ist.itemStatusId IN :statuses
            AND ist.active = true
        )
    """)
    long countItemsNotHavingGivenStatusesByOrderId(
            @Param("salesOrderId") UUID salesOrderId,
            @Param("statuses") List<com.mattercom.salesOrders.enums.ItemStatusId> statuses
    );



}


