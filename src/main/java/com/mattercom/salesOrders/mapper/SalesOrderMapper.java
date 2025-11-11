package com.mattercom.salesOrders.mapper;

import com.mattercom.salesOrders.dto.ItemCreationDto;
import com.mattercom.salesOrders.dto.OrderCreationDto;
import com.mattercom.salesOrders.dto.RecurrentOrderDetailsDto;
import com.mattercom.salesOrders.dto.ShipmentAddressDto;
import com.mattercom.salesOrders.entities.RecurrentOrderDetails;
import com.mattercom.salesOrders.entities.SalesItem;
import com.mattercom.salesOrders.entities.SalesOrder;
import com.mattercom.salesOrders.entities.ShipmentAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
@Component
public interface SalesOrderMapper {

    SalesOrder toSalesOrder(OrderCreationDto orderCreationDto);

    ShipmentAddress toShipmentAddress(ShipmentAddressDto shipmentAddressDto);

    RecurrentOrderDetails toRecurrentOrderDetails(RecurrentOrderDetailsDto recurrentOrderDetailsDto);

    SalesItem toSalesItem(ItemCreationDto itemCreationDto );

    ShipmentAddressDto toShipmentAddressDto(ShipmentAddress address);

    RecurrentOrderDetailsDto toRecurrentOrderDetailsDto(RecurrentOrderDetails details);

    ItemCreationDto toItemCreationDto(SalesItem item);

    @Mapping(target = "dto", ignore = true)
    OrderCreationDto toOrderCreationDto(SalesOrder order);

}