package com.example.quoteservice.mapper;

import com.example.quoteservice.dto.*;
import com.example.quoteservice.model.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QuoteMapper {
    QuoteMapper INSTANCE = Mappers.getMapper(QuoteMapper.class);

    @Mappings({
            @Mapping(source = "newQuote.content",target ="content"),
            @Mapping(source = "oldQuote.user",target ="user"),
            @Mapping(source = "oldQuote.votes",target = "votes"),
            @Mapping(source = "newQuote.title",target = "title"),
            @Mapping(source = "oldQuote.quoteId",target = "quoteId"),
            @Mapping(source = "oldQuote.dateOfPost",target = "dateOfPost")
    })
    Quote convert(QuoteDtoRequest newQuote, Quote oldQuote);

    @Mappings({
            @Mapping(source = "username",target ="user.username"),
            @Mapping(target = "votes",expression = "java(new ArrayList())")
    })
    Quote convert(QuoteDtoRequest quote);

    @Mappings({
            @Mapping(source = "quote.quoteId",target = "quoteId"),
            @Mapping(source = "quote.title",target = "title"),
            @Mapping(source = "quote.voteCounter.counter",target = "voteCounter"),
            @Mapping(source = "quote.user.username",target = "username"),
            @Mapping(source = "quote.content",target = "content"),
            @Mapping(source = "votes",target = "votes"),

    })
    QuoteDtoResponse convert(Quote quote,
                             List<VoteDtoResponse> votes,
                             Long votesValue);


    QuoteDto convert(Quote quote);

    default LocalDateTime map(Timestamp timestamp){
        return timestamp == null ? null :
                timestamp.toLocalDateTime();
    }

    default Timestamp map(LocalDateTime localDateTime){
        return localDateTime == null ? null :
                Timestamp.valueOf(localDateTime);
    }
}
