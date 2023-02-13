package com.example.quoteservice.controller;

import com.example.quoteservice.dto.QuoteDtoRequest;
import com.example.quoteservice.dto.QuoteDtoResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.QuoteService;
import com.example.quoteservice.validator.SortDirectionConstraint;
import com.example.quoteservice.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
@Validated
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping
    @ResponseStatus(OK)
    public Page<QuoteDtoResponse> findAll(Pageable pageable){
        return quoteService.findAll(pageable);
    }

    @GetMapping("/{quoteId}")
    public QuoteDtoResponse getById(@PathVariable @ValidId Long quoteId){
        return quoteService.getById(quoteId);
    }


    @PostMapping
    @ResponseStatus(CREATED)
    public QuoteDtoResponse saveQuote(@Valid @RequestBody QuoteDtoRequest quoteDtoRequest){
        return quoteService.save(quoteDtoRequest);
    }

    @PutMapping("/{quoteId}")
    @ResponseStatus(OK)
    public QuoteDtoResponse updateQuote(@PathVariable @ValidId Long quoteId,
                                        @Valid @RequestBody QuoteDtoRequest quoteDtoRequest,
                                        @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return quoteService.update(quoteId,quoteDtoRequest,authenticatedUser);
    }

    @GetMapping("/top")
    @ResponseStatus(OK)
    public List<QuoteDtoResponse> getTopQuotes( @RequestParam(required = false,
    defaultValue = "ASC") @SortDirectionConstraint String sortDirection){
        return quoteService.getTopQuotes(sortDirection);
    }
    @DeleteMapping("/{quoteId}")
    @ResponseStatus(OK)
    public void deleteQuote(@PathVariable @ValidId Long quoteId,
                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        quoteService.deleteById(quoteId,authenticatedUser);
    }
}
