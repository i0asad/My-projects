package com.mattercom.salesOrders.enums;

import lombok.Getter;


@Getter
public enum ItemStatusId {

    E_BKO("Backordered"),
    I_INVC_ITEM("SalesItem Invoiced"),
    I_DLFL_ITEM("SalesItem Deletion Flag Set"),
    E_CNCL_CUST("Cancelled by Customer"),
    E_CNCL_SYS("Cancelled by System/Admin");

    private final String description;

    ItemStatusId(String description) {
        this.description = description;

    }

        public String getStatusId() {
        return this.name();
    }
}