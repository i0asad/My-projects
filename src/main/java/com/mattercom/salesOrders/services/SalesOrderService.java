package com.mattercom.salesOrders.services;

import com.mattercom.salesOrders.dto.ItemCreationDto;
import com.mattercom.salesOrders.dto.OrderCreationDto;
import com.mattercom.salesOrders.dto.OrderCreationFlags;
import com.mattercom.salesOrders.dto.RecurrentOrderDetailsDto;
import com.mattercom.salesOrders.dto.ShipmentAddressDto;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.enums.DeliverySpeed;
import com.mattercom.salesOrders.enums.SalesTransactions;

import java.util.List;
import java.util.UUID;

/**
 * Interface for the SalesOrderService.
 * This decouples the business logic implementation from the controllers,
 * allowing for easy testing and mocking.
 */
public interface SalesOrderService {

    UUID createOrder(OrderCreationDto orderCreationDto, OrderCreationFlags orderCreationFlags);

    SalesOrder performOrderTransaction(UUID salesOrderId, SalesTransactions transaction);

    List<SalesItem> performItemTransaction(List<UUID> salesItemIds, SalesTransactions transaction, UUID salesOrderId);

    SalesOrder ChangeShippingAddress(UUID salesOrderId, ShipmentAddressDto shipmentAddressDto, Boolean systemChange);

    List<SalesItem> backOrderItem(List<UUID> salesItemIds, UUID salesOrderId, Integer backOrderedQuantity);

    SalesOrder changeRecurrentDetails(UUID salesOrderId, RecurrentOrderDetailsDto recurrentOrderDetailsDto, Boolean systemChange);

    SalesOrder CancelOrder(UUID salesOrderId, Boolean systemCancel);

    List<SalesItem> cancelOrderItems(List<UUID> salesItemIds, UUID salesOrderId, Boolean systemCancel);

    SalesOrder changeDeliverySpeed(DeliverySpeed deliverySpeed, UUID salesOrderId);

    SalesOrder addOrderItems(UUID salesOrderId, List<ItemCreationDto> itemsToAdd, Boolean systemChange);

    SalesOrder restartOrder(UUID salesOrderId, Boolean regenerateInvoice);

    SalesOrder findOrderWithDetailsById(UUID salesOrderId);

    // This was the stray line, now removed.
    SalesOrder restartDisputedOrder(UUID salesOrderId, Boolean regenerateInvoice);
}