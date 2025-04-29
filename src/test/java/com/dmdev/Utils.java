package com.dmdev;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class Utils {
    public static final Subscription IVAN = Subscription.builder()
            .id(1)
            .userId(1)
            .name("Ivan")
            .provider(Provider.GOOGLE)
            .expirationDate(LocalDate.parse("2090-01-10").atStartOfDay().toInstant(ZoneOffset.UTC))
            .status(Status.ACTIVE)
            .build();

    public static final Subscription ALEX = Subscription.builder()
            .id(3)
            .userId(3)
            .name("Alex")
            .provider(Provider.APPLE)
            .expirationDate(LocalDate.parse("2030-01-10").atStartOfDay().toInstant(ZoneOffset.UTC))
            .status(Status.ACTIVE)
            .build();

    public static final Subscription JOHN = Subscription.builder()
            .id(4)
            .userId(4)
            .name("John")
            .provider(Provider.APPLE)
            .expirationDate(LocalDate.parse("2030-01-10").atStartOfDay().toInstant(ZoneOffset.UTC))
            .status(Status.CANCELED)
            .build();
}
