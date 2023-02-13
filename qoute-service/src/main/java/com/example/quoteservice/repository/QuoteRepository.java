package com.example.quoteservice.repository;

import com.example.quoteservice.model.Quote;
import jakarta.persistence.NamedNativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote,Long>{

    @EntityGraph(value = "quote-entity-graph")
    List<Quote> findAllById(Iterable<Long> ids);

    @EntityGraph(value = "quote-entity-graph")
    Page<Quote> findAll(Pageable pageable);

    @EntityGraph(value = "quote-entity-graph")
    Optional<Quote> findQuoteByQuoteId(Long quoteId);

}
