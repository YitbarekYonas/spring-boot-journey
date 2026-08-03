package com.JavaBackEnd.spring_boot_journey_week6_day3.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object password = new BeanWrapperImpl(value)
                .getPropertyValue(passwordField);
        Object confirmPassword = new BeanWrapperImpl(value)
                .getPropertyValue(confirmPasswordField);

        if (password == null && confirmPassword == null) {
            return true;
        }

        boolean matches = password != null && password.equals(confirmPassword);

        if (!matches) {
            context.disableDefaultConstraintViolation();
            // ✅ Fixed method name
            context.buildConstraintViolationWithTemplate(
                        context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode(confirmPasswordField)
                   .addConstraintViolation();
        }

        return matches;
    }
}