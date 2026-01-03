package com.jadebloom.goblin_api.security.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidUserEmailUnitTests {

    @Test
    public void GivenValidUserEmails_WhenValidating_ThenDoNotThrow() {
        Foo f1 = new Foo("user@gmail.com");
        Foo f2 = new Foo("u@g.g");

        assertAll("Assert that valid user emails can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void GivenInvalidUserEmails_WhenValidating_ThenThrowInvalidUserEmailException() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("");
        Foo f3 = new Foo("  ");
        Foo f4 = new Foo("K}@XP*@7a36BjBvyQQ&t2.hjS]4NYcom");
        Foo f5 = new Foo("K}@XP*@7a36BjBvyQQ&t2.hjS]4NN@com");

        assertAll("Assert that invalid user emails can be validated",
                () -> assertThrowsExactly(InvalidUserEmail.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidUserEmail.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidUserEmail.class, () -> validate(f3)),
                () -> assertThrowsExactly(InvalidUserEmail.class, () -> validate(f4)),
                () -> assertThrowsExactly(InvalidUserEmail.class, () -> validate(f5)));
    }

    private class Foo {
        @ValidUserEmail
        String email;

        Foo(String email) {
            this.email = email;
        }
    }

    private class InvalidUserEmail extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidUserEmail();
        }
    }

}
