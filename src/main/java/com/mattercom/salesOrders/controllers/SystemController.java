package com.mattercom.salesOrders.controllers;

import com.mattercom.salesOrders.dto.*;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.mapper.SalesOrderResponseMapper;
import com.mattercom.salesOrders.services.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API Controller for Internal operations (Admin, System Services).
 * Endpoints here can use 'systemChange' flags.
 */
@RestController
@RequestMapping("/api/v1/internal/sales-orders") // Internal-only path
@RequiredArgsConstructor
public class SystemController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderResponseMapper responseMapper;

    /**
     * Performs a generic order-level transaction.
     */
    @PostMapping("/{orderId}/transaction")
    public ResponseEntity<OrderResponseDto> performOrderTransaction(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.TransactionRequest request
    ) {
        SalesOrder updatedOrder = salesOrderService.performOrderTransaction(orderId, request.getTransaction());
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Performs a generic item-level transaction.
     */
    @PostMapping("/{orderId}/items/transaction")
    public ResponseEntity<List<ItemResponseDto>> performItemTransaction(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.ItemTransactionRequest request
    ) {
        List<SalesItem> updatedItems = salesOrderService.performItemTransaction(request.getItemIds(), request.getTransaction(), orderId);
        // Convert list of SalesItem to list of ItemResponseDto
        List<ItemResponseDto> response = updatedItems.stream()
                .map(responseMapper::toItemResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Restarts disputed order (emergency-only).
     */
    //Made it private to keep it locked for now
    @PostMapping("/{orderId}/emergency/restart")
    public ResponseEntity<OrderResponseDto> restartDisputedOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.RestartRequest request
    ) {
        SalesOrder updatedOrder = salesOrderService.restartDisputedOrder(orderId, request.getRegenerateInvoice());
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Restarts a recurrent order (system-only).
     */
    @PostMapping("/{orderId}/restart")
    public ResponseEntity<OrderResponseDto> restartRecurrentOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.RestartRequest request
    ) {
        SalesOrder updatedOrder = salesOrderService.restartOrder(orderId, request.getRegenerateInvoice());
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }


    /**
     * Backorders specific items (system-only).
     */
    @PostMapping("/{orderId}/items/backorder")
    public ResponseEntity<List<ItemResponseDto>> backOrderItems(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.BackOrderRequest request
    ) {
        List<SalesItem> updatedItems = salesOrderService.backOrderItem(request.getItemIds(), orderId, request.getQuantity());
        List<ItemResponseDto> response = updatedItems.stream()
                .map(responseMapper::toItemResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * System-level add items.
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponseDto> addOrderItems(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.AddItemsRequest request
    ) {
        SalesOrder updatedOrder = salesOrderService.addOrderItems(orderId, request.getItems(), true);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * System-level shipping address update.
     */
    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<OrderResponseDto> changeShippingAddress(
            @PathVariable UUID orderId,
            @Valid @RequestBody ShipmentAddressDto shipmentAddressDto
    ) {
        SalesOrder updatedOrder = salesOrderService.ChangeShippingAddress(orderId, shipmentAddressDto, true);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * System-level recurrence update.
     */
    @PutMapping("/{orderId}/recurrent-details")
    public ResponseEntity<OrderResponseDto> changeRecurrentDetails(
            @PathVariable UUID orderId,
            @Valid @RequestBody RecurrentOrderDetailsDto detailsDto
    ) {
        SalesOrder updatedOrder = salesOrderService.changeRecurrentDetails(orderId, detailsDto, true);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * System-level order cancellation.
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable UUID orderId) {
        SalesOrder updatedOrder = salesOrderService.CancelOrder(orderId, true);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * System-level item cancellation.
     */
    @PostMapping("/{orderId}/items/cancel")
    public ResponseEntity<List<ItemResponseDto>> cancelOrderItems(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.ItemListRequest request
    ) {
        // This now correctly returns the list of items that were cancelled
        List<SalesItem> cancelledItems = salesOrderService.cancelOrderItems(request.getItemIds(), orderId, true);

        List<ItemResponseDto> response = cancelledItems.stream()
                .map(responseMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}