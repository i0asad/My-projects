package com.mattercom.salesOrders.util;

import com.mattercom.salesOrders.enums.*;
import com.mattercom.salesOrders.records.StatusRule;
import java.util.Map;

public final class StatusChangeConstants {

    // --- ORDER LEVEL RULES ---
    private static final Map<SalesTransactions, StatusRule<OrderStatusId>> ORDER_RULES = Map.ofEntries(

            Map.entry(SalesTransactions.APPROVE_ORDER,
                    StatusRule.deactivate(OrderStatusId.E_AWT_APPV)
            ),

            Map.entry(SalesTransactions.RELEASE_ORDER,
                    StatusRule.move(OrderStatusId.I_REL,OrderStatusId.I_CRTD)
            ),
            Map.entry(SalesTransactions.SET_TRANSIT_ACTIVE,
                    StatusRule.move(OrderStatusId.E_TRNS,OrderStatusId.I_REL)
            ),

            Map.entry(SalesTransactions.SET_TRANSIT_INACTIVE,
                    StatusRule.move(OrderStatusId.I_WAIT,OrderStatusId.E_TRNS)
            ),

            Map.entry(SalesTransactions.RESTART_ORDER,
                    StatusRule.move(OrderStatusId.I_CRTD,OrderStatusId.I_WAIT)
            ),

            Map.entry(SalesTransactions.CANCEL_ORDER,
                    StatusRule.activate(OrderStatusId.E_CNCL)
            ),
            Map.entry(SalesTransactions.MARK_ORDER_DELETION_FLAG,
                    StatusRule.activate(OrderStatusId.I_DLFL)
            ),
            Map.entry(SalesTransactions.COMPLETE_ORDER,
                    StatusRule.move(OrderStatusId.I_CMPL,OrderStatusId.I_WAIT)
            ),

            Map.entry(SalesTransactions.APPLY_CREDIT_BLOCK,
                    StatusRule.activate(OrderStatusId.E_CRED_BLK, OrderStatusId.I_DLKD, OrderStatusId.I_BLKD)
            ),
            Map.entry(SalesTransactions.REMOVE_CREDIT_BLOCK,
                    StatusRule.deactivate(OrderStatusId.E_CRED_BLK, OrderStatusId.I_DLKD, OrderStatusId.I_BLKD)
            ),
            Map.entry(SalesTransactions.APPLY_ADMIN_HOLD,
                    StatusRule.activate(OrderStatusId.E_ADMIN_HOLD)
            ),
            Map.entry(SalesTransactions.REMOVE_ADMIN_HOLD,
                    StatusRule.deactivate(OrderStatusId.E_ADMIN_HOLD)
            ),
            Map.entry(SalesTransactions.GENERATE_INVOICE,
                    StatusRule.activate(OrderStatusId.I_INV)
            ),
            Map.entry(SalesTransactions.CANCEL_INVOICE,
                    StatusRule.deactivate(OrderStatusId.I_INV)
            ),
            Map.entry(SalesTransactions.RAISE_DISPUTE,
                    StatusRule.activate(OrderStatusId.E_DISP)
            ),
            Map.entry(SalesTransactions.RESTART_DISPUTED_ORDER,
                    StatusRule.move(OrderStatusId.I_CRTD,OrderStatusId.E_DISP)
            ),


            Map.entry(SalesTransactions.RESOLVE_DISPUTE,
                    StatusRule.deactivate(OrderStatusId.E_DISP)
            )
    );

    // --- ITEM LEVEL RULES ---
    private static final Map<SalesTransactions, StatusRule<ItemStatusId>> ITEM_RULES = Map.ofEntries(
            Map.entry(SalesTransactions.CANCEL_ITEM,
                    StatusRule.activate(ItemStatusId.E_CNCL_CUST)
            ),
            Map.entry(SalesTransactions.SYSTEM_CANCEL_ITEM,
                    StatusRule.activate(ItemStatusId.E_CNCL_SYS)
            ),
            Map.entry(SalesTransactions.SET_ITEM_DELETION_FLAG,
                    StatusRule.activate(ItemStatusId.I_DLFL_ITEM)
            ),
            Map.entry(SalesTransactions.BACKORDER_ITEM,
                    StatusRule.activate(ItemStatusId.E_BKO)
            ),
            Map.entry(SalesTransactions.REORDER_ITEM,
                    StatusRule.deactivate(ItemStatusId.E_BKO)
            )
    );

    public static StatusRule<OrderStatusId> getOrderRule(SalesTransactions transaction) {
        return ORDER_RULES.get(transaction);
    }

    public static StatusRule<ItemStatusId> getItemRule(SalesTransactions transaction) {
        return ITEM_RULES.get(transaction);
    }

    private StatusChangeConstants() {}
}


