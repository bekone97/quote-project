package com.example.quoteservice.dto;

import com.example.quoteservice.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDtoResponse {


    private Long voteId;

    private VoteType voteType;

    private String username;

    private LocalDateTime date;


}
