@users
Feature: make calls for user controller

  Scenario: the client makes GET request for all users
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When the client calls GET for users
    Then a response returned with status code of 200
    And the client receives page of 2 users


  Scenario: the client makes successful GET request for user with correct username
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When the client calls GET for user with username "adminka"
    Then a response returned with status code of 200
    And the client receives an user with username "adminka"

  Scenario: the client makes failed GET request for user with incorrect id
    When the client calls GET for user with username "someAnotherName"
    Then a response returned with status code of 404
    And the client receives a message
      | message                                       |
      | User wasn't found by username=someAnotherName |

  Scenario: the client makes successful POST request to save a new user with correct data
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 2008-01-18  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When the client calls POST for user with new username "arteminio" with new User and password "password"
      | email             | dateOfBirth |
      | someemail@mail.ru | 1997-06-25  |
    Then a response returned with status code of 201
    And the client receives the same saved user with username "arteminio"


  Scenario: the client makes failed POST request to save a new user with incorrect data
    When the client calls POST for user with new username "basdadad" with new User and password "dsadadasd"
      | email | dateOfBirth |
      | sd    | 2020-06-25  |
    Then a response returned with status code of 400
    And the client receives a list of validation messages
      | field       | message                                   |
      | dateOfBirth | User must be older than 12 years          |
      | email       | incorrect email                           |

  Scenario: the client makes successful PUT request to update a username of user with correct data
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When the client calls PUT for user with username "adminka" and an updated user
      | email             | dateOfBirth |
      | someemail@mail.ru | 1997-06-25  |
    Then a response returned with status code of 200
    And the client receives an user with new email "someemail@mail.ru" and the same username "adminka"


  Scenario: the client makes failed PUT request to update a username of user with incorrect data
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When the client calls PUT for user with username "adminka" and an updated user
      | email   | dateOfBirth |
      | someema | 1997-06-25  |
    Then a response returned with status code of 400
    And the client receives a list of validation messages
      | field | message         |
      | email | incorrect email |

  Scenario: the client makes successful DELETE request  to delete an employee with correct id
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When  the client calls DELETE for user with username "adminka"
    Then a response returned with status code of 200
    And the database has 1 users

  Scenario: the client makes failed DELETE request to delete an employee with incorrect id
    Given the database has users
      | username | password | email                 | role       | dateOfBirth |
      | adminka  | password | myachinenergo@mail.ru | ROLE_ADMIN | 1997-06-25  |
      | another  | secondd  | bekone97@mail.ru      | ROLE_ADMIN | 1997-06-25  |
    When  the client calls DELETE for user with username "asdmkasdmaskdm"
    Then a response returned with status code of 404
    And the client receives a message
      | message                                      |
      | User wasn't found by username=asdmkasdmaskdm |
