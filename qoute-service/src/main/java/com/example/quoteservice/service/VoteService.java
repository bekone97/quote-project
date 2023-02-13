package com.example.quoteservice.service;

import com.example.quoteservice.dto.VoteDtoRequest;
import com.example.quoteservice.dto.VoteDtoResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;

import java.util.List;

public interface VoteService {
    List<VoteDtoResponse> findAll(Long quoteId);

    VoteDtoResponse save(VoteDtoRequest voteDtoRequest, Long quoteId);

    VoteDtoResponse update(Long voteId, VoteDtoRequest voteDtoRequest, Long quoteId, AuthenticatedUser authenticatedUser);

    void deleteById(Long voteId, Long quoteId, AuthenticatedUser authenticatedUser);

}
