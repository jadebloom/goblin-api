package com.jadebloom.goblin_api.currency.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CurrencyValidatorsUnitTests {

    @Test
    public void canInvalidateCurrencyNames() {
        CurrencyDto dto1 = new CurrencyDto(null);
        CurrencyDto dto2 = new CurrencyDto("");
        CurrencyDto dto3 = new CurrencyDto("   ");
        CurrencyDto dto4 = new CurrencyDto(
                "pSM]@}}akbWz+5ALx8i:bpQ!x?Y:CF3P]hGT!bmBe[5%60/Y}4n2$.FGMTkbLGSFC");

        assertAll(
                "Assert that currencies with invalid names can be invalidated",
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto3)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto4)));
    }

    @Test
    public void canInvalidateCurrencyAlphabeticalCodes() {
        CurrencyDto dto1 = new CurrencyDto("Dollar");
        dto1.setAlphabeticalCode("AB");

        CurrencyDto dto2 = new CurrencyDto("Dollar");
        dto2.setAlphabeticalCode("!AB");

        CurrencyDto dto3 = new CurrencyDto("Dollar");
        dto3.setAlphabeticalCode("A%B");

        CurrencyDto dto4 = new CurrencyDto("Dollar");
        dto4.setAlphabeticalCode("AB1");

        CurrencyDto dto5 = new CurrencyDto("Dollar");
        dto5.setAlphabeticalCode("ABCD");

        assertAll(
                "Assert that currencies with invalid alphabetical codes can be invalidated",
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto3)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto4)),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> validate(dto5)));
    }

    private <T> void validate(T target) {
        if (!GenericValidator.isValid(target)) {
            String message = GenericValidator.getValidationErrorMessage(target);

            throw new InvalidCurrencyException(message);
        }
    }

}
