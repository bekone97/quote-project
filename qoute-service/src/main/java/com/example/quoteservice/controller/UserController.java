package com.example.quoteservice.controller;

import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.handling.ApiErrorResponse;
import com.example.quoteservice.handling.ValidationErrorResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.UserService;
import com.example.quoteservice.validator.ValidPassword;
import com.example.quoteservice.validator.ValidUsername;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.quoteservice.util.ConstantUtil.SpringDocResponse.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Returns all users")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @GetMapping
    @ResponseStatus(OK)
    public Page<UserDtoResponse> getAllUsers(Pageable pageable) {
        var users= userService.findAll(pageable);
        return users;
    }


    @Operation(summary = "Returns a user by username")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))})
    })

    @GetMapping("/{username}")
    @ResponseStatus(OK)
    public UserDtoResponse getUserById(@Parameter(description = "Username of user to be searched", required = true, example = "miachyn")
            @PathVariable @ValidUsername String username) {
        return userService.getById(username);
    }

    @Operation(summary = "Save a new user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_CREATED, description = RESPONSE_DESCRIPTION_CREATED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))})
    })
    @PostMapping("/{username}")
    @ResponseStatus(CREATED)
    public UserDtoResponse save(@Parameter(description = "User information for a new user to be created", required = true,
            schema = @Schema(implementation = UserDtoRequest.class))
                                @Valid @RequestBody UserDtoRequest userDtoRequest,
                                @Parameter(description = "User password to be saved", required = true)
                                @RequestParam @ValidPassword String password,
                                @PathVariable @ValidUsername String username) {
        return userService.save(username,userDtoRequest, password);
    }


    @Operation(summary = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,description = RESPONSE_CODE_DESCRIPTION_FORBIDDEN,
            content = {@Content(mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @PutMapping("/{username}")
    @ResponseStatus(OK)
    public UserDtoResponse update(@Parameter(description = "Username of user to be updated", required = true, example = "miachyn")
                                    @PathVariable @ValidUsername String username,
                                  @Parameter(description = "User information for a user to be updated", required = true,
                                          schema = @Schema(implementation = UserDtoRequest.class))
                                  @Valid @RequestBody UserDtoRequest userDtoRequest,
                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return userService.update(username, userDtoRequest,authenticatedUser);
    }

    @Operation(summary = "Update a password of user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = UserDtoResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,description = RESPONSE_CODE_DESCRIPTION_FORBIDDEN,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @PutMapping("/{username}/password")
    @ResponseStatus(OK)
    public void updatePassword(@Parameter(description = "Username of user to be updated", required = true, example = "miachyn")
                                @PathVariable @ValidUsername String username,
                               @Parameter(description = "Password of user to be updated", required = true, example = "somePassword")
                               @RequestParam @ValidPassword String password,
                               @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        userService.updatePassword(username, password,authenticatedUser);
    }



    @Operation(summary = "Delete an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = RESPONSE_CODE_OK, description = RESPONSE_DESCRIPTION_OK),
            @ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_NOT_FOUNDED, description = RESPONSE_DESCRIPTION_NOT_FOUNDED,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = RESPONSE_CODE_FORBIDDEN,description = RESPONSE_CODE_DESCRIPTION_FORBIDDEN,
                    content = {@Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @DeleteMapping("/{username}")
    @ResponseStatus(OK)
    public void deleteById(@Parameter(description = "Username of user to be deleted", required = true, example = "miachyn")
                            @PathVariable @ValidUsername String username,
                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        userService.deleteById(username,authenticatedUser);
    }

}
