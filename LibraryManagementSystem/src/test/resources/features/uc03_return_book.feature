Feature: UC-03 Return Book
  As a borrower
  I want to return books I have borrowed
  So that I can free my borrowing slots and make copies available to others

  Background:
    Given a fresh library system

  Rule: R3.1 Returning a title with no hold queue makes the copy Available and removes it from the borrower's account.

    Scenario: Returning the only borrowed title makes the copy available and empties the account
      Given "borrower1" is logged in with password "password1"
      And "borrower1" has already borrowed "The Hobbit"
      When "borrower1" returns "The Hobbit"
      Then the operation succeeds
      And "The Hobbit" has 1 available copy
      And "borrower1" has 0 titles borrowed

    Scenario: Returning one of multiple borrowed titles removes only the returned title
      Given "borrower2" is logged in with password "password2"
      And "borrower2" has already borrowed "1984" and "Dune"
      When "borrower2" returns "1984"
      Then the operation succeeds
      And "1984" has 1 available copy
      And "borrower2" has 1 title borrowed
      And "borrower2" has borrowed "Dune"

  Rule: R3.2 A borrower with no titles borrowed cannot return a title, and the system reports that no titles are borrowed.

    Scenario: Returning a catalog title when no titles are borrowed is rejected
      Given "borrower1" is logged in with password "password1"
      When "borrower1" attempts to return "The Hobbit"
      Then the operation fails with message "No books are currently borrowed."
      And "borrower1" has 0 titles borrowed

    Scenario: Returning an unknown title when no titles are borrowed still reports no titles borrowed
      Given "borrower2" is logged in with password "password2"
      When "borrower2" attempts to return "Nonexistent Book"
      Then the operation fails with message "No books are currently borrowed."
      And "borrower2" has 0 titles borrowed

  Rule: R3.3 When a returned title has a hold queue, the returned copy becomes Available only to the first borrower in the queue, who is notified.

    Scenario: Returning a title with one borrower in the hold queue makes it available only to that borrower and notifies them
      Given "borrower1" with password "password1" has borrowed "The Book Thief" and logged out
      And "borrower2" with password "password2" has placed a hold on "The Book Thief" and logged out
      And "borrower1" is logged in with password "password1"
      When "borrower1" returns "The Book Thief"
      Then the operation succeeds
      And "The Book Thief" is available to "borrower2"
      And "The Book Thief" is not available to "borrower3"
      And "borrower2" has a pending hold notification for "The Book Thief"

    Scenario: Returning a title with multiple borrowers in the hold queue makes it available only to the first borrower
      Given "borrower1" with password "password1" has borrowed "The Kite Runner" and logged out
      And "borrower2" with password "password2" has placed a hold on "The Kite Runner" and logged out
      And "borrower3" with password "password3" has placed a hold on "The Kite Runner" and logged out
      And "borrower1" is logged in with password "password1"
      When "borrower1" returns "The Kite Runner"
      Then the operation succeeds
      And "The Kite Runner" is available to "borrower2"
      And "The Kite Runner" is not available to "borrower3"
      And "borrower2" has a pending hold notification for "The Kite Runner"
      And "borrower3" has no pending hold notification for "The Kite Runner"
