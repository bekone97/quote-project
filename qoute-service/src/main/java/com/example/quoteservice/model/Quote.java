package com.example.quoteservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "quote")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "quote-entity-graph",
                attributeNodes = {
                        @NamedAttributeNode(value = "votes"),
                        @NamedAttributeNode(value = "voteCounter")
                }
        )
})
public class Quote {

    @Id
    @Column(name = "quote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private User user;

    @OneToOne(mappedBy = "quote",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private VoteCounter voteCounter;

    @OneToMany(mappedBy = "quote",cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Vote> votes;

    private Timestamp dateOfPost;

    @PrePersist
    public void prePersist(){
        dateOfPost=Timestamp.valueOf(LocalDateTime.now());
    }
    @PostPersist
    public void postPersist(){
        voteCounter=new VoteCounter(quoteId);
    }
}
