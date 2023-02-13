package com.example.quoteservice.service;

import com.example.quoteservice.model.VoteType;

public interface VoteCounterService {
    void addCountValue(Long quoteId, VoteType voteType);

    void withdrawCounterValue(Long quoteId, VoteType voteType);


    void updateCounterValue(Long quoteId, VoteType voteType);
}
