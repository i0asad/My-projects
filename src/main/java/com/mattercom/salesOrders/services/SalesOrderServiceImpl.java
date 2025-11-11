package com.mattercom.salesOrders.services;

import com.mattercom.salesOrders.dto.*;
import com.mattercom.salesOrders.entities.*;
import com.mattercom.salesOrders.entities.status.*;
import com.mattercom.salesOrders.enums.DeliverySpeed;
import com.mattercom.salesOrders.enums.ObjectType;
import com.mattercom.salesOrders.enums.SalesTransactions;
import com.mattercom.salesOrders.mapper.SalesOrderMapper;
import com.mattercom.salesOrders.repositories.*;
import com.mattercom.salesOrders.util.StatusManagementUtil;
import com.mattercom.salesOrders.util.StatusRuleConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // This handles the constructor injection
public class SalesOrderServiceImpl implements SalesOrderService

{
    // Repositories and Mappers, final for constructor injection
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesItemRepository salesItemRepository;
    private final SalesOrderStatusRepository salesOrderStatusRepository;

    @Transactional
    public UUID createOrder(OrderCreationDto orderCreationDto, OrderCreationFlags orderCreationFlags)
    {
        // Map the DTO to the entity
        SalesOrder newOrder = salesOrderMapper.toSalesOrder(orderCreationDto);

        // Set the bidirectional relationships so JPA saves them
        newOrder.getShipmentAddress().setSalesOrder(newOrder);
        if (newOrder.getRecurrentOrderDetails() != null)
        {
            newOrder.getRecurrentOrderDetails().setSalesOrder(newOrder);
        }
        newOrder.getSalesItems().forEach(item -> item.setSalesOrder(newOrder));

        // Get the initial status list based on flags (e.g., Credit Block)
        List<SalesOrderStatus> orderCreationStatuses = StatusManagementUtil.orderCreationListProvider(orderCreationFlags);
        newOrder.setStatusList(orderCreationStatuses);

        SalesOrder savedOrder = salesOrderRepository.save(newOrder);

        return savedOrder.getSalesOrderId();

    }

    @Transactional
    public SalesOrder performOrderTransaction(UUID salesOrderId, SalesTransactions transaction)
    {
       // My gate to prevent this from being used for complex transactions
        if (transaction.getObjectType() != ObjectType.ORDER||transaction == SalesTransactions.CANCEL_ORDER
                ||transaction==SalesTransactions.RESTART_ORDER ||transaction==SalesTransactions.RESTART_DISPUTED_ORDER)
        {
            throw new IllegalArgumentException("Invalid transaction type for Item: " + transaction);
        }

        // This is my main helper for all generic order state changes.
        SalesOrder salesOrder = getOrderWithStatus(salesOrderId);
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(), transaction, salesOrderId);
        List<SalesOrderStatus> newStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder, transaction);
        salesOrder.setStatusList(newStatusList);

        return salesOrderRepository.save(salesOrder);

    }

    @Transactional
    public List<SalesItem> performItemTransaction(List<UUID> salesItemIds, SalesTransactions transaction, UUID salesOrderId)
    {
        // My gate for complex item transactions
        if (transaction.getObjectType() != ObjectType.ITEM||transaction == SalesTransactions.BACKORDER_ITEM
                ||transaction == SalesTransactions.CANCEL_ITEM || transaction == SalesTransactions.SYSTEM_CANCEL_ITEM)
        {
            throw new IllegalArgumentException("Invalid transaction type for Item: " + transaction);
        }

        // Must check order-level rules first
        List<SalesOrderStatus> orderStatusList = getOrderStatus(salesOrderId);
        StatusRuleConstants.checkHeaderStatuses(orderStatusList, transaction, salesOrderId);

        List<SalesItem> salesItems = getOrderItems(salesItemIds, salesOrderId);

        for (SalesItem salesItem : salesItems)
        {
            StatusRuleConstants.checkItemStatuses(salesItem.getStatusList(), transaction, salesItem.getItemId());
            List<SalesItemStatus> newStatusList = StatusManagementUtil.applyItemStatusChange(salesItem, transaction);
            salesItem.setStatusList(newStatusList);

            // Special case: REORDER_ITEM must also remove the BackOrderedItem link
            if (transaction == SalesTransactions.REORDER_ITEM)
            {
                salesItem.setBackOrderedItem(null);
            }
        }
        return salesItemRepository.saveAll(salesItems);
    }

    @Transactional
    public SalesOrder ChangeShippingAddress(UUID salesOrderId, ShipmentAddressDto shipmentAddressDto, Boolean systemChange)
    {
        SalesOrder salesOrder = getOrderWithStatus(salesOrderId);

        // Use my standard permission check for updates
        checkUpdatePermission(salesOrder, systemChange);

        ShipmentAddress shipmentAddress = salesOrderMapper.toShipmentAddress(shipmentAddressDto);
        salesOrder.setShipmentAddress(shipmentAddress);
        shipmentAddress.setSalesOrder(salesOrder); // Set the owning side

        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public List<SalesItem> backOrderItem(List<UUID> salesItemIds, UUID salesOrderId, Integer backOrderedQuantity)
    {
        // Must check order-level rules first
        List<SalesOrderStatus> orderStatusList = getOrderStatus(salesOrderId);
        StatusRuleConstants.checkHeaderStatuses(orderStatusList, SalesTransactions.BACKORDER_ITEM, salesOrderId);

        List<SalesItem> salesItems = getOrderItems(salesItemIds, salesOrderId);

        for (SalesItem salesItem : salesItems)
        {
            StatusRuleConstants.checkItemStatuses(salesItem.getStatusList(), SalesTransactions.BACKORDER_ITEM, salesItem.getItemId());
            List<SalesItemStatus> newStatusList = StatusManagementUtil.applyItemStatusChange(salesItem, SalesTransactions.BACKORDER_ITEM);
            salesItem.setStatusList(newStatusList);

            BackOrderedItem backOrderedItem = BackOrderedItem.builder().bckQty(backOrderedQuantity).build();
            salesItem.setBackOrderedItem(backOrderedItem);
            backOrderedItem.setSalesItem(salesItem); // Set the owning side
        }
        return salesItemRepository.saveAll(salesItems);
    }

    public SalesOrder changeRecurrentDetails(UUID salesOrderId, RecurrentOrderDetailsDto recurrentOrderDetailsDto,Boolean systemChange)
    {
        SalesOrder salesOrder = getOrderWithStatus(salesOrderId);

    // Use my standard permission check for updates
        checkUpdatePermission(salesOrder, systemChange);

        if (salesOrder.getRecurrent()==false)
        {
            throw new IllegalArgumentException("Recurrent orders not found");
        }
        if (salesOrder.getInTransit()==true)
        {
            // This is a business rule, not a status rule, so it's good here.
            throw new IllegalArgumentException("Item already in transit");
        }
        RecurrentOrderDetails recurrentOrderDetails = salesOrderMapper.toRecurrentOrderDetails(recurrentOrderDetailsDto);
        salesOrder.setRecurrentOrderDetails(recurrentOrderDetails);
        recurrentOrderDetails.setSalesOrder(salesOrder); // Set the owning side

        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public SalesOrder CancelOrder(UUID salesOrderId,Boolean systemCancel)
    {
        // Need the full order with items for this
        SalesOrder salesOrder = getOrderWithItems(salesOrderId);
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(), SalesTransactions.CANCEL_ORDER, salesOrderId);
        // Determine if it's a user or system cancel
        SalesTransactions itemTransaction = systemCancel ? SalesTransactions.SYSTEM_CANCEL_ITEM : SalesTransactions.CANCEL_ITEM;
        // Cancel all items first
        for (SalesItem salesItem : salesOrder.getSalesItems())
        {
            StatusRuleConstants.checkItemStatuses(salesItem.getStatusList(), itemTransaction, salesItem.getItemId());
            List<SalesItemStatus> salesItemStatuses = StatusManagementUtil.applyItemStatusChange(salesItem, itemTransaction);
            salesItem.setStatusList(salesItemStatuses);
        }
        // Now cancel the order header
        List<SalesOrderStatus> orderStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder, SalesTransactions.CANCEL_ORDER);
        salesOrder.setSalesItems(salesOrder.getSalesItems());
        salesOrder.setStatusList(orderStatusList);

        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public List<SalesItem> cancelOrderItems(List<UUID> salesItemIds, UUID salesOrderId, Boolean systemCancel)
    {
        SalesOrder salesOrder = getOrderWithStatus(salesOrderId);
        // Check header rules
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(), SalesTransactions.CANCEL_ITEM, salesOrderId);

        List<SalesItem> salesItems = getOrderItems(salesItemIds, salesOrderId);
        SalesTransactions transaction = systemCancel ? SalesTransactions.SYSTEM_CANCEL_ITEM : SalesTransactions.CANCEL_ITEM;

        for (SalesItem salesItem : salesItems)
        {
            StatusRuleConstants.checkItemStatuses(salesItem.getStatusList(), transaction, salesItem.getItemId());
            List<SalesItemStatus> newStatusList = StatusManagementUtil.applyItemStatusChange(salesItem, transaction);
            salesItem.setStatusList(newStatusList);
        }
        List <SalesItem> cancelledItems = salesItemRepository.saveAll(salesItems);
        // Side effect: If all items are cancelled, cancel the order too.
        cancelOrderIfAllItemsCancelled(salesOrder);
        return cancelledItems;
    }

    @Transactional
    public SalesOrder changeDeliverySpeed(DeliverySpeed deliverySpeed, UUID salesOrderId)
    {
        // Need to use getOrderWithDetails for this, as findById doesn't fetch statuses
        SalesOrder salesOrder = getOrderWithDetails(salesOrderId);
        // Can't change speed if it's locked (e.g., in transit)
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(),SalesTransactions.CHANGE_DETAILS, salesOrderId);
        salesOrder.setDeliverySpeed(deliverySpeed);
        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public SalesOrder addOrderItems(UUID salesOrderId, List<ItemCreationDto> itemsToAdd, Boolean systemChange)
    {
        SalesOrder salesOrder = getOrderWithStatus(salesOrderId);
        // Use my standard permission check for updates
        checkUpdatePermission(salesOrder, systemChange);

        List<SalesItem> salesItems = salesOrder.getSalesItems();
        for (ItemCreationDto itemCreationDto : itemsToAdd)
        {
            SalesItem salesItem = salesOrderMapper.toSalesItem(itemCreationDto);
            salesItem.setSalesOrder( salesOrder ); // Set the owning side
            salesItems.add(salesItem);
        }
        // Set the list once, *after* the loop
        salesOrder.setSalesItems(salesItems);
        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public SalesOrder restartOrder(UUID salesOrderId, Boolean regenerateInvoice)
    {
        SalesOrder salesOrder = getOrderWithItems(salesOrderId);
        //This prevents restarting of non-recurring orders
        if(! salesOrder.getRecurrent())
        {
            throw new IllegalArgumentException("Invalid Request: restartOrder is for recurrent system use only.");
        }
        // Restarts the order lifecycle for recurremt orders
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(),SalesTransactions.RESTART_ORDER, salesOrderId);
        List<SalesOrderStatus> newStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder,SalesTransactions.RESTART_ORDER);
        salesOrder.setStatusList(newStatusList);

        // In case we might need to cancel the old invoice
        if (regenerateInvoice)
        {
            StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(),SalesTransactions.CANCEL_INVOICE, salesOrderId);
            // Must apply the next status change to the *new* list.
            newStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder, SalesTransactions.CANCEL_INVOICE);
            salesOrder.setStatusList(newStatusList);
        }
        return salesOrderRepository.save(salesOrder);
    }

    /**
     * Public helper for controllers to fetch the full order DTO.
     */
    @Transactional(readOnly = true)
    public SalesOrder findOrderWithDetailsById(UUID salesOrderId)
    {
        // Using the "details" fetch from the repo
        return getOrderWithDetails(salesOrderId);
    }

    @Transactional
    public SalesOrder restartDisputedOrder(UUID salesOrderId,Boolean regenerateInvoice)
    {
        SalesOrder salesOrder = getOrderWithItems(salesOrderId);
        // Restarts the order in case of disputes
        StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(),SalesTransactions.RESTART_DISPUTED_ORDER, salesOrderId);
        List<SalesOrderStatus> newStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder,SalesTransactions.RESTART_DISPUTED_ORDER);
        salesOrder.setStatusList(newStatusList);

        // If needed to cancel the old invoice
        if (regenerateInvoice)
        {
            StatusRuleConstants.checkHeaderStatuses(salesOrder.getStatusList(),SalesTransactions.CANCEL_INVOICE, salesOrderId);
            // Must apply the next status change to the new list.
            newStatusList = StatusManagementUtil.applyOrderStatusChange(salesOrder, SalesTransactions.CANCEL_INVOICE);
            salesOrder.setStatusList(newStatusList);
        }
        return salesOrderRepository.save(salesOrder);
    }

// =================================================================
// PRIVATE HELPER METHODS
// =================================================================

    /**
     * My helper to simplify checking if an update is allowed (system vs. user).
     * This avoids repeating the same if/else block in 3-4 methods.
     */
    private void checkUpdatePermission(SalesOrder order, boolean systemChange) {
        if (systemChange) {
            StatusRuleConstants.checkHeaderStatuses(order.getStatusList(),SalesTransactions.SYSTEM_CHANGE_DETAILS, order.getSalesOrderId());
        } else
        {
            StatusRuleConstants.checkHeaderStatuses(order.getStatusList(),SalesTransactions.CHANGE_DETAILS, order.getSalesOrderId());
        }
    }

    /**
     * Helper to auto-cancel the order header if all its items are cancelled.
     */
    private void cancelOrderIfAllItemsCancelled(SalesOrder salesOrder)
    {
        // Check if any non-cancelled items are left
        if(salesItemRepository.findNonCancelledItemsByOrderId(salesOrder.getSalesOrderId()).isEmpty())
        {
            List <SalesOrderStatus> newStatuses = StatusManagementUtil.applyOrderStatusChange(salesOrder, SalesTransactions.CANCEL_ORDER);
            salesOrder.setStatusList(newStatuses);
            salesOrderRepository.save(salesOrder);
        }
    }

    /**
     * Fetches a list of items and ensures they exist.
     */
    private List<SalesItem> getOrderItems(List<UUID> itemIds, UUID orderId)
    {
        List<SalesItem> salesItems = salesItemRepository.findWithDetailsByItemIdInAndSalesOrder_SalesOrderId(itemIds, orderId);
        if (salesItems.isEmpty() || salesItems.size() != itemIds.size())
        {
            // Check count just in case some IDs were invalid
            throw new EntityNotFoundException("One or more items were not found for this order.");
        }
        return salesItems;
    }

    /**
     * Fetches just the order statuses for a quick check.
     */
    private List<SalesOrderStatus> getOrderStatus(UUID orderId)
    {
        return salesOrderStatusRepository.findAllBySalesOrder_SalesOrderId(orderId);
    }

    /**
     * My standard getter for most operations.
     * Fetches an order with its status list.
     */
    private SalesOrder getOrderWithStatus(UUID salesOrderId)
    {
        return salesOrderRepository.findWithStatusBySalesOrderId(salesOrderId)
                .orElseThrow(() -> new EntityNotFoundException("SalesOrder with id: " + salesOrderId + " not found"));
    }

    /**
     * My getter for operations that need to loop over items (Cancel, Restart).
     * Fetches an order with items and statuses.
     */
    private SalesOrder getOrderWithItems(UUID salesOrderId)
    {
        return salesOrderRepository.findWithStatusAndItemsBySalesOrderId(salesOrderId)
                .orElseThrow(() -> new EntityNotFoundException("SalesOrder with id: " + salesOrderId + " not found"));
    }



    /**
     * My getter for read operations (get by ID, change speed).
     * Fetches an order with all details (shipment, recurrent).
     */
    private SalesOrder getOrderWithDetails(UUID salesOrderId)
    {
        return salesOrderRepository.findWithOrderDetailsBySalesOrderId(salesOrderId)
                .orElseThrow(() -> new EntityNotFoundException("SalesOrder with id: " + salesOrderId + " not found"));
    }

}