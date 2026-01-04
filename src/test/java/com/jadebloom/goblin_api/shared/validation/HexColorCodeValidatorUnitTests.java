package com.jadebloom.goblin_api.shared.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HexColorCodeValidatorUnitTests {

	@Test
	public void GivenValidHexColorCodes_WhenValidating_thenDoNotThrow() {
		Foo f1 = new Foo("#FF1");
		Foo f2 = new Foo("#FF0000");
		Foo f3 = new Foo(null);

		assertAll("Assert that valid hex color codes can be validated",
				() -> assertDoesNotThrow(() -> validate(f1)),
				() -> assertDoesNotThrow(() -> validate(f2)),
				() -> assertDoesNotThrow(() -> validate(f3)));
	}

	@Test
	public void GivenInvalidHexColorCode_WhenValidating_ThenThrowInvalidHexColorCodeException() {
		Foo f1 = new Foo("FF00000");
		Foo f2 = new Foo("#FF%123");
		Foo f3 = new Foo("#FF123");

		assertAll("Assert that invalid hex color codes can be validated",
				() -> assertThrowsExactly(InvalidHexColorCodeException.class, () -> validate(f1)),
				() -> assertThrowsExactly(InvalidHexColorCodeException.class, () -> validate(f2)),
				() -> assertThrowsExactly(InvalidHexColorCodeException.class, () -> validate(f3)));
	}

	private class Foo {
		@ValidHexColorCode
		String hexColorCode;

		Foo(String hexColorCode) {
			this.hexColorCode = hexColorCode;
		}
	}

	private class InvalidHexColorCodeException extends RuntimeException {
	}

	private void validate(Foo foo) {
		if (!GenericValidator.isValid(foo)) {
			throw new InvalidHexColorCodeException();
		}
	}

}
