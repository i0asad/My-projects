package com.mattercom.salesOrders.repositories;

import com.mattercom.salesOrders.entities.status.SalesOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalesOrderStatusRepository extends JpaRepository<SalesOrderStatus, Long>
{
    List<SalesOrderStatus> findAllBySalesOrder_SalesOrderId(UUID salesOrderSalesOrderId);
}
