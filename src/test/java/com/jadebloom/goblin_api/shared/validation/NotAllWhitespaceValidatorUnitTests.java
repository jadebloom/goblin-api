package com.jadebloom.goblin_api.shared.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class NotAllWhitespaceValidatorUnitTests {

	@Test
	public void GivenValidString_WhenValidating_ThenDoNotThrow() {
		Foo f1 = new Foo(null);
		Foo f2 = new Foo("");
		Foo f3 = new Foo("Valid Name");

		assertAll("Assert that valid strings can be validated",
				() -> assertDoesNotThrow(() -> validate(f1)),
				() -> assertDoesNotThrow(() -> validate(f2)),
				() -> assertDoesNotThrow(() -> validate(f3)));
	}

	@Test
	public void GivenInvalidString_WhenValidating_ThenThrowInvalidString() {
		Foo f1 = new Foo("            ");

		assertAll("Assert that invalid strings can be validated",
				() -> assertThrowsExactly(InvalidString.class, () -> validate(f1)));
	}

	private class Foo {
		@NotAllWhitespace
		String string;

		Foo(String string) {
			this.string = string;
		}
	}

	private class InvalidString extends RuntimeException {
	}

	private void validate(Foo foo) {
		if (!GenericValidator.isValid(foo)) {
			throw new InvalidString();
		}
	}

}
