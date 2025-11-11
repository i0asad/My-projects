package com.mattercom.salesOrders.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecurrentOrderDetailsDto
{
    @Positive
    private Integer installments;

    @Positive
    private Integer gapInDays;


    @PositiveOrZero
    private Integer requestedOffsetInDays;



}
