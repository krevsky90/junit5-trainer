package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {
    private final CreateSubscriptionValidator createSubscriptionValidator = CreateSubscriptionValidator.getInstance();

    @Test
    void validateValidObject() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(100, ChronoUnit.SECONDS))
                .build();

        ValidationResult validationResult = createSubscriptionValidator.validate(subscriptionDto);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void validateInvalidObjectWithSeveralProblems() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Ivan")
                .provider("wrongProvider")
                .expirationDate(null)
                .build();

        ValidationResult validationResult = createSubscriptionValidator.validate(subscriptionDto);

        assertEquals(validationResult.getErrors().size(), 3);
        assertTrue(validationResult.getErrors().stream().map((e) -> e.getCode()).toList().contains(100));   //check existence of error code of incorrect name
    }

    @MethodSource("getInvalidDataForValidateMethod")
    @ParameterizedTest
    void validateDifferentErrorCases(Integer userId, String name, String provider, Instant expirationDate, Error expectedError) {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(userId)
                .name(name)
                .provider(provider)
                .expirationDate(expirationDate)
                .build();

        ValidationResult validationResult = createSubscriptionValidator.validate(subscriptionDto);

        assertEquals(validationResult.getErrors().stream().findFirst().get(), expectedError);
        assertEquals(validationResult.getErrors().stream().findFirst().get(), expectedError);
        assertEquals(validationResult.getErrors().stream().findFirst().get(), expectedError);
        assertEquals(validationResult.getErrors().stream().findFirst().get(), expectedError);
        assertEquals(validationResult.getErrors().stream().findFirst().get(), expectedError);
    }

    private static Stream<Arguments> getInvalidDataForValidateMethod() {
        return Stream.of(
                Arguments.of(null, "dummyName", "dummyProvider", Instant.now(), Error.of(100, "userId is invalid")),
                Arguments.of(1, "", "dummyProvider", Instant.now(), Error.of(101, "name is invalid")),
                Arguments.of(1, "dummyName", "wrongProvider", Instant.now(), Error.of(102, "provider is invalid")),
                Arguments.of(1, "dummyName", Provider.GOOGLE.name(), Instant.now().minus(1000, ChronoUnit.SECONDS), Error.of(103, "expirationDate is invalid")),
                Arguments.of(1, "dummyName", Provider.GOOGLE.name(), null, Error.of(103, "expirationDate is invalid"))
        );
    }
}