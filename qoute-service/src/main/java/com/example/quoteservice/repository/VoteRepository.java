package com.example.quoteservice.repository;

import com.example.quoteservice.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<Vote> findByVoteIdAndQuoteQuoteId(Long voteId, Long quoteId);

    boolean existsByQuoteQuoteIdAndUserUsername(Long quoteId, String username);
}
