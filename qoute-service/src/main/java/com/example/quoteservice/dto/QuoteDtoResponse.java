package com.example.quoteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDtoResponse {

    private Long quoteId;

    private String title;

    private String content;

    private String username;

    private Long voteCounter;

    private List<VoteDtoResponse> votes;

    private LocalDateTime dateOfPost;

}
