package com.mattercom.salesOrders.dto;

import com.mattercom.salesOrders.enums.ItemStatusId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ItemResponseDto
{
    private ItemCreationDto item;
    private UUID itemId;
    private Map<ItemStatusId, Instant> activeStatuses; //the timestamp here should be updation timestamp
}
