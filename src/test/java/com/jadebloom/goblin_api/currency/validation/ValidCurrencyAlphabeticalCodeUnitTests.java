package com.jadebloom.goblin_api.currency.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidCurrencyAlphabeticalCodeUnitTests {

    @Test
    public void givenValidAlphabeticalCode_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("ABC");

        assertAll("Assert that valid alphabetical codes can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void givenInvalidAlphabeticalCode_whenValidating_thenThrowInvalidNameException() {
        Foo f1 = new Foo("");
        Foo f2 = new Foo("   ");
        Foo f3 = new Foo("A");
        Foo f4 = new Foo("AB");
        Foo f5 = new Foo("AB1");
        Foo f6 = new Foo("AB%");
        Foo f7 = new Foo("ABCD");

        assertAll("Assert that invalid alphabetical codes can be validated",
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f3)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f4)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f5)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f6)),
                () -> assertThrowsExactly(InvalidAlphabeticalCodeException.class, () -> validate(f7)));
    }

    private class Foo {
        @ValidCurrencyAlphabeticalCode
        String alphabeticalCode;

        Foo(String alphabeticalCode) {
            this.alphabeticalCode = alphabeticalCode;
        }
    }

    private class InvalidAlphabeticalCodeException extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidAlphabeticalCodeException();
        }
    }

}
