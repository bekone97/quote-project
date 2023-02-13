package com.example.quoteservice.mapper;

import com.example.quoteservice.dto.VoteDtoRequest;
import com.example.quoteservice.dto.VoteDtoResponse;
import com.example.quoteservice.model.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper
public interface VoteMapper {

    VoteMapper INSTANCE = Mappers.getMapper(VoteMapper.class);

    @Mappings({
            @Mapping(source = "quoteId",target = "quote.quoteId"),
            @Mapping(source = "vote.username",target = "user.username"),
            @Mapping(source = "vote.voteType",target = "voteType")
    })
    Vote convert(VoteDtoRequest vote,Long quoteId);

    @Mappings({
            @Mapping(source = "vote.user.username",target = "username"),
            @Mapping(source = "vote.date",target = "date")
    })
    VoteDtoResponse convert(Vote vote);

    default LocalDateTime map(Timestamp timestamp){
        return timestamp == null ? null :
                timestamp.toLocalDateTime();
    }

    default Timestamp map(LocalDateTime localDateTime){
        return localDateTime == null ? null :
                Timestamp.valueOf(localDateTime);
    }
}
