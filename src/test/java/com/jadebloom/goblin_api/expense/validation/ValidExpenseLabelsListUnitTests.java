package com.jadebloom.goblin_api.expense.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidExpenseLabelsListUnitTests {

    @Test
    public void givenValidLabelsList_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo(List.of());

        List<String> labelsList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            labelsList.add("" + i);
        }
        Foo f3 = new Foo(labelsList);

        assertAll("Assert that valid expense labels can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)),
                () -> assertDoesNotThrow(() -> validate(f3)));
    }

    @Test
    public void givenInvalidLabelsList_whenValidating_thenThrowInvalidExpenseLabelsList() {
        List<String> labelsList = new ArrayList<>();

        for (int i = 1; i <= 17; i++) {
            labelsList.add("" + i);
        }

        Foo f1 = new Foo(labelsList);

        assertThrowsExactly(InvalidExpenseLabelsList.class, () -> validate(f1));
    }

    private class Foo {
        @ValidExpenseLabelsList
        List<String> labelsList;

        Foo(List<String> labelsList) {
            this.labelsList = labelsList;
        }
    }

    private class InvalidExpenseLabelsList extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidExpenseLabelsList();
        }
    }

}
