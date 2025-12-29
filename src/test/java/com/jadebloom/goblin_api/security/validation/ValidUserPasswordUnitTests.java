package com.jadebloom.goblin_api.security.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidUserPasswordUnitTests {

    @Test
    public void GivenValidUserPasswords_WhenValidating_ThenDoNotThrow() {
        Foo f1 = new Foo("Pad!?_$#%^&*1");
        Foo f2 = new Foo("123456789_123456789_123456789Wqa");

        assertAll("Assert that valid user passwords can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void GivenInvalidUserPasswords_WhenValidating_ThenThrowInvalidUserPasswordException() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("");
        Foo f3 = new Foo("  ");
        Foo f4 = new Foo("Pad!?_$#%^&*1+");
        Foo f5 = new Foo("123456789_123456789_123456789Wqa12313213");

        assertAll("Assert that invalid user passwords can be validated",
                () -> assertThrowsExactly(InvalidUserPasswordException.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidUserPasswordException.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidUserPasswordException.class, () -> validate(f3)),
                () -> assertThrowsExactly(InvalidUserPasswordException.class, () -> validate(f4)),
                () -> assertThrowsExactly(InvalidUserPasswordException.class, () -> validate(f5)));
    }

    private class Foo {
        @ValidUserPassword
        String password;

        Foo(String password) {
            this.password = password;
        }
    }

    private class InvalidUserPasswordException extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidUserPasswordException();
        }
    }

}
