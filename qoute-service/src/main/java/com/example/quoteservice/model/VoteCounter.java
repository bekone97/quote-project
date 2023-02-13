package com.example.quoteservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteCounterId;


    @Column(name = "counter")
    private long counter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    @ToString.Exclude
    private Quote quote;

    public VoteCounter(Long quoteId) {
        quote=Quote.builder()
                .quoteId(quoteId)
                .build();
        counter=0;
    }
}
