package com.mattercom.salesOrders.enums;

import lombok.Getter;

@Getter
public enum SalesTransactions {

    // === ORDER LIFECYCLE ===
    RELEASE_ORDER(ObjectType.ORDER),
    LOCK_ORDER(ObjectType.ORDER),
    UNLOCK_ORDER(ObjectType.ORDER),
    CANCEL_ORDER(ObjectType.ORDER),
    SET_TRANSIT_ACTIVE(ObjectType.ORDER),
    SET_TRANSIT_INACTIVE(ObjectType.ORDER),
    RESTART_ORDER(ObjectType.ORDER),
    MARK_ORDER_DELETION_FLAG(ObjectType.ORDER),
    COMPLETE_ORDER(ObjectType.ORDER),

    // === APPROVAL & COMPLIANCE (ORDER-LEVEL BLOCKS) ===
    APPLY_CREDIT_BLOCK(ObjectType.ORDER),
    REMOVE_CREDIT_BLOCK(ObjectType.ORDER),
    APPLY_FRAUD_HOLD(ObjectType.ORDER),
    REMOVE_FRAUD_HOLD(ObjectType.ORDER),
    APPLY_ADMIN_HOLD(ObjectType.ORDER),
    REMOVE_ADMIN_HOLD(ObjectType.ORDER),
    APPROVE_ORDER(ObjectType.ORDER),
    REMOVE_CANCEL_BLOCK(ObjectType.ORDER),
    APPLY_DELIVERY_BLOCK(ObjectType.ORDER),
    REMOVE_DELIVERY_BLOCK(ObjectType.ORDER),
    CHANGE_DETAILS(ObjectType.ORDER),
    SYSTEM_CHANGE_DETAILS(ObjectType.ORDER),

    // === BILLING (ORDER-LEVEL) ===
    GENERATE_INVOICE(ObjectType.ORDER),
    CANCEL_INVOICE(ObjectType.ORDER),
    APPLY_BILLING_BLOCK(ObjectType.ORDER),
    REMOVE_BILLING_BLOCK(ObjectType.ORDER),

    // === ITEM-LEVEL OPERATIONS (COMMERCIAL) ===
    CREATE_ITEM(ObjectType.ITEM),
    SYSTEM_CANCEL_ITEM(ObjectType.ITEM),
    CANCEL_ITEM(ObjectType.ITEM),
    SET_ITEM_DELETION_FLAG(ObjectType.ITEM),
    BACKORDER_ITEM(ObjectType.ITEM),
    REORDER_ITEM(ObjectType.ITEM),

    // === DISPUTE & RESOLUTION ===
    RAISE_DISPUTE(ObjectType.ORDER),
    RESOLVE_DISPUTE(ObjectType.ORDER),
    RESTART_DISPUTED_ORDER(ObjectType.ORDER);
    private final ObjectType objectType;

    SalesTransactions(ObjectType objectType) {
        this.objectType = objectType;
    }
}