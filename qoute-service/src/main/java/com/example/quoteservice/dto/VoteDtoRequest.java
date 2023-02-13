package com.example.quoteservice.dto;

import com.example.quoteservice.model.VoteType;
import com.example.quoteservice.validator.ValidUsername;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDtoRequest {

    @NotNull(message = "{vote.validation.voteType.notNull}")
    private VoteType voteType;

    @ValidUsername
    private String username;


}
