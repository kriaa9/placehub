package com.placehub.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StrongPasswordValidator.
 */
@ExtendWith(MockitoExtension.class)
class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    @DisplayName("Should accept valid strong password")
    void shouldAcceptValidStrongPassword() {
        assertTrue(validator.isValid("Password@123", context));
    }

    @Test
    @DisplayName("Should accept null password (let @NotBlank handle it)")
    void shouldAcceptNullPassword() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Should accept blank password (let @NotBlank handle it)")
    void shouldAcceptBlankPassword() {
        assertTrue(validator.isValid("", context));
    }

    @Test
    @DisplayName("Should reject password shorter than 8 characters")
    void shouldRejectShortPassword() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        assertFalse(validator.isValid("Pass@1", context));
        
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password must be at least 8 characters");
    }

    @Test
    @DisplayName("Should reject password without uppercase letter")
    void shouldRejectPasswordWithoutUppercase() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        assertFalse(validator.isValid("password@123", context));
        
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter");
    }

    @Test
    @DisplayName("Should reject password without lowercase letter")
    void shouldRejectPasswordWithoutLowercase() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        assertFalse(validator.isValid("PASSWORD@123", context));
        
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter");
    }

    @Test
    @DisplayName("Should reject password without number")
    void shouldRejectPasswordWithoutNumber() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        assertFalse(validator.isValid("Password@abc", context));
        
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one number");
    }

    @Test
    @DisplayName("Should reject password without special character")
    void shouldRejectPasswordWithoutSpecialChar() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        
        assertFalse(validator.isValid("Password123", context));
        
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password must contain at least one special character (@$!%*?&)");
    }

    @ParameterizedTest
    @DisplayName("Should accept various valid passwords")
    @ValueSource(strings = {
            "Password@123",
            "MyP@ssw0rd!",
            "Str0ng&Pass",
            "Test$1234abc",
            "Secure@Pass1"
    })
    void shouldAcceptVariousValidPasswords(String password) {
        assertTrue(validator.isValid(password, context));
    }

    @ParameterizedTest
    @DisplayName("Should reject various weak passwords")
    @ValueSource(strings = {
            "short",
            "12345678",
            "password",
            "PASSWORD",
            "nospecial1A"
    })
    void shouldRejectVariousWeakPasswords(String password) {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        assertFalse(validator.isValid(password, context));
    }
}
