package com.example.bddservice.cucumber.hooks;


import com.example.quoteservice.model.User;
import com.example.quoteservice.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class DatabaseHook {

    private final UserRepository userRepository;

    @Before("@users")
    @Transactional
    public void clearDatabaseGenerator() {
        userRepository.deleteAll();
    }

    @After("@users")
    @Transactional
    public void tearDownDatabaseGenerator() {
        userRepository.deleteAll();
    }

    @Given("the database has users")
    public void initUserTable(final List<User> users) {
        users.forEach(user -> user.setQuotes(new ArrayList<>()));
        userRepository.saveAll(users);
    }

    @And("the database has {int} users")
    public void checkQuantity(int expectedQuantity) {
        List<User> all = userRepository.findAll();
        assertEquals(expectedQuantity, all.size());
    }

}
