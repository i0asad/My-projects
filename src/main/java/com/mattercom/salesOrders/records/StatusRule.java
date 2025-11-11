package com.mattercom.salesOrders.records;

import java.util.Set;

/**
 * A simple, TYPE-SAFE record to hold which statuses to activate and deactivate.
 * It uses generics (T) to accept either OrderStatusId or ItemStatusId.
 */
public record StatusRule<T extends Enum<T>>(Set<T> activate, Set<T> deactivate) {

    @SafeVarargs
    public static <T extends Enum<T>> StatusRule<T> activate(T... statuses) {
        return new StatusRule<>(Set.of(statuses), Set.of());
    }

    @SafeVarargs
    public static <T extends Enum<T>> StatusRule<T> deactivate(T... statuses) {
        return new StatusRule<>(Set.of(), Set.of(statuses));
    }

    public static <T extends Enum<T>> StatusRule<T> move(T activate, T deactivate) {
        return new StatusRule<>(Set.of(activate), Set.of(deactivate));
    }
}