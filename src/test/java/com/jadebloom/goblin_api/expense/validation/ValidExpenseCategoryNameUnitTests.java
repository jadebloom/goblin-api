package com.jadebloom.goblin_api.expense.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidExpenseCategoryNameUnitTests {

    @Test
    public void givenValidName_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo("Name");
        Foo f2 = new Foo("Z%]f/D]/p@2R1hQ+rCVFCC1ccu[xWnc7TF-23Qy}WV&nV!0qvB3iuWeE+:):NFy!");

        assertAll("Assert that valid expense category names can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)));
    }

    @Test
    public void givenInvalidName_whenValidating_thenThrowInvalidExpenseCategoryName() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("");
        Foo f3 = new Foo("    ");
        Foo f4 = new Foo("*L}]7yD7,8bpC15+#(igC-]2)Qx74Q%cx6]![;K2-USUR_MKrxQ.pU@m+MqH2mJ:=");

        assertAll("Assert that invalid expense category names can be validated",
                () -> assertThrowsExactly(InvalidExpenseCategoryName.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidExpenseCategoryName.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidExpenseCategoryName.class, () -> validate(f3)),
                () -> assertThrowsExactly(InvalidExpenseCategoryName.class, () -> validate(f4)));
    }

    private class Foo {
        @ValidExpenseCategoryName
        String name;

        Foo(String name) {
            this.name = name;
        }
    }

    private class InvalidExpenseCategoryName extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidExpenseCategoryName();
        }
    }

}
