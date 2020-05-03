package de.mazdermind.gintercom.testutils.assertations;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Condition;

public class IsValidCondition extends Condition<Object> {
	public final static IsValidCondition VALID = new IsValidCondition();
	private final Validator validator;

	private IsValidCondition() {
		super("valid");
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Override
	public boolean matches(Object value) {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(value);
		return constraintViolations.isEmpty();
	}
}
