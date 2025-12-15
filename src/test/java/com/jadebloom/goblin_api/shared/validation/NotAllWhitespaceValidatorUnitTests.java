package com.jadebloom.goblin_api.shared.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class NotAllWhitespaceValidatorUnitTests {

    @Test
    public void givenValidString_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("Valid Name");

        assertAll("Assert that valid strings can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void givenInvalidString_whenValidating_thenThrowInvalidString() {
        Foo f1 = new Foo("");
        Foo f2 = new Foo("            ");

        assertAll("Assert that invalid strings can be validated",
                () -> assertThrowsExactly(InvalidString.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidString.class, () -> validate(f2)));
    }

    private class Foo {
        @NotAllWhitespace
        String string;

        Foo(String string) {
            this.string = string;
        }
    }

    private class InvalidString extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidString();
        }
    }

}
