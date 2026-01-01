package com.placehub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the @StrongPassword annotation.
 * Validates that password meets strength requirements:
 * - Minimum 8 characters
 * - At least 1 uppercase letter
 * - At least 1 lowercase letter
 * - At least 1 number
 * - At least 1 special character
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final String PASSWORD_PATTERN = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true; // Let @NotBlank handle null/blank validation
        }
        
        // Check minimum length
        if (password.length() < 8) {
            setCustomMessage(context, "Password must be at least 8 characters");
            return false;
        }
        
        // Check for uppercase
        if (!password.matches(".*[A-Z].*")) {
            setCustomMessage(context, "Password must contain at least one uppercase letter");
            return false;
        }
        
        // Check for lowercase
        if (!password.matches(".*[a-z].*")) {
            setCustomMessage(context, "Password must contain at least one lowercase letter");
            return false;
        }
        
        // Check for digit
        if (!password.matches(".*\\d.*")) {
            setCustomMessage(context, "Password must contain at least one number");
            return false;
        }
        
        // Check for special character
        if (!password.matches(".*[@$!%*?&].*")) {
            setCustomMessage(context, "Password must contain at least one special character (@$!%*?&)");
            return false;
        }
        
        return true;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
