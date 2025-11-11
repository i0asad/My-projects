package com.mattercom.salesOrders.mapper;

import com.mattercom.salesOrders.dto.*;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.entities.status.SalesItemStatus;
import com.mattercom.salesOrders.entities.status.SalesOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Builds the final OrderResponseDto and ItemResponseDto.
 * Relies on the SalesOrderMapper (MapStruct) for simple boilerplate.
 * This class handles the complex logic (like building the active status map).
 */
@Component
@RequiredArgsConstructor // Use Spring's constructor injection
public class SalesOrderResponseMapper {

    // Injected MapStruct mapper for simple field copies
    private final SalesOrderMapper salesOrderMapper;

    /**
     * Builds the full OrderResponseDto from an entity.
     */
    public OrderResponseDto toOrderResponseDto(SalesOrder order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getSalesOrderId());

        // 1. Build the active Order status map
        if (order.getStatusList() != null) {
            dto.setActiveStatuses(
                    order.getStatusList().stream()
                            .filter(SalesOrderStatus::getActive)
                            .collect(Collectors.toMap(
                                    SalesOrderStatus::getOrderStatusId,
                                    SalesOrderStatus::getLastUpdatedAt,
                                    (existing, replacement) -> existing // In case of duplicates, keep first
                            ))
            );
        } else {
            dto.setActiveStatuses(Collections.emptyMap());
        }

        // 2. Build the list of ItemResponseDtos
        if (order.getSalesItems() != null) {
            dto.setItems(
                    order.getSalesItems().stream()
                            .map(this::toItemResponseDto) // Reuse item mapper
                            .collect(Collectors.toList())
            );
        } else {
            dto.setItems(Collections.emptyList());
        }

        // 3. Use MapStruct for the nested 'order' snapshot
        dto.setOrder(salesOrderMapper.toOrderCreationDto(order));

        return dto;
    }

    /**
     * Builds the ItemResponseDto from an entity.
     */
    public ItemResponseDto toItemResponseDto(SalesItem item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setItemId(item.getItemId());

        // 1. Build the active Item status map
        if (item.getStatusList() != null) {
            dto.setActiveStatuses(
                    item.getStatusList().stream()
                            .filter(SalesItemStatus::getActive)
                            .collect(Collectors.toMap(
                                    SalesItemStatus::getItemStatusId,
                                    SalesItemStatus::getLastUpdatedAt,
                                    (existing, replacement) -> existing
                            ))
            );
        } else {
            dto.setActiveStatuses(Collections.emptyMap());
        }

        // 2. Use MapStruct for the nested 'item' snapshot
        dto.setItem(salesOrderMapper.toItemCreationDto(item));

        return dto;
    }

    /*
     * Helper methods for reverse mapping are no longer needed.
     * Delegated to SalesOrderMapper (MapStruct).
     */
}