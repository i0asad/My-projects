package com.mattercom.salesOrders.util;

import com.mattercom.salesOrders.dto.OrderCreationFlags;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.entities.status.*;
import com.mattercom.salesOrders.enums.*;
import com.mattercom.salesOrders.records.StatusRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Stateless utility for applying Order and Item status changes
 * using predefined StatusRule constants.
 */
public final class StatusManagementUtil {

    private static final Logger log = LoggerFactory.getLogger(StatusManagementUtil.class);

    private StatusManagementUtil() {}

    // =========================================================
    // ORDER LEVEL STATUS UPDATES
    // =========================================================
    public static List<SalesOrderStatus> applyOrderStatusChange(
            SalesOrder order,
            SalesTransactions transaction
    ) {
        if (transaction.getObjectType() != ObjectType.ORDER) {
            throw new IllegalArgumentException("Invalid transaction type for Item: " + transaction);
        }


        StatusRule<OrderStatusId> rule = StatusChangeConstants.getOrderRule(transaction);
        if (rule == null || (rule.activate().isEmpty() && rule.deactivate().isEmpty())) {
            log.warn("No order status rule defined for {}", transaction);
            return order.getStatusList();
        }

        log.debug("Applying ORDER status change for {} on order {}", transaction, order.getSalesOrderId());

        List<SalesOrderStatus> updated = new ArrayList<>(order.getStatusList());

        // ---- DEACTIVATE ----
        for (OrderStatusId toDeactivate : rule.deactivate()) {
            boolean found = false;
            for (SalesOrderStatus status : updated) {
                if (status.getOrderStatusId() == toDeactivate && Boolean.TRUE.equals(status.getActive())) {
                    status.setActive(false);
                    found = true;
                    log.debug("Deactivated order status {} for order {}", toDeactivate, order.getSalesOrderId());
                }
            }
            if (!found) {
                log.warn("Order status {} was not active for order {}", toDeactivate, order.getSalesOrderId());
            }
        }

        // ---- ACTIVATE ----
        for (OrderStatusId toActivate : rule.activate()) {
            Optional<SalesOrderStatus> existing = updated.stream()
                    .filter(s -> s.getOrderStatusId() == toActivate)
                    .findFirst();

            if (existing.isPresent()) {
                if (existing.get().getActive()) {
                    log.warn("Order status {} already active for order {}", toActivate, order.getSalesOrderId());
                } else {
                    existing.get().setActive(true);
                    log.debug("Reactivated order status {} for order {}", toActivate, order.getSalesOrderId());
                }
            } else {
                // New status
                SalesOrderStatus newStatus = SalesOrderStatus.builder()
                        .salesOrder(order)
                        .orderStatusId(toActivate)
                        .Active(true)
                        .build();
                updated.add(newStatus);
                log.debug("Activated new order status {} for order {}", toActivate, order.getSalesOrderId());
            }
        }

        return updated;
    }

    // =========================================================
    // ITEM LEVEL STATUS UPDATES
    // =========================================================
    public static List<SalesItemStatus> applyItemStatusChange(
            SalesItem item,
            SalesTransactions transaction
    ) {
        if (transaction.getObjectType() != ObjectType.ITEM) {
            throw new IllegalArgumentException("Invalid transaction type for Item: " + transaction);
        }

        StatusRule<ItemStatusId> rule = StatusChangeConstants.getItemRule(transaction);
        if (rule == null || (rule.activate().isEmpty() && rule.deactivate().isEmpty())) {
            log.warn("No item status rule defined for {}", transaction);
            return item.getStatusList();
        }

        log.debug("Applying ITEM status change for {} on item {}", transaction, item.getItemId());

        List<SalesItemStatus> updated = new ArrayList<>(item.getStatusList());

        // ---- DEACTIVATE ----
        for (ItemStatusId toDeactivate : rule.deactivate()) {
            boolean found = false;
            for (SalesItemStatus status : updated) {
                if (status.getItemStatusId() == toDeactivate && Boolean.TRUE.equals(status.getActive())) {
                    status.setActive(false);
                    found = true;
                    log.debug("Deactivated item status {} for item {}", toDeactivate, item.getItemId());
                }
            }
            if (!found) {
                log.warn("Item status {} was not active for item {}", toDeactivate, item.getItemId());
            }
        }

        // ---- ACTIVATE ----
        for (ItemStatusId toActivate : rule.activate()) {
            Optional<SalesItemStatus> existing = updated.stream()
                    .filter(s -> s.getItemStatusId() == toActivate)
                    .findFirst();

            if (existing.isPresent()) {
                if (existing.get().getActive()) {
                    log.warn("Item status {} already active for item {}", toActivate, item.getItemId());
                } else {
                    existing.get().setActive(true);
                    log.debug("Reactivated item status {} for item {}", toActivate, item.getItemId());
                }
            } else {
                // Create new status
                SalesItemStatus newStatus = SalesItemStatus.builder()
                        .salesItem(item)
                        .itemStatusId(toActivate)
                        .Active(true)
                        .build();
                updated.add(newStatus);
                log.debug("Activated new item status {} for item {}", toActivate, item.getItemId());
            }
        }

        return updated;
    }

    // =========================================================
    // CREATION HELPERS
    // =========================================================

    public static List<SalesOrderStatus> orderCreationListProvider(OrderCreationFlags creationFlags) {
        List<SalesOrderStatus> result = new ArrayList<>();

        // Always created
        result.add(SalesOrderStatus.builder()
                .orderStatusId(OrderStatusId.I_CRTD)
                .Active(true)
                .build());

        if (creationFlags.isApprovalRequired()) {
            result.add(SalesOrderStatus.builder()
                    .orderStatusId(OrderStatusId.E_AWT_APPV)
                    .Active(true)
                    .build());
        }

        if (creationFlags.isCreditBlock() || creationFlags.isFraudHold()) {
            result.add(SalesOrderStatus.builder()
                    .orderStatusId(OrderStatusId.I_DLKD)
                    .Active(true)
                    .build());
            result.add(SalesOrderStatus.builder()
                    .orderStatusId(OrderStatusId.I_BLKD)
                    .Active(true)
                    .build());
        }

        if (creationFlags.isFraudHold()) {
            result.add(SalesOrderStatus.builder()
                    .orderStatusId(OrderStatusId.E_FRD_HOLD)
                    .Active(true)
                    .build());
        }

        if (creationFlags.isCreditBlock()) {
            result.add(SalesOrderStatus.builder()
                    .orderStatusId(OrderStatusId.E_CRED_BLK)
                    .Active(true)
                    .build());
        }

        return result;
    }


}
