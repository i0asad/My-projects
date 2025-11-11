package com.mattercom.salesOrders.controllers;

import com.mattercom.salesOrders.dto.*;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.enums.DeliverySpeed;
import com.mattercom.salesOrders.mapper.SalesOrderResponseMapper;
import com.mattercom.salesOrders.services.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API Controller for Customer-Facing operations.
 * Assumes authenticated customer.
 * All service calls hardcode 'systemChange' flags to 'false'.
 */
@RestController
@RequestMapping("/api/v1/my-orders") // Customer-specific path
@RequiredArgsConstructor
public class CustomerEndpointController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderResponseMapper responseMapper;
    // In a real app, inject a service to get the authenticated customer's ID

    /**
     * Creates a new sales order.
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderCreationDto creationDto,
            @RequestParam(defaultValue = "false") boolean approvalRequired,
            @RequestParam(defaultValue = "false") boolean creditBlock,
            @RequestParam(defaultValue = "false") boolean fraudHold
    ) {
        OrderCreationFlags flags = OrderCreationFlags.builder()
                .approvalRequired(approvalRequired)
                .creditBlock(creditBlock)
                .fraudHold(fraudHold)
                .build();

        UUID orderId = salesOrderService.createOrder(creationDto, flags);

        // Fetch the newly created order to return it in the full response format
        SalesOrder newOrder = salesOrderService.findOrderWithDetailsById(orderId);
        return new ResponseEntity<>(responseMapper.toOrderResponseDto(newOrder), HttpStatus.CREATED);
    }

    /**
     * Retrieves a single order by its ID.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID orderId) {
        // TODO: Add security check here to ensure customer owns this order
        SalesOrder order = salesOrderService.findOrderWithDetailsById(orderId);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(order));
    }

    /**
     * Adds one or more new items to an existing order (if state allows).
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponseDto> addOrderItems(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.AddItemsRequest request
    ) {
        // systemChange is hardcoded to false
        SalesOrder updatedOrder = salesOrderService.addOrderItems(orderId, request.getItems(), false);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Updates the shipping address for an order (if state allows).
     */
    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<OrderResponseDto> changeShippingAddress(
            @PathVariable UUID orderId,
            @Valid @RequestBody ShipmentAddressDto shipmentAddressDto
    ) {
        // systemChange is hardcoded to false
        SalesOrder updatedOrder = salesOrderService.ChangeShippingAddress(orderId, shipmentAddressDto, false);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Updates the recurrence details for an order (if state allows).
     */
    @PutMapping("/{orderId}/recurrent-details")
    public ResponseEntity<OrderResponseDto> changeRecurrentDetails(
            @PathVariable UUID orderId,
            @Valid @RequestBody RecurrentOrderDetailsDto detailsDto
    ) {
        // systemChange is hardcoded to false
        SalesOrder updatedOrder = salesOrderService.changeRecurrentDetails(orderId, detailsDto, false);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Requests to cancel the entire order (if state allows).
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable UUID orderId) {
        // systemCancel is hardcoded to false
        SalesOrder updatedOrder = salesOrderService.CancelOrder(orderId, false);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }

    /**
     * Requests to cancel specific items from an order.
     * Returns *only* the list of items that were just cancelled.
     */
    @PostMapping("/{orderId}/items/cancel")
    public ResponseEntity<List<ItemResponseDto>> cancelOrderItems(
            @PathVariable UUID orderId,
            @Valid @RequestBody WebRequestDtos.ItemListRequest request
    ) {
        // systemCancel is hardcoded to false
        List<SalesItem> cancelledItems = salesOrderService.cancelOrderItems(request.getItemIds(), orderId, false);

        // Map the returned items to the ItemResponseDto
        List<ItemResponseDto> response = cancelledItems.stream()
                .map(responseMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Changes the delivery speed for an order.
     */
    @PatchMapping("/{orderId}/delivery-speed")
    public ResponseEntity<OrderResponseDto> changeDeliverySpeed(
            @PathVariable UUID orderId,
            @RequestParam DeliverySpeed speed
    ) {
        SalesOrder updatedOrder = salesOrderService.changeDeliverySpeed(speed, orderId);
        return ResponseEntity.ok(responseMapper.toOrderResponseDto(updatedOrder));
    }
}