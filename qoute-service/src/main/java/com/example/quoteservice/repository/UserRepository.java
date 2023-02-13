package com.example.quoteservice.repository;

import com.example.quoteservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @EntityGraph(attributePaths = {"quotes","quotes.voteCounter"})
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"quotes","quotes.voteCounter"})
    Optional<User> findById(String userId);

    boolean existsByEmail(String email);
    boolean existsUserByUsername(String username);
    Optional<User> findUserByUsername(String username);
}
