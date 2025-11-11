package com.mattercom.salesOrders.util;

import com.mattercom.salesOrders.entities.status.*;
import com.mattercom.salesOrders.enums.ItemStatusId;
import com.mattercom.salesOrders.enums.OrderStatusId;
import com.mattercom.salesOrders.enums.SalesTransactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import static java.util.Map.entry;

public final class StatusRuleConstants {

    private static final Logger log = LoggerFactory.getLogger(StatusRuleConstants.class);

    private static final Map<OrderStatusId, Set<SalesTransactions>> HEADER_FORBID_RULES = Map.ofEntries(
            entry(OrderStatusId.E_AWT_APPV, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.SET_TRANSIT_ACTIVE
            )),
            entry(OrderStatusId.E_CRED_BLK, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.RESTART_ORDER
            )),
            entry(OrderStatusId.E_FRD_HOLD, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.RESTART_ORDER,
                    SalesTransactions.RESTART_DISPUTED_ORDER

            )),
            entry(OrderStatusId.E_ADMIN_HOLD, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS,
                    SalesTransactions.RESTART_ORDER,
                    SalesTransactions.RESTART_DISPUTED_ORDER
            )),
            entry(OrderStatusId.E_DISP, Set.of(
                    SalesTransactions.COMPLETE_ORDER,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.RAISE_DISPUTE,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS,
                    SalesTransactions.RESTART_ORDER
            )),
            entry(OrderStatusId.I_BLKD, Set.of(
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.RESTART_ORDER
            )),
            entry(OrderStatusId.I_DLKD, Set.of(
                    SalesTransactions.SET_TRANSIT_ACTIVE
            )),
            entry(OrderStatusId.I_LOCK, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.SYSTEM_CANCEL_ITEM,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS,
                    SalesTransactions.SYSTEM_CHANGE_DETAILS,
                    SalesTransactions.RESTART_ORDER,
                    SalesTransactions.RESTART_DISPUTED_ORDER

            )),
            entry(OrderStatusId.I_INV, Set.of(
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS

            )),

            entry(OrderStatusId.I_CMPL, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.LOCK_ORDER,
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.SYSTEM_CANCEL_ITEM,
                    SalesTransactions.APPLY_CREDIT_BLOCK,
                    SalesTransactions.APPLY_FRAUD_HOLD,
                    SalesTransactions.APPLY_ADMIN_HOLD,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.BACKORDER_ITEM,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS,
                    SalesTransactions.SYSTEM_CHANGE_DETAILS,
                    SalesTransactions.CANCEL_INVOICE
            )),
            entry(OrderStatusId.E_CNCL, Set.of(
                    SalesTransactions.RELEASE_ORDER,
                    SalesTransactions.LOCK_ORDER,
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.SYSTEM_CANCEL_ITEM,
                    SalesTransactions.APPLY_CREDIT_BLOCK,
                    SalesTransactions.APPLY_FRAUD_HOLD,
                    SalesTransactions.APPLY_ADMIN_HOLD,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CHANGE_DETAILS,
                    SalesTransactions.SYSTEM_CHANGE_DETAILS,
                    SalesTransactions.RESTART_ORDER

            )),

            entry(OrderStatusId.E_TRNS, Set.of(
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.SET_TRANSIT_ACTIVE,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.GENERATE_INVOICE,
                    SalesTransactions.MARK_ORDER_DELETION_FLAG,
                    SalesTransactions.COMPLETE_ORDER,
                    SalesTransactions.BACKORDER_ITEM,
                    SalesTransactions.CHANGE_DETAILS


            )),

            entry(OrderStatusId.I_WAIT, Set.of(
                    SalesTransactions.CANCEL_ORDER,
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.BACKORDER_ITEM,
                    SalesTransactions.CANCEL_INVOICE,
                    SalesTransactions.CHANGE_DETAILS
            )),

            entry(OrderStatusId.I_REL, Set.of(
                    SalesTransactions.CHANGE_DETAILS
            ))

    );

    private static final Map<ItemStatusId, Set<SalesTransactions>> ITEM_FORBID_RULES = Map.ofEntries(
            entry(ItemStatusId.E_CNCL_CUST, Set.of(
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.SYSTEM_CANCEL_ITEM,
                    SalesTransactions.BACKORDER_ITEM
            )),
            entry(ItemStatusId.E_CNCL_SYS, Set.of(
                    SalesTransactions.CANCEL_ITEM,
                    SalesTransactions.SYSTEM_CANCEL_ITEM,
                    SalesTransactions.BACKORDER_ITEM
            ))
    );

    public static void checkHeaderStatuses(List<SalesOrderStatus> orderStatuses, SalesTransactions transaction, UUID orderId) throws IllegalStateException {
        log.debug("Checking HEADER statuses for transaction {} on order {}", transaction, orderId);

        if (orderStatuses == null || orderStatuses.isEmpty()) {
            return;
        }

        OrderStatusId forbiddenByStatus = orderStatuses.stream()
                .filter(SalesOrderStatus::getActive)
                .map(SalesOrderStatus::getOrderStatusId)
                .filter(orderStatusId -> {
                    Set<SalesTransactions> forbidden = HEADER_FORBID_RULES.get(orderStatusId);
                    return forbidden != null && forbidden.contains(transaction);
                })
                .findFirst()
                .orElse(null);

        if (forbiddenByStatus != null) {
            String errorMsg = String.format(
                    "Transaction %s FORBIDDEN for order %s by active header status: %s",
                    transaction, orderId, forbiddenByStatus.name()
            );
            log.warn(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    public static void checkItemStatuses(List<SalesItemStatus> salesItemStatuses, SalesTransactions transaction, UUID itemId) throws IllegalStateException {
        log.debug("Checking ITEM statuses for transaction {} on item {}", transaction, itemId);

        if (salesItemStatuses == null || salesItemStatuses.isEmpty()) {
            return;
        }

        ItemStatusId forbiddenByStatus = salesItemStatuses.stream().filter(SalesItemStatus::getActive)
                .map(SalesItemStatus::getItemStatusId)
                .filter(itemStatusId -> {
                    Set<SalesTransactions> forbidden = ITEM_FORBID_RULES.get(itemStatusId);
                    return forbidden != null && forbidden.contains(transaction);
                })
                .findFirst()
                .orElse(null);

        if (forbiddenByStatus != null) {
            String errorMsg = String.format(
                    "Item transaction %s FORBIDDEN for item %s by active item status: %s",
                    transaction, itemId, forbiddenByStatus.name()
            );
            log.warn(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    private StatusRuleConstants() {}
}