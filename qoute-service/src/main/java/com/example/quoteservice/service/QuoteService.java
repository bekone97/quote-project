package com.example.quoteservice.service;

import com.example.quoteservice.dto.QuoteDtoRequest;
import com.example.quoteservice.dto.QuoteDtoResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuoteService {
    Page<QuoteDtoResponse> findAll(Pageable pageable);

    QuoteDtoResponse save(QuoteDtoRequest quoteDtoRequest);

    QuoteDtoResponse update(Long quoteId, QuoteDtoRequest quoteDtoRequest, AuthenticatedUser authenticatedUser);

    void deleteById(Long quoteId, AuthenticatedUser authenticatedUser);

    List<QuoteDtoResponse> getTopQuotes(String sort_direction);

    QuoteDtoResponse getById(Long quoteId);

}
