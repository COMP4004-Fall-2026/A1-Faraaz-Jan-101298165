Feature: UC-04 Librarian Add Book
  As a librarian
  I want to add new book titles and additional copies to the catalog
  So that borrowers have access to new books and more copies of existing titles

  Background:
    Given a fresh library system

  Rule: R4.1 Adding a title that does not exist creates the title with one Available copy, which borrowers can borrow.

    Scenario: Adding a new title creates one available copy in the catalog
      Given "librarian1" is logged in with password "admin1"
      When "librarian1" adds copy "C021" of "Brave New World" by "Aldous Huxley"
      Then the operation succeeds
      And "Brave New World" exists with 1 copy and 1 available copy
      And the catalog contains 21 titles

    Scenario: A newly added title can be borrowed by a borrower
      Given "librarian2" with password "admin2" has added copy "C022" of "Fahrenheit 451" by "Ray Bradbury" and logged out
      And "borrower1" is logged in with password "password1"
      When "borrower1" borrows "Fahrenheit 451"
      Then the operation succeeds
      And "borrower1" has borrowed "Fahrenheit 451"
      And "Fahrenheit 451" has 0 available copies

  Rule: R4.2 Adding a copy of an existing title with no hold queue adds one more Available copy to that title.

    Scenario: Adding a copy to an existing available title increases both total and available copies
      Given "librarian1" is logged in with password "admin1"
      When "librarian1" adds copy "C021" of "The Hobbit" by "J. R. R. Tolkien"
      Then the operation succeeds
      And "The Hobbit" exists with 2 copies and 2 available copies

    Scenario: Adding a copy to an existing checked-out title with no hold queue adds one available copy
      Given "borrower1" with password "password1" has borrowed "1984" and logged out
      And "librarian2" is logged in with password "admin2"
      When "librarian2" adds copy "C021" of "1984" by "George Orwell"
      Then the operation succeeds
      And "1984" exists with 2 copies and 1 available copy

  Rule: R4.3 A copy cannot be added while the title has a hold queue.

    Scenario: Adding a copy to a title with one borrower in the hold queue is rejected
      Given "borrower1" with password "password1" has borrowed "Dune" and logged out
      And "borrower2" with password "password2" has placed a hold on "Dune" and logged out
      And "librarian1" is logged in with password "admin1"
      When "librarian1" attempts to add copy "C021" of "Dune" by "Frank Herbert"
      Then the operation fails
      And "Dune" exists with 1 copy and 0 available copies

    Scenario: Adding a copy to a title with multiple borrowers in the hold queue is rejected
      Given "borrower1" with password "password1" has borrowed "Jane Eyre" and logged out
      And "borrower2" with password "password2" has placed a hold on "Jane Eyre" and logged out
      And "borrower3" with password "password3" has placed a hold on "Jane Eyre" and logged out
      And "librarian2" is logged in with password "admin2"
      When "librarian2" attempts to add copy "C021" of "Jane Eyre" by "Charlotte Bronte"
      Then the operation fails
      And "Jane Eyre" exists with 1 copy and 0 available copies
