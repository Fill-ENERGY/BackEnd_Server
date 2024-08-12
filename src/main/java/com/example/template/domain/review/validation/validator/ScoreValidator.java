package com.example.template.domain.review.validation.validator;

import com.example.template.domain.review.validation.annotation.ValidScore;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ScoreValidator implements ConstraintValidator<ValidScore, Double> {

    @Override
    public void initialize(ValidScore constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Double aDouble, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = aDouble != null && aDouble <= 5.0 && aDouble >= 0.0;
        if (aDouble == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Score 값이 없습니다.").addConstraintViolation();
        }
        return isValid;
    }
}
