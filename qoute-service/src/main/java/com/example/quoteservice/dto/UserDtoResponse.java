package com.example.quoteservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {

    @Schema(description = "User's username", example = "Arteminio", implementation = String.class)
    private String username;
    @Schema(description = "User's date of birth", example = "2000-01-18", pattern = "yyyy-MM-dd", implementation = LocalDate.class)
    private LocalDate dateOfBirth;
    @Schema(description = "User's list of quotes", implementation = QuoteDtoResponse.class)
    private List<QuoteDto> quotes;

    @Schema(description = "User's email", example = "amdsldmal@mail.ru", implementation = String.class)
    private String email;


}
