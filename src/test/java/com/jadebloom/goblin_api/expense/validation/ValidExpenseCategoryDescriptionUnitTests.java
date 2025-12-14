package com.jadebloom.goblin_api.expense.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@ExtendWith(SpringExtension.class)
public class ValidExpenseCategoryDescriptionUnitTests {

    @Test
    public void givenValidDescription_whenValidating_thenDoNotThrow() {
        Foo f1 = new Foo(null);
        Foo f2 = new Foo("1");
        Foo f3 = new Foo(
                "q=hn:fX#SZd{#GJ#Ke+:ni${MKtQ#=SZ3g3vQ*A:v.@jtif%z}Yc.T3hqwY!unDARF/41u=Q$[LH5w5qSD/gK_.z9%&y!qNJj}TTk}7_q+5}mG_R2h2)Xiy1B6YdV29DKuWur*Nui16{5d*]3Q#=Ht7yn7{Y$v-?_nuwx]_a/V:w]G=XuAY[KTBg.;MZPQG5$XRBk0ihXZG;=1Sh]kz/.Mn3aCF*!iE%yZ,rvq$frTu=#uZ8f7mQ66iL@+?FBLv=");

        assertAll("Assert that valid expense category descriptions can be validated",
                () -> assertDoesNotThrow(() -> validate(f1)),
                () -> assertDoesNotThrow(() -> validate(f2)),
                () -> assertDoesNotThrow(() -> validate(f3)));
    }

    @Test
    public void givenValidDescription_whenValidating_thenThrowInvalidExpenseCategoryDescription() {
        Foo f1 = new Foo("");
        Foo f2 = new Foo("    ");
        Foo f3 = new Foo(
                "P73yHti&+dF5*qh@.5T4-3rT8{PA0NK4S}6wE:={]&Hv(*rW$!Q6LZv8+_%b?BK}6N]MqB)McA.r7#,#-)z&qz6}(9KWH*+.iup-vp%1}?#@{ejL$M%F}!2S)?qS]LGUe/xWK5JY6J56f51Hen1ybEg}F{pygF12R7MY-p7*0,3D(1=S%ccGwiMj/Gh9W#X51pem.$})Z%7Khk@KEbqagf%eFH1Y4482)1h_6r%aYTP{D#f{}F&(C9Pyv=qf3bC.Z");

        assertAll("Assert that invalid expense category descriptions can be validated",
                () -> assertThrowsExactly(InvalidExpenseCategoryDescription.class, () -> validate(f1)),
                () -> assertThrowsExactly(InvalidExpenseCategoryDescription.class, () -> validate(f2)),
                () -> assertThrowsExactly(InvalidExpenseCategoryDescription.class, () -> validate(f3)));
    }

    private class Foo {
        @ValidExpenseCategoryDescription
        String description;

        Foo(String description) {
            this.description = description;
        }
    }

    private class InvalidExpenseCategoryDescription extends RuntimeException {
    }

    private void validate(Foo foo) {
        if (!GenericValidator.isValid(foo)) {
            throw new InvalidExpenseCategoryDescription();
        }
    }

}
