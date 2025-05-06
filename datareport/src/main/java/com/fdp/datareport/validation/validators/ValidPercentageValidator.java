package com.fdp.datareport.validation.validators;

import com.fdp.datareport.validation.annotations.ValidPercentage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Custom validator to check that the percentage is not negative
public class ValidPercentageValidator implements ConstraintValidator<ValidPercentage, Integer> {

    @Override
    public void initialize(ValidPercentage constraintAnnotation) {
        // Initialization if needed, you can leave it empty for this case
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return value!=null && value >= 0 && value<=100; // Ensure percentage is not negative
    }
}
