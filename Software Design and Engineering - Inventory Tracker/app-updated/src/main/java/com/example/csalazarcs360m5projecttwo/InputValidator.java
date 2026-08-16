package com.example.csalazarcs360m5projecttwo;

import android.text.TextUtils;

// InputValidator
// Validates user input before it is processed.
public class InputValidator {

    // ValidationResult
    // Stores the result of a validation check.
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    // validateItem
    // Validates a new inventory item.
    public static ValidationResult validateItem(String itemName, String quantityText, String description) {

        if (TextUtils.isEmpty(itemName)) {
            return ValidationResult.failure("Item name cannot be empty.");
        }

        if (TextUtils.isEmpty(quantityText)) {
            return ValidationResult.failure("Quantity cannot be empty.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText.trim());
        } catch (NumberFormatException e) {
            return ValidationResult.failure("Quantity must be a whole number.");
        }

        if (quantity < 0) {
            return ValidationResult.failure("Quantity cannot be negative.");
        }

        if (description != null && description.length() > 250) {
            return ValidationResult.failure("Description is too long (250 characters max).");
        }

        return ValidationResult.success();
    }

    // validateQuantity
    // Validates an inventory quantity.
    public static ValidationResult validateQuantity(String quantityText) {

        if (TextUtils.isEmpty(quantityText)) {
            return ValidationResult.failure("Quantity cannot be empty.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText.trim());
        } catch (NumberFormatException e) {
            return ValidationResult.failure("Quantity must be a whole number.");
        }

        if (quantity < 0) {
            return ValidationResult.failure("Quantity cannot be negative.");
        }

        return ValidationResult.success();
    }

    // validateCredentials
    // Validates login credentials.
    public static ValidationResult validateCredentials(String username, String password) {

        if (TextUtils.isEmpty(username)) {
            return ValidationResult.failure("Username cannot be empty.");
        }

        if (TextUtils.isEmpty(password)) {
            return ValidationResult.failure("Password cannot be empty.");
        }

        if (password.length() < 6) {
            return ValidationResult.failure("Password must be at least 6 characters.");
        }

        return ValidationResult.success();
    }
}