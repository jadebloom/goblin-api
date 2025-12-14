package com.jadebloom.goblin_api.currency.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidCurrencyNameUnitTests {

    @Test
    public void givenValidName_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo("1");
        Foo f2 = new Foo("g2kg+-k_f:K{@Cx3t1AfQk;f:#{zfN].9DLxP)2N#S1m}vUQ*tTt,jW&7XTu#.-=");

        assertAll("Assert that valid currency names can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void givenInvalidName_whenValidating_thenThrowInvalidNameException() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("");
        Foo f3 = new Foo("    ");
        Foo f4 = new Foo("w68Hm2P@1Jg7ETqrxeHYw%R9M9KEA.p%zf!E;)F*&?x=gX-8Y$daqGYKD;b8*w#@5");

        assertAll("Assert that invalid currency names can be validated",
                () -> assertThrowsExactly(InvalidNameException.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidNameException.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidNameException.class, () -> validate(f3)),
                () -> assertThrowsExactly(InvalidNameException.class, () -> validate(f4)));
    }

    private class Foo {
        @ValidCurrencyName
        String name;

        Foo(String name) {
            this.name = name;
        }
    }

    private class InvalidNameException extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidNameException();
        }
    }

}
