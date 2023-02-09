package com.example.quoteservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "quote")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quote {

    @Id
    @Column(name = "quote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteId;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "vote",cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY)
    private List<Vote> votes;


}
