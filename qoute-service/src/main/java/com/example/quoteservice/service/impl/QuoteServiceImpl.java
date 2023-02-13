package com.example.quoteservice.service.impl;

import com.example.quoteservice.dto.QuoteDtoRequest;
import com.example.quoteservice.dto.QuoteDtoResponse;
import com.example.quoteservice.dto.VoteDtoResponse;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.mapper.QuoteMapper;
import com.example.quoteservice.mapper.VoteMapper;
import com.example.quoteservice.model.Quote;
import com.example.quoteservice.repository.QuoteRepository;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.QuoteService;
import com.example.quoteservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.quoteservice.util.ConstantUtil.ValidOperations.checkValidCredentials;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final UserService userService;


    @Override
    public Page<QuoteDtoResponse> findAll(Pageable pageable) {
        return quoteRepository.findAll(pageable)
                .map(this::getQuoteDtoResponse);
    }

    @Override
    @Cacheable(value = "quote", key = "#quoteId")
    public QuoteDtoResponse getById(Long quoteId) {
        return quoteRepository.findQuoteByQuoteId(quoteId)
                .map(this::getQuoteDtoResponse)
                .orElseThrow(() -> new ResourceNotFoundException(Quote.class, "quoteId", quoteId));
    }

    @Override
    @Transactional
    public QuoteDtoResponse save(QuoteDtoRequest quoteDtoRequest) {
        userService.getById(quoteDtoRequest.getUsername());
        Quote quote = quoteRepository.save(QuoteMapper.INSTANCE.convert(quoteDtoRequest));
        return getQuoteDtoResponse(quote);
    }

    @Override
    @Transactional
    @CachePut(value = "quote", key = "#quoteId")
    public QuoteDtoResponse update(Long quoteId, QuoteDtoRequest quoteDtoRequest, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(authenticatedUser, quoteDtoRequest.getUsername());
        return quoteRepository.findQuoteByQuoteId(quoteId)
                .map(quote -> QuoteMapper.INSTANCE.convert(quoteDtoRequest, quote))
                .map(quoteRepository::save)
                .map(this::getQuoteDtoResponse)
                .orElseThrow(() -> new ResourceNotFoundException(Quote.class, "quoteId", quoteId));
    }

    @Override
    @Transactional
    @CacheEvict(value = "quote", key = "#quoteId")
    public void deleteById(Long quoteId, AuthenticatedUser authenticatedUser) {
        QuoteDtoResponse quote = getById(quoteId);
        checkValidCredentials(authenticatedUser, quote.getUsername());
        quoteRepository.deleteById(quote.getQuoteId());
    }


    @Override
    public List<QuoteDtoResponse> getTopQuotes(String sortDirection) {
        Sort sort = sortDirection.equals("ASC") || sortDirection.equals("DESC") ?
                Sort.by(Sort.Direction.valueOf(sortDirection), "voteCounter.counter") :
                Sort.by(Sort.DEFAULT_DIRECTION, "voteCounter.counter");
        Pageable pageable = PageRequest.of(0, 10, sort);
        return quoteRepository.findAll(pageable)
                .stream()
                .map(this::getQuoteDtoResponse)
                .collect(Collectors.toList());
    }


    private QuoteDtoResponse getQuoteDtoResponse(Quote quote) {
        List<VoteDtoResponse> votes = quote.getVotes().stream()
                .map(VoteMapper.INSTANCE::convert)
                .collect(Collectors.toList());
        return QuoteMapper.INSTANCE.convert(quote,
                votes,
                getVoteCounter(votes));
    }

    private long getVoteCounter(List<VoteDtoResponse> votes) {
        return votes.stream()
                .mapToLong(vote -> vote.getVoteType().getValue())
                .sum();
    }
}
