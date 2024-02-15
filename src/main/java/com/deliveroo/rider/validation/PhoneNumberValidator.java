package com.deliveroo.rider.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber,String> {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(
            "^(\\+[0-9]{1,3})?([0-9\\-]?){6,14}[0-9]$"
    );

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return phoneNumber != null && PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}
