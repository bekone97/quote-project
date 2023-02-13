package com.example.quoteservice.service.impl;

import com.example.quoteservice.dto.VoteDtoRequest;
import com.example.quoteservice.dto.VoteDtoResponse;
import com.example.quoteservice.exception.NotUniqueResourceException;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.mapper.VoteMapper;
import com.example.quoteservice.model.Quote;
import com.example.quoteservice.model.Vote;
import com.example.quoteservice.model.VoteType;
import com.example.quoteservice.repository.VoteRepository;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.QuoteService;
import com.example.quoteservice.service.VoteCounterService;
import com.example.quoteservice.service.VoteService;
import com.example.quoteservice.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.quoteservice.util.ConstantUtil.ValidOperations.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final QuoteService quoteService;
    private final VoteCounterService voteCounterService;


    @Override
    public List<VoteDtoResponse> findAll(Long quoteId) {
        return quoteService.getById(quoteId).getVotes();
    }

    @Override
    @Transactional
    public VoteDtoResponse save(VoteDtoRequest voteDtoRequest, Long quoteId) {
        if (voteRepository.existsByQuoteQuoteIdAndUserUsername(quoteId, voteDtoRequest.getUsername())) {
            throw new NotUniqueResourceException(
                    String.format("Vote already exists with quoteId = %s and username = %s", quoteId, voteDtoRequest.getUsername()));
        } else {
            Vote vote = voteRepository.save(VoteMapper.INSTANCE.convert(voteDtoRequest, quoteId));
            voteCounterService.addCountValue(quoteId, voteDtoRequest.getVoteType());
            return VoteMapper.INSTANCE.convert(vote);
        }
    }

    @Override
    @Transactional
    public VoteDtoResponse update(Long voteId, VoteDtoRequest voteDtoRequest, Long quoteId, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(authenticatedUser,voteDtoRequest.getUsername());
        return voteRepository.findByVoteIdAndQuoteQuoteId(voteId, quoteId)
                .map(vote -> {
                    changeVoteType(vote);
                    voteCounterService.updateCounterValue(quoteId, voteDtoRequest.getVoteType());
                    return vote;
                })
                .map(voteRepository::save)
                .map(VoteMapper.INSTANCE::convert)
                .orElseThrow(() -> new ResourceNotFoundException(Vote.class, "voteId", voteId, Quote.class, "quoteId", quoteId));
    }


    @Override
    public void deleteById(Long voteId, Long quoteId, AuthenticatedUser authenticatedUser) {

        Vote vote = voteRepository.findByVoteIdAndQuoteQuoteId(voteId, quoteId)
                .orElseThrow(() -> new ResourceNotFoundException(Vote.class, "voteId", voteId, Quote.class, "quoteId", quoteId));
        checkValidCredentials(authenticatedUser,vote.getUser().getUsername());
        voteRepository.deleteById(vote.getVoteId());
        voteCounterService.withdrawCounterValue(quoteId, vote.getVoteType());
    }

    private Vote changeVoteType(Vote vote) {
        if (vote.getVoteType().equals(VoteType.UP)) {
            vote.setVoteType(VoteType.DOWN);
        } else {
            vote.setVoteType(VoteType.UP);
        }
        return vote;
    }
}
