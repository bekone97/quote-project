package com.example.quoteservice.model;

import lombok.Getter;

@Getter
public enum VoteType {
    UP(1), DOWN(-1);
    private Integer value;

    VoteType(int value) {
        this.value = value;
    }
}
