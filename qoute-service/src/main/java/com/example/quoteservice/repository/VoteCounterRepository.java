package com.example.quoteservice.repository;

import com.example.quoteservice.model.VoteCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VoteCounterRepository extends JpaRepository<VoteCounter,Long> {

    Optional<VoteCounter> findByQuoteQuoteId(Long quoteId);

}
