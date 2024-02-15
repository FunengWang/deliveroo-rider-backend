package com.deliveroo.rider.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDatePatternValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface FutureDatePattern {
    String message() default "date either be null or matches the pattern 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss' and be in the future!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
