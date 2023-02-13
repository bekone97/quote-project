package com.example.quoteservice.dto;

import com.example.quoteservice.validator.UserAgeConstraint;
import com.example.quoteservice.validator.ValidUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {


    @Schema(description = "User's email", example = "amdsldmal@mail.ru", implementation = String.class)
    @UserAgeConstraint
    @NotNull(message = "{user.validation.dateOfBirth.notNull}")
    private LocalDate dateOfBirth;

    @Schema(description = "User's date of birth", example = "2000-01-18", pattern = "yyyy-MM-dd", implementation = LocalDate.class)
    @Email(message = "{user.validation.email}")
    private String email;

}
