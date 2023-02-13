package com.example.quoteservice.dto;

import com.example.quoteservice.validator.ValidUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDtoRequest {

    @NotNull(message = "{quote.validation.title.notNull}")
    @NotBlank(message = "{quote.validation.title.notBlank}")
    @Schema(description = "Title of quote",example = "Some awesome title",implementation = String.class)
    private String title;

    @Size(min = 1, message = "{quote.validation.content.min}")
    @Size(max = 300, message = "{quote.validation.content.max}")
    @NotBlank(message = "{quote.validation.content.notBlank}")
    @Schema(description = "Some text are written in this quote", implementation = String.class)
    private String content;

    @ValidUsername
    @Schema(description = "User's username who has written this quote", implementation = String.class)
    private String username;

}
