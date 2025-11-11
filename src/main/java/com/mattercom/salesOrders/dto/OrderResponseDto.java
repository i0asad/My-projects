package com.mattercom.salesOrders.dto;

import com.mattercom.salesOrders.enums.OrderStatusId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderResponseDto
{   private OrderCreationDto order;
    private UUID orderId;
    List<ItemResponseDto> items;
    private Map<OrderStatusId, Instant> activeStatuses; //the timestamp here should be updation timestamp
}
