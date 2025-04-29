package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {
    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        Instant date = LocalDate.parse("2010-04-14").atStartOfDay().toInstant(ZoneOffset.UTC);
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(100)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(date)
                .build();

        Subscription subscription = mapper.map(subscriptionDto);

        assertEquals(subscriptionDto.getUserId(), subscription.getUserId());
        assertEquals(subscriptionDto.getName(), subscription.getName());
        assertEquals(subscriptionDto.getProvider(), subscription.getProvider().name());
        assertEquals(subscriptionDto.getExpirationDate(), subscription.getExpirationDate());
    }
}