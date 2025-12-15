package com.jadebloom.goblin_api.expense.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidExpenseAmountUnitTests {

    @Test
    public void givenValidAmount_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(1L);
        Foo f2 = new Foo(Long.MAX_VALUE);

        assertAll("Assert that valid expense amounts can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void givenInvalidAmount_whenValidating_thenThrowInvalidExpenseAmount() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo(-1L);
        Foo f3 = new Foo(0L);

        assertAll("Assert that invalid expense amounts can be validated",
                () -> assertThrowsExactly(InvalidExpenseAmount.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidExpenseAmount.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidExpenseAmount.class, () -> validate(f3)));
    }

    private class Foo {
        @ValidExpenseAmount
        Long amount;

        Foo(Long amount) {
            this.amount = amount;
        }
    }

    private class InvalidExpenseAmount extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidExpenseAmount();
        }
    }

}
