package com.example.quoteservice.controller;

import com.example.quoteservice.dto.VoteDtoRequest;
import com.example.quoteservice.dto.VoteDtoResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.VoteService;
import com.example.quoteservice.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/quotes/{quoteId}/votes")
@RequiredArgsConstructor
@Validated
public class VoteController {

    private final VoteService voteService;

    @GetMapping
    @ResponseStatus(OK)
    public List<VoteDtoResponse> findAll(@PathVariable @ValidId Long quoteId){
        return voteService.findAll(quoteId);
    }


    @PostMapping
    @ResponseStatus(CREATED)
    public VoteDtoResponse save(@Valid @RequestBody VoteDtoRequest voteDtoRequest,
                                @PathVariable @ValidId Long quoteId){
        return voteService.save(voteDtoRequest,quoteId);
    }

    @PutMapping("/{voteId}")
    @ResponseStatus(OK)
    public VoteDtoResponse update(@Valid @RequestBody VoteDtoRequest voteDtoRequest,
                                             @PathVariable @ValidId Long quoteId,
                                             @PathVariable @ValidId Long voteId,
                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return voteService.update(voteId,voteDtoRequest,quoteId,authenticatedUser);
    }

    @DeleteMapping("/{voteId}")
    @ResponseStatus(OK)
    public void deleteById(@PathVariable @ValidId Long voteId,
                           @PathVariable @ValidId Long quoteId,
                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        voteService.deleteById(voteId,quoteId,authenticatedUser);
    }
}
