package com.deliveroo.rider.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class FutureDatePatternValidator implements ConstraintValidator<FutureDatePattern, LocalDateTime> {
    private static final Pattern PATTERN = Pattern.compile("^$|^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

    @Override
    public void initialize(FutureDatePattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return (PATTERN.matcher(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).matches()) &&
                value.isAfter(LocalDateTime.now());
    }
}
