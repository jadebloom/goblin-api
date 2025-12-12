package com.jadebloom.goblin_api.expense.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ExpenseCategoryValidationUnitTests {

    @Test
    public void canInvalidateExpenseCategoryName() {
        ExpenseCategoryDto dto1 = new ExpenseCategoryDto(null);
        ExpenseCategoryDto dto2 = new ExpenseCategoryDto("   ");
        ExpenseCategoryDto dto3 = new ExpenseCategoryDto(
                "U9uezDG7=6g==U?XR{Y)B35Vi6vb5YaBrzQ4Zb.idjdp.Z29=X[ru72%V%KBRGNZ)");

        assertAll(
                "Assert that expense category name can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseCategoryException.class, () -> {
                    validate(dto1);
                }),
                () -> assertThrowsExactly(InvalidExpenseCategoryException.class, () -> {
                    validate(dto2);
                }),
                () -> assertThrowsExactly(InvalidExpenseCategoryException.class, () -> {
                    validate(dto3);
                }));
    }

    @Test
    public void canInvalidateExpenseCategoryDescription() {
        ExpenseCategoryDto dto1 = new ExpenseCategoryDto("1", "");
        ExpenseCategoryDto dto2 = new ExpenseCategoryDto("1",
                ".]%$fZR!Yh%rt{qyeYiBjdXb!bGtHtM$t&J].CYUWxdQBDzL=FN-i=3GL8=D(kvBUq-XExgn{}{!-ahZ-ubp*RVcJ+[+Gp}!Au#{%J4y?(F)/((fu&t[G-0DAqvGEz-L,)u6zjA08.q+C#3J6gcQ+RZ/;:{tB6KZ.@j,KmEKD{--z@(*na7bNz?5VX#hfh?qx09iuf+/3+RkkAjBq5w]:C8dMz[xdAmY{L6-jqrvi)g:Tfh(Mj8TPZK[2?{-5u_[e");

        assertAll(
                "Assert that expense category description can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseCategoryException.class, () -> {
                    validate(dto1);
                }),
                () -> assertThrowsExactly(InvalidExpenseCategoryException.class, () -> {
                    validate(dto2);
                }));
    }

    private <T> void validate(T target) {
        if (!GenericValidator.isValid(target)) {
            String message = GenericValidator.getValidationErrorMessage(target);

            throw new InvalidExpenseCategoryException(message);
        }
    }

}
