package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.util.ConnectionManager;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.dmdev.Utils.ALEX;
import static com.dmdev.Utils.IVAN;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoTest extends IntegrationTestBase {
    private SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAllSuccess() {
        List<Subscription> res = subscriptionDao.findAll();
        assertEquals(res.size(), 2);
    }

    @Test
    void findByIdSuccess() {
        int id = subscriptionDao.findAll().stream().findFirst().get().getId();
        Optional<Subscription> optional = subscriptionDao.findById(id);
        assertTrue(optional.isPresent());
        assertEquals(id, optional.get().getId());
    }

    @Test
    void findByIdFailure() {
        Optional<Subscription> optional = subscriptionDao.findById(-1);
        assertTrue(optional.isEmpty());
    }

    @Test
    void deleteSuccess() {
        Subscription firstSubscription = subscriptionDao.findAll().stream().findFirst().get();
        assertTrue(subscriptionDao.delete(firstSubscription.getId()));
    }

    @Test
    void deleteFailure() {
        assertFalse(subscriptionDao.delete(-1));
    }

    @Test
    void updateSuccess() {
//        Subscription newIvan = subscriptionDao.findById(IVAN.getId()).get();
        // do not want to use findById, since BeforeEach method cleans DB and inserts 2 new rows with increasing ids
        // => we have smth like 8,9, but not 1,2 (i.e. IVAN's id = 8 but not 1)
        Subscription newIvan = subscriptionDao.findByUserId(IVAN.getUserId()).stream().findFirst().get();
        newIvan.setProvider(Provider.APPLE);

        subscriptionDao.update(newIvan);

        Subscription updatedIvan = subscriptionDao.findById(newIvan.getId()).get();
        assertEquals(updatedIvan.getProvider(), Provider.APPLE);
    }

    @Test
    void insert() {
        subscriptionDao.insert(ALEX);
        assertEquals(subscriptionDao.findAll().size(), 3);
    }

    @Test
    void findByUserIdSuccess() {
        List<Subscription> subscriptions = subscriptionDao.findByUserId(IVAN.getUserId());
        assertEquals(subscriptions.size(), 1);
    }

    @Test
    void findByUserIdFailure() {
        List<Subscription> subscriptions = subscriptionDao.findByUserId(666);
        assertTrue(subscriptions.isEmpty());
    }
}