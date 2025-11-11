package com.mattercom.salesOrders.dto;

import com.mattercom.salesOrders.enums.SalesTransactions;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Holds simple DTOs used only for API Request Bodies.
 * This keeps our core DTOs clean and extensible.
 */
public class WebRequestDtos {

    // --- DTOs used by both Customer and Internal Controllers ---

    /**
     * For requests that just need a list of item IDs.
     * e.g., POST /items/cancel
     */
    @Data
    public static class ItemListRequest {
        @NotEmpty
        private List<UUID> itemIds;
    }

    /**
     * For adding new items to an existing order.
     * e.g., POST /items
     */
    @Data
    public static class AddItemsRequest {
        @NotEmpty
        private List<ItemCreationDto> items;
    }

    // --- DTOs used only by the Internal Controller ---

    /**
     * For performing a generic transaction.
     * e.g., POST /transaction
     */
    @Data
    public static class TransactionRequest {
        @NotNull
        private SalesTransactions transaction;
    }

    /**
     * For performing a generic item-level transaction.
     * e.g., POST /items/transaction
     */
    @Data
    public static class ItemTransactionRequest {
        @NotEmpty
        private List<UUID> itemIds;
        @NotNull
        private SalesTransactions transaction;
    }

    /**
     * For backordering items.
     * e.g., POST /items/backorder
     */
    @Data
    public static class BackOrderRequest {
        @NotEmpty
        private List<UUID> itemIds;
        @NotNull
        @Min(1)
        private Integer quantity;
    }

    /**
     * For restarting a recurrent order.
     * e.g., POST /restart
     */
    @Data
    public static class RestartRequest {
        @NotNull
        private Boolean regenerateInvoice;
    }
}