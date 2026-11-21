Feature: UC-01 User Login
  As a library user
  I want to log in with my username and password
  So that I can access the library operations for my role

  Background:
    Given a fresh library system

  Rule: R1.1 Valid credentials log the user in with their account's assigned role.

    Scenario Outline: Valid credentials log in users with their assigned role
      Given no user is logged in
      When "<username>" logs in with password "<password>"
      Then "<username>" is logged in with role "<role>"
      And the operation succeeds

      Examples:
        | username   | password  | role      |
        | borrower1  | password1 | BORROWER  |
        | librarian1 | admin1    | LIBRARIAN |

  Rule: R1.2 Invalid credentials are rejected, and no user is logged in.

    Scenario Outline: Invalid credentials are rejected
      Given no user is logged in
      When "<username>" attempts to log in with password "<password>"
      Then the operation fails
      And no user is logged in

      Examples:
        | username    | password  |
        | borrower1   | wrongpass |
        | unknownUser | password1 |

  Rule: R1.3 A borrower who is first in the hold queue for a title with an Available copy is notified at login.

    Scenario: First borrower in the hold queue is notified at login when a copy is available
      Given "borrower1" with password "password1" has borrowed "The Hobbit" and logged out
      And "borrower2" with password "password2" has placed a hold on "The Hobbit" and logged out
      And "borrower1" with password "password1" has returned "The Hobbit" and logged out
      When "borrower2" logs in with password "password2"
      Then "borrower2" is logged in with role "BORROWER"
      And the logged-in borrower receives a hold notification for "The Hobbit"

    Scenario: Second borrower in the hold queue is not notified at login
      Given "borrower1" with password "password1" has borrowed "1984" and logged out
      And "borrower2" with password "password2" has placed a hold on "1984" and logged out
      And "borrower3" with password "password3" has placed a hold on "1984" and logged out
      And "borrower1" with password "password1" has returned "1984" and logged out
      When "borrower3" logs in with password "password3"
      Then "borrower3" is logged in with role "BORROWER"
      And the logged-in borrower has 0 hold notifications
