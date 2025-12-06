package com.jadebloom.goblin_api.currency.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.jadebloom.goblin_api.currency.dto.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CurrencyEntityValidatorUnitTests {

    @Test
    public void canCatchInvalidCurrencyName() {
        CurrencyEntity entity1 = new CurrencyEntity(null);
        CurrencyEntity entity2 = new CurrencyEntity("  ");
        CurrencyEntity entity3 = new CurrencyEntity(
                "#LF8+.C;ANRJXR4%LgVzxMPJBk.8L/1a3T-f_9NHxTq$p4h}$wdX(vTQ_ASYc$fS["); // 65 chars

        assertAll(
                "Assert that currency entity validator can catch invalid currency names",
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity1, "name");
                }),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity2, "name");
                }),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity3, "name");
                }));
    }

    @Test
    public void canCatchInvalidCurrencyAlphabeticalCode() {
        CurrencyEntity entity1 = new CurrencyEntity("Dollar", "   ");
        CurrencyEntity entity2 = new CurrencyEntity("Dollar", "A");
        CurrencyEntity entity3 = new CurrencyEntity("Dollar", "AB");
        CurrencyEntity entity4 = new CurrencyEntity("Dollar", "ABCD");

        assertAll(
                "Assert that currency entity validator can catch invalid currency alphabetical codes",
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity1, "alphabeticalCode");
                }),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity2, "alphabeticalCode");
                }),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity3, "alphabeticalCode");
                }),
                () -> assertThrowsExactly(InvalidCurrencyException.class, () -> {
                    CurrencyEntityValidator.validateProperty(entity4, "alphabeticalCode");
                }));
    }

}
