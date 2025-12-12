package com.jadebloom.goblin_api.expense.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ExpenseValidationUnitTests {

    @Test
    public void canInvalidateExpenseId() {
        ExpenseDto dto = createDefaultExpenseDto();

        dto.setId(null);

        assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto));
    }

    @Test
    public void canInvalidateExpenseName() {
        ExpenseDto dto1 = createDefaultExpenseDto();
        ExpenseDto dto2 = createDefaultExpenseDto();
        ExpenseDto dto3 = createDefaultExpenseDto();
        ExpenseDto dto4 = createDefaultExpenseDto();

        dto1.setName(null);
        dto2.setName("");
        dto3.setName("   ");
        dto4.setName("6SPn06uQYv%x(xcWb:+}C}nG].w?R{@aSBv-]@$WDV3P%b/p5]BamnvfMu$7R}im%");

        assertAll(
                "Assert that an expense's name can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto3)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto4)));
    }

    @Test
    public void canInvalidateExpenseDescription() {
        ExpenseDto dto1 = createDefaultExpenseDto();
        ExpenseDto dto2 = createDefaultExpenseDto();
        ExpenseDto dto3 = createDefaultExpenseDto();

        dto1.setDescription("");
        dto2.setName("  ");
        dto3.setName(
                "P5_cRR%Y$nC6}nM,h,b&Lquz{kiFBWL_?7TEEkCK*L=r:zwFhVwA&H06LG8xpe_A?,wh@gmEq5;ar$ku4b5fnEdt)!MQRxj{)m8k]kgmMfjvC*}9{Y,47nQLU0mn9nx1P[eta2{y.xSz_&?7j5FHx_p:;wG_uz_MN@_1wyXG2H6w@5X7:Pc+pr0iPKEyiEX}rjh5y=7Lr8ZC+?b?)NHxBAfKHje)K*;T?+,?jSYJ;&wG97UB=2]&HYgj/}ME8%+Hn");

        assertAll(
                "Assert that an expense's description can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto3)));
    }

    @Test
    public void canInvalidateExpenseAmount() {
        ExpenseDto dto1 = createDefaultExpenseDto();
        ExpenseDto dto2 = createDefaultExpenseDto();
        ExpenseDto dto3 = createDefaultExpenseDto();

        dto1.setAmount(null);
        dto2.setAmount(0);
        dto3.setAmount(Integer.MAX_VALUE + 1);

        assertAll(
                "Assert that an expense's amount can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto3)));
    }

    @Test
    public void canInvalidateExpenseLabels() {
        ExpenseDto dto1 = createDefaultExpenseDto();
        ExpenseDto dto2 = createDefaultExpenseDto();
        ExpenseDto dto3 = createDefaultExpenseDto();
        ExpenseDto dto4 = createDefaultExpenseDto();

        dto1.setLabels(List.of("", "label1"));
        dto2.setLabels(List.of("  ", "label1"));
        dto3.setLabels(List.of("an#F3h&:,vhyu7W9p}c#2tJvGV&,[F?C4", "label1"));

        List<String> testLabels = new ArrayList<>();
        for (int i = 1; i <= 17; i++) {
            testLabels.add("" + i);
        }
        dto4.setLabels(testLabels);

        assertAll(
                "Assert that an expense's labels can be invalidated",
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto1)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto2)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto3)),
                () -> assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto4)));
    }

    @Test
    public void canInvalidateExpenseExpenseCategoryId() {
        ExpenseDto dto = createDefaultExpenseDto();

        dto.setExpenseCategoryId(null);

        assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto));
    }

    @Test
    public void canInvalidateExpenseCurrencyId() {
        ExpenseDto dto = createDefaultExpenseDto();

        dto.setCurrencyId(null);

        assertThrowsExactly(InvalidExpenseException.class, () -> validate(dto));
    }

    private <T> void validate(T target) {
        System.out.println(target);

        if (!GenericValidator.isValid(target)) {
            String message = GenericValidator.getValidationErrorMessage(target);

            throw new InvalidExpenseException(message);
        }
    }

    private ExpenseDto createDefaultExpenseDto() {
        ExpenseDto dto = new ExpenseDto(
                1L,
                "Uber Ride",
                100,
                1L,
                1L);

        dto.setDescription("Magnifient silent ride");
        dto.setLabels(List.of("label1", "label2"));

        return dto;
    }

}
