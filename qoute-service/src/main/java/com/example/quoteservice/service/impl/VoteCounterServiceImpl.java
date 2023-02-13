package com.example.quoteservice.service.impl;

import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.model.VoteType;
import com.example.quoteservice.repository.VoteCounterRepository;
import com.example.quoteservice.service.VoteCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteCounterServiceImpl implements VoteCounterService {

    private final VoteCounterRepository voteCounterRepository;

    public void addCountValue(Long quoteId, VoteType voteType) {
        voteCounterRepository.findByQuoteQuoteId(quoteId)
                .map(voteCounter-> {
                    voteCounter.setCounter(voteCounter.getCounter()+ voteType.getValue());
                    return voteCounterRepository.save(voteCounter);
                })
                .orElseThrow(()->new ResourceNotFoundException(VoteCounterService.class,"quote_id", voteType));
    }

    @Override
    public void withdrawCounterValue(Long quoteId, VoteType voteType) {
        voteCounterRepository.findByQuoteQuoteId(quoteId)
                .map(voteCounter-> {
                    voteCounter.setCounter(voteCounter.getCounter()- voteType.getValue());
                    return voteCounterRepository.save(voteCounter);
                })
                .orElseThrow(()->new ResourceNotFoundException(VoteCounterService.class,"quote_id", voteType));
    }


    @Override
    public void updateCounterValue(Long quoteId, VoteType voteType) {
        voteCounterRepository.findByQuoteQuoteId(quoteId)
                .map(voteCounter-> {
                    voteCounter.setCounter(voteCounter.getCounter()+ 2L *voteType.getValue());
                    return voteCounterRepository.save(voteCounter);
                })
                .orElseThrow(()->new ResourceNotFoundException(VoteCounterService.class,"quote_id", voteType));
    }
}
