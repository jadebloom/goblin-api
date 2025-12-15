package com.jadebloom.goblin_api.expense.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidExpenseLabelUnitTests {

    @Test
    public void givenValidLabel_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("Valid");
        Foo f3 = new Foo("Hy*m}dQ0A};*&nD7cAj{z,XfenZ5vc%7");

        assertAll("Assert that valid expense labels can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)),
                () -> assertDoesNotThrow(() -> validate(f3)));
    }

    @Test
    public void givenInvalidLabel_whenValidating_thenThrowInvalidExpenseLabel() {
        Foo f1 = new Foo("");
        Foo f2 = new Foo("   ");
        Foo f3 = new Foo("wieZ%6Kt?FXx;a]Eh$12S0WpGjpmrcc?2");

        assertAll("Assert that invalid expense labels can be validated",
                () -> assertThrowsExactly(InvalidExpenseLabel.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidExpenseLabel.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidExpenseLabel.class, () -> validate(f3)));
    }

    private class Foo {
        @ValidExpenseLabel
        String label;

        Foo(String label) {
            this.label = label;
        }
    }

    private class InvalidExpenseLabel extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidExpenseLabel();
        }
    }

}
