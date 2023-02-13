package com.example.quoteservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity(name = "vote")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vote {

    @Id
    @Column(name = "vote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @Column(columnDefinition = "VOTE_TYPE")
    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @ManyToOne(fetch = FetchType.LAZY,
                cascade = {CascadeType.MERGE})
    @JoinColumn(name="quote_id")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "username")
    private User user;

    @Column(name = "set_date")
    private Timestamp date;


    @PrePersist
    public void prePersist(){
        date =Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate(){
        date = Timestamp.valueOf(LocalDateTime.now());
    }
}
