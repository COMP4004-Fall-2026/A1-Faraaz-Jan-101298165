Feature: UC-02 Borrow Book
  As a borrower
  I want to borrow available books or place holds on unavailable books
  So that I can read library titles when they are available to me

  Background:
    Given a fresh library system

  Rule: R2.1 Borrowing a title that is Available to the borrower checks out a copy and adds it to the borrower's account.

    Scenario: Borrowing an available single-copy title checks out the copy
      Given "borrower1" is logged in with password "password1"
      When "borrower1" borrows "Dune"
      Then the operation succeeds
      And "Dune" has 0 available copies
      And "borrower1" has 1 title borrowed
      And "borrower1" has borrowed "Dune"

    Scenario: Borrowing an available multi-copy title leaves the remaining copy available
      Given "librarian1" with password "admin1" has added copy "C021" of "The Great Gatsby" by "F. Scott Fitzgerald" and logged out
      And "borrower2" is logged in with password "password2"
      When "borrower2" borrows "The Great Gatsby"
      Then the operation succeeds
      And "The Great Gatsby" has 1 available copy
      And "borrower2" has 1 title borrowed
      And "borrower2" has borrowed "The Great Gatsby"

  Rule: R2.2 A borrower can have at most 3 titles borrowed at a time.

    Scenario: Borrowing a third title succeeds at the borrowing limit
      Given "borrower1" is logged in with password "password1"
      And "borrower1" has already borrowed "1984" and "Dune"
      When "borrower1" borrows "The Odyssey"
      Then the operation succeeds
      And "borrower1" has 3 titles borrowed

    Scenario: Borrowing a fourth title is rejected above the borrowing limit
      Given "borrower1" is logged in with password "password1"
      And "borrower1" has already borrowed "1984", "Dune", and "The Odyssey"
      When "borrower1" attempts to borrow "Jane Eyre"
      Then the operation fails
      And "borrower1" has 3 titles borrowed
      And "Jane Eyre" has 1 available copy

  Rule: R2.3 A title that is not Available to the borrower cannot be borrowed.

    Scenario: Borrowing a title whose only copy is checked out is rejected
      Given "borrower1" with password "password1" has borrowed "The Shining" and logged out
      And "borrower2" is logged in with password "password2"
      When "borrower2" attempts to borrow "The Shining"
      Then the operation fails
      And "borrower2" has 0 titles borrowed

    Scenario: Borrowing a title already checked out by the same borrower is rejected even with another copy available
      Given "librarian1" with password "admin1" has added copy "C022" of "The Road" by "Cormac McCarthy" and logged out
      And "borrower3" is logged in with password "password3"
      And "borrower3" has already borrowed "The Road"
      When "borrower3" attempts to borrow "The Road"
      Then the operation fails
      And "borrower3" has 1 title borrowed
      And "The Road" has 1 available copy

  Rule: R2.4 A borrower can place a hold on a title only when no copy of that title is Available.

    Scenario: Placing a hold succeeds when all copies are checked out
      Given "borrower1" with password "password1" has borrowed "Little Women" and logged out
      And "borrower2" is logged in with password "password2"
      When "borrower2" places a hold on "Little Women"
      Then the operation succeeds
      And "borrower2" is in the hold queue for "Little Women"

    Scenario: Placing a hold is rejected when a copy is still available
      Given "librarian1" with password "admin1" has added copy "C023" of "The Alchemist" by "Paulo Coelho" and logged out
      And "borrower1" with password "password1" has borrowed "The Alchemist" and logged out
      And "borrower2" is logged in with password "password2"
      When "borrower2" attempts to place a hold on "The Alchemist"
      Then the operation fails
      And "The Alchemist" has no hold queue

  Rule: R2.5 A borrower cannot hold a title they already have on hold or checked out.

    Scenario: Placing a hold on a title already checked out by the borrower is rejected
      Given "borrower1" is logged in with password "password1"
      And "borrower1" has already borrowed "The Stranger"
      When "borrower1" attempts to place a hold on "The Stranger"
      Then the operation fails
      And "The Stranger" has no hold queue

    Scenario: Placing a duplicate hold on a title already on hold by the borrower is rejected
      Given "borrower1" with password "password1" has borrowed "Wuthering Heights" and logged out
      And "borrower2" is logged in with password "password2"
      And "borrower2" has placed a hold on "Wuthering Heights"
      When "borrower2" attempts to place a hold on "Wuthering Heights"
      Then the operation fails
      And the hold queue for "Wuthering Heights" has size 1

  Rule: R2.6 Holds are first-come, first-served: when a copy becomes Available, only the first borrower in the title's hold queue can borrow it, and that borrower is removed from the queue after borrowing the title.

    Scenario: Second borrower in the hold queue cannot borrow a returned copy reserved for the first borrower
      Given "borrower1" with password "password1" has borrowed "Pride and Prejudice" and logged out
      And "borrower2" with password "password2" has placed a hold on "Pride and Prejudice" and logged out
      And "borrower3" with password "password3" has placed a hold on "Pride and Prejudice" and logged out
      And "borrower1" with password "password1" has returned "Pride and Prejudice" and logged out
      And "borrower3" is logged in with password "password3"
      When "borrower3" attempts to borrow "Pride and Prejudice"
      Then the operation fails
      And "borrower3" has 0 titles borrowed
      And the hold queue for "Pride and Prejudice" has size 2

    Scenario: First borrower in the hold queue can borrow the returned copy and is removed from the queue
      Given "borrower1" with password "password1" has borrowed "Pride and Prejudice" and logged out
      And "borrower2" with password "password2" has placed a hold on "Pride and Prejudice" and logged out
      And "borrower3" with password "password3" has placed a hold on "Pride and Prejudice" and logged out
      And "borrower1" with password "password1" has returned "Pride and Prejudice" and logged out
      And "borrower2" is logged in with password "password2"
      When "borrower2" borrows "Pride and Prejudice"
      Then the operation succeeds
      And "borrower2" has borrowed "Pride and Prejudice"
      And "borrower2" is not in the hold queue for "Pride and Prejudice"
      And "borrower3" is first in the hold queue for "Pride and Prejudice"
