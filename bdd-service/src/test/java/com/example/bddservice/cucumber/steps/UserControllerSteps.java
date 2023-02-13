package com.example.bddservice.cucumber.steps;

import com.example.bddservice.cucumber.CucumberConfiguration;
import com.example.bddservice.cucumber.client.HttpClient;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.handling.ApiErrorResponse;
import com.example.quoteservice.handling.ValidationErrorResponse;
import com.example.quoteservice.handling.ValidationMessage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.bddservice.cucumber.utils.UrlUtil.USERS;
import static com.example.bddservice.cucumber.utils.UrlUtil.USERS_WITH_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerSteps {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private CucumberConfiguration cucumberConfiguration;

    @When("the client calls GET for users")
    public void the_client_calls() {
        httpClient.get(USERS);
    }


    @When("the client calls POST for user with new username {string} with new User and password {string}")
    public void the_client_calls_post_method(String username, String password, final UserDtoRequest userDtoRequest) throws Exception {
        httpClient.post(USERS_WITH_USERNAME+username, cucumberConfiguration.getStringValue(userDtoRequest), password, UserDtoRequest.class);
    }

    @When("the client calls PUT for user with username {string} and an updated user")
    public void the_client_calls_put_with_user(String username, final UserDtoRequest userDtoRequest) {
        httpClient.update(USERS_WITH_USERNAME + username, cucumberConfiguration.getStringValue(userDtoRequest), UserDtoRequest.class);
    }


    @When("the client calls DELETE for user with username {string}")
    public void the_client_calls_delete(String username) {
        httpClient.delete(USERS_WITH_USERNAME + username);
    }

    @When("the client calls GET for user with username {string}")
    public void the_client_calls_user_by_username(String username) {
        httpClient.getById(USERS_WITH_USERNAME + username);
    }


    @Then("a response returned with status code of {int}")
    public void the_client_receives_status_code_of(Integer responseStatus) {
        var response = httpClient.getResponse().getStatusCode().value();
        assertTrue(responseStatus == httpClient.getResponse().getStatusCode().value());
    }

    @And("the client receives page of {int} users")
    public void the_client_receives_users(int expectedNumber) {
        var actualPage = cucumberConfiguration.readValueForPage(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedNumber, actualPage.getContent().size());
    }


    @And("the client receives the same saved user with username {string}")
    public void the_client_receive_user_with_id(String expectedUsername) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedUsername, actualUser.getUsername());
    }


    @And("the client receives an user with new email {string} and the same username {string}")
    public void the_client_receives_updated_user(String expectedEmail, String expectedUsername) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedUsername, actualUser.getUsername());
        assertEquals(expectedEmail, actualUser.getEmail());
    }

    @And("the client receives an user with username {string}")
    public void the_client_receives_user_by_id(String expectedUsername) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedUsername, actualUser.getUsername());
    }

    @And("the client receives a message")
    public void the_client_receives_error_message(ApiErrorResponse expected) {
        var actual = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), ApiErrorResponse.class);
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @And("the client receives a list of validation messages")
    public void the_client_receives_list_of_errors(List<ValidationMessage> expectedMessages) {
        var actual = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), ValidationErrorResponse.class);
        assertThat(actual.getValidationMessages())
                .hasSize(expectedMessages.size())
                .hasSameElementsAs(expectedMessages);
    }
}
