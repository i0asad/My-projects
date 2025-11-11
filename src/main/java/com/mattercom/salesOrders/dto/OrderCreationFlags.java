package com.mattercom.salesOrders.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreationFlags
{
    private boolean approvalRequired;
    private boolean creditBlock;
    private boolean fraudHold;

}
