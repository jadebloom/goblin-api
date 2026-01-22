package com.jadebloom.goblin_api.shared.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ValidCurrencyCodeValidatorUnitTests {

	@Test
	public void GivenValidCurrencyCode_WhenValidating_ThenDoNotThrow() {
		Foo f1 = new Foo("USD");
		Foo f2 = new Foo("RUB");

		assertAll("Assert that valid currency codes can be validated",
				() -> assertDoesNotThrow(() -> validate(f1)),
				() -> assertDoesNotThrow(() -> validate(f2)));
	}

	@Test
	public void GivenInvalidCurrencyCode_WhenValidating_ThenThrowInvalidCurrencyCodeException() {
		Foo f1 = new Foo(null);
		Foo f2 = new Foo("");
		Foo f3 = new Foo("AZ");
		Foo f4 = new Foo("A");
		Foo f5 = new Foo("AZCC");
		Foo f6 = new Foo("1AB");
		Foo f7 = new Foo("BA4");

		assertAll("Assert that invalid currency codes can be validated",
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f1)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f2)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f3)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f4)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f5)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f6)),
				() -> assertThrowsExactly(InvalidCurrencyCodeException.class, () -> validate(f7)));
	}

	private class Foo {
		@ValidCurrencyCode
		String currencyCode;

		Foo(String currencyCode) {
			this.currencyCode = currencyCode;
		}
	}

	private class InvalidCurrencyCodeException extends RuntimeException {
	}

	private void validate(Foo foo) {
		if (!GenericValidator.isValid(foo)) {
			throw new InvalidCurrencyCodeException();
		}
	}

}
