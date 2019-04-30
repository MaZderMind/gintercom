package de.mazdermind.gintercom.matchers;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ValidatesMatcher extends TypeSafeMatcher<Object> {
	private final Validator validator;

	public ValidatesMatcher() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	public static ValidatesMatcher validates() {
		return new ValidatesMatcher();
	}

	@Override
	protected boolean matchesSafely(Object item) {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(item);
		return constraintViolations.isEmpty();
	}

	@Override
	protected void describeMismatchSafely(Object item, Description description) {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(item);
		description
			.appendText("Does not Validate (")
			.appendValue(constraintViolations.size())
			.appendText(" Violations): ")
			.appendValue(constraintViolations);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("Validates");
	}
}
