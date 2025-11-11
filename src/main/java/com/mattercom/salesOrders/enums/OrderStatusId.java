package com.mattercom.salesOrders.enums;


import lombok.Getter;

/**
 * Represents the status of an Order Header within the SalesOrderApi.
 * This defines the overall commercial and financial state of the order.
 */
@Getter
public enum OrderStatusId {

    // --- Core Lifecycle ---
    I_CRTD("Created"),
    I_REL("Released"),
    E_TRNS("Order in Transit"),
    I_WAIT("Waiting"),
    I_CMPL("Logistically Completed"),
    E_CNCL("Cancelled"),

    // --- Financial & Approval Blocks ---
    E_AWT_APPV("Awaiting Approval"),
    E_CRED_BLK("Credit Block Applied"),
    E_FRD_HOLD("Fraud Hold Applied"),
    I_DLKD("Delivery Blocked"),
    I_BLKD("Billing Blocked"),

    // --- Other States & Locks ---
    I_LOCK("Locked"),
    E_ADMIN_HOLD("Administrative Hold"),
    E_DISP("Disputed"),
    I_INV("Invoiced"),
    I_DLFL("Deletion Flag Set");

    private final String description;


    OrderStatusId(String description) {
        this.description = description;

    }

    /**
     * Provides the string ID of the enum, which matches the enum name.
     * @return The status ID (e.g., "I_CRTD")
     */
    public String getStatusId() {
        return this.name();
    }
}