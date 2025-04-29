package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.dmdev.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest extends IntegrationTestBase {
    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();

        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                Clock.systemUTC()
        );
    }

    @Test
    void upsertThrowsValidationException() {
        CreateSubscriptionDto invalidDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("someName")
                .expirationDate(Instant.ofEpochMilli(System.currentTimeMillis() - 4000))    //invalid expiration date
                .provider("wrongProvider")
                .build();

        ValidationException ex = assertThrows(ValidationException.class, () -> subscriptionService.upsert(invalidDto));
        List<Error> errorList = ex.getErrors();

        //use assertAll just as example to check all assertions even if the previous one failed
        assertAll(
                () -> {
                    assertEquals(errorList.size(), 3);
                },
                () -> {
                    assertEquals(errorList.get(0).getCode(), 100);
                },
                () -> {
                    assertEquals(errorList.get(1).getCode(), 102);
                },
                () -> {
                    assertEquals(errorList.get(2).getCode(), 103);
                }
        );
    }

    @Test
    void upsertExistingSubscription() {
        String newName = IVAN.getName() + "_updated";
        CreateSubscriptionDto existingDto = CreateSubscriptionDto.builder()
                .userId(IVAN.getUserId())
                .name(newName)
                .provider(IVAN.getProvider().name())
                .expirationDate(IVAN.getExpirationDate())
                .build();

        Subscription updatedSubscription = subscriptionService.upsert(existingDto);
        assertAll(
                () -> {
                    assertEquals(updatedSubscription.getName(), newName);
                },
                () -> {
                    assertEquals(updatedSubscription.getStatus(), Status.ACTIVE);
                },
                () -> {
                    assertEquals(updatedSubscription.getExpirationDate(), existingDto.getExpirationDate());
                }

        );

    }

    @Test
    void insertSubscription() {
        String newName = "Grigorii";
        CreateSubscriptionDto newDto = CreateSubscriptionDto.builder()
                .userId(35)
                .name(newName)
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(400, ChronoUnit.SECONDS))
                .build();

        Subscription newSubscription = subscriptionService.upsert(newDto);
        assertNotNull(newSubscription.getId());
    }


    @Test
    void cancelThrowsIllegalArgumentException() {
        int invalidSubscriptionId = -100500;
        assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(invalidSubscriptionId));
    }

    @Test
    void cancelThrowsSubscriptionException() {
        Subscription subscriptionExpiredPetr = subscriptionDao.findByUserId(2).get(0);
        Exception ex = assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscriptionExpiredPetr.getId()));
        assertEquals(ex.getMessage(), String.format("Only active subscription %d can be canceled", subscriptionExpiredPetr.getId()));
    }

    @Test
    void cancelSuccess() {
        Subscription subscriptionIvan = subscriptionDao.findByUserId(IVAN.getUserId()).get(0);
        subscriptionService.cancel(subscriptionIvan.getId());

        assertEquals(subscriptionDao.findByUserId(IVAN.getUserId()).get(0).getStatus(), Status.CANCELED);
    }

    @Test
    void expireThrowsIllegalArgumentException() {
        int invalidSubscriptionId = -100500;
        assertThrows(IllegalArgumentException.class, () -> subscriptionService.expire(invalidSubscriptionId));
    }

    @Test
    void expireThrowsSubscriptionException() {
        Subscription subscriptionExpiredPetr = subscriptionDao.findByUserId(2).get(0);
        Exception ex = assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscriptionExpiredPetr.getId()));
        assertEquals(ex.getMessage(), String.format("Subscription %d has already expired", subscriptionExpiredPetr.getId()));
    }

    @Test
    void expireSuccess() {
        Subscription subscriptionIvan = subscriptionDao.findByUserId(IVAN.getUserId()).get(0);
        subscriptionService.expire(subscriptionIvan.getId());

        assertEquals(subscriptionDao.findByUserId(IVAN.getUserId()).get(0).getStatus(), Status.EXPIRED);
    }
}