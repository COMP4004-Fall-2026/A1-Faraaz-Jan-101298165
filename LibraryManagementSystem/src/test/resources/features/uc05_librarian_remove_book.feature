Feature: UC-05 Librarian Remove Book
  As a librarian
  I want to remove available copies of books from the catalog
  So that damaged or retired copies are no longer listed

  Background:
    Given a fresh library system

  Rule: R5.1 Removing an Available copy of a title with no hold queue removes that copy from the title.

    Scenario: Removing one available copy of a two-copy title leaves the other copy available
      Given "librarian1" with password "admin1" has added copy "C021" of "The Hobbit" by "J. R. R. Tolkien" and logged out
      And "librarian1" is logged in with password "admin1"
      When "librarian1" removes copy "C002" of "The Hobbit"
      Then the operation succeeds
      And "The Hobbit" exists with 1 copy and 1 available copy

    Scenario: Removing an available copy when the other copy is checked out succeeds
      Given "librarian1" with password "admin1" has added copy "C021" of "Dune" by "Frank Herbert" and logged out
      And "borrower1" with password "password1" has borrowed "Dune" and logged out
      And "librarian2" is logged in with password "admin2"
      When "librarian2" removes copy "C021" of "Dune"
      Then the operation succeeds
      And "Dune" exists with 1 copy and 0 available copies

  Rule: R5.2 A librarian cannot remove a Checked Out copy or any copy of a title that has a hold queue.

    Scenario: Removing a checked-out copy is rejected
      Given "borrower1" with password "password1" has borrowed "1984" and logged out
      And "librarian1" is logged in with password "admin1"
      When "librarian1" attempts to remove copy "C004" of "1984"
      Then the operation fails
      And "1984" exists with 1 copy and 0 available copies

    Scenario: Removing an available copy of a title that has a hold queue is rejected
      Given "librarian1" with password "admin1" has added copy "C021" of "The Catcher in the Rye" by "J. D. Salinger" and logged out
      And "borrower1" with password "password1" has borrowed "The Catcher in the Rye" and logged out
      And "borrower2" with password "password2" has borrowed "The Catcher in the Rye" and logged out
      And "borrower3" with password "password3" has placed a hold on "The Catcher in the Rye" and logged out
      And "borrower1" with password "password1" has returned "The Catcher in the Rye" and logged out
      And "librarian1" is logged in with password "admin1"
      When "librarian1" attempts to remove copy "C006" of "The Catcher in the Rye"
      Then the operation fails
      And "The Catcher in the Rye" exists with 2 copies and 1 available copy

  Rule: R5.3 Removing the last copy means borrowers no longer see the title.

    Scenario: Removing the sole copy of an initial title removes the title from the catalog
      Given "librarian1" is logged in with password "admin1"
      When "librarian1" removes copy "C010" of "Dune"
      Then the operation succeeds
      And "Dune" is not in the catalog
      And the catalog contains 19 titles

    Scenario: Removing the last remaining copy of a newly added title removes it from the catalog
      Given "librarian2" with password "admin2" has added copy "C021" of "Animal Farm" by "George Orwell" and logged out
      And "librarian2" is logged in with password "admin2"
      When "librarian2" removes copy "C021" of "Animal Farm"
      Then the operation succeeds
      And "Animal Farm" is not in the catalog
      And the catalog contains 20 titles
