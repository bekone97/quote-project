package com.example.quoteservice.util;

import com.example.quoteservice.exception.NotValidCredentialsException;
import com.example.quoteservice.security.user.AuthenticatedUser;

public class ConstantUtil {
    public static class Exception {
        public final static String NO_FOUND_PATTERN = "%s wasn't found by %s=%s";
        public final static String NO_FOUNDED_FROM_RESOURCE_PATTERN = "%s wasn't found by %s=%s from %s with %s=%s";
        public static final String NOT_UNIQUE_RESOURCE_PATTERN = "%s already exists with %s=%s";
        public static final String NO_ENOUGH_PERMISSIONS = "User has no enough permissions";
        public static final String NOT_VALID_TOKEN = "Token isn't valid";
    }

    public static class SpringDocResponse {
        public final static String APPLICATION_JSON = "application/json";
        public static final String RESPONSE_CODE_OK = "200";
        public static final String RESPONSE_CODE_CREATED = "201";
        public static final String RESPONSE_CODE_BAD_REQUEST = "400";
        public static final String RESPONSE_CODE_NOT_FOUNDED = "404";
        public static final String RESPONSE_CODE_FORBIDDEN = "403";
        public static final String RESPONSE_CODE_INTERNAL_SERVER_ERROR = "500";
        public static final String RESPONSE_DESCRIPTION_OK = "OK";
        public static final String RESPONSE_DESCRIPTION_CREATED = "Created";
        public static final String RESPONSE_DESCRIPTION_BAD_REQUEST = "Bad Request";
        public static final String RESPONSE_DESCRIPTION_NOT_FOUNDED = "Not Founded";
        public static final String RESPONSE_CODE_DESCRIPTION_FORBIDDEN = "Forbidden";
    }

    public static class ValidOperations {
        public static void checkValidCredentials(AuthenticatedUser authenticatedUser, String username) {
            boolean stat = false;
            if (authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                    || authenticatedUser.getUsername().equals(username)) {
                return;
            }
            throw new NotValidCredentialsException("You have no right to make this operations for this data");
        }
    }
}

