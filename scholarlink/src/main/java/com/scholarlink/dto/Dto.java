package com.scholarlink.dto;

import com.scholarlink.model.User;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Dto {

    // ---- Auth DTOs ----

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private String role;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String token;

        public AuthResponse(Long id, String name, String email, User.Role role, String token) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role.name();
            this.token = token;
        }
    }

    // ---- Scholarship DTOs ----

    @Data
    public static class ScholarshipRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        @NotNull(message = "Amount is required")
        private BigDecimal amount;

        @NotNull(message = "Deadline is required")
        private LocalDate deadline;

        @NotBlank(message = "Eligibility criteria is required")
        private String eligibilityCriteria;
    }

    @Data
    public static class ScholarshipResponse {
        private Long id;
        private String title;
        private String description;
        private BigDecimal amount;
        private LocalDate deadline;
        private String eligibilityCriteria;
        private LocalDateTime createdAt;
    }

    // ---- Application DTOs ----

    @Data
    public static class ApplicationRequest {
        @NotBlank(message = "Application text is required")
        private String applicationText;
    }

    @Data
    public static class ApplicationResponse {
        private Long id;
        private Long studentId;
        private Long scholarshipId;
        private String status;
        private String applicationText;
        private LocalDateTime appliedAt;
    }

    @Data
    public static class MyApplicationResponse {
        private Long id;
        private Long scholarshipId;
        private String title;
        private BigDecimal amount;
        private String status;
        private String applicationText;
        private LocalDateTime appliedAt;
    }

    @Data
    public static class AllApplicationResponse {
        private Long id;
        private String scholarshipTitle;
        private String studentName;
        private String studentEmail;
        private String status;
        private String applicationText;
        private LocalDateTime appliedAt;
    }

    @Data
    public static class StatusUpdateRequest {
        @NotBlank
        private String status;
    }

    @Data
    public static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
    }
}
