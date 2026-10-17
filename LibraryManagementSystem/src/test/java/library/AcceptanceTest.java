package library;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AcceptanceTest {
    @Test
    void A_TEST_01_multiUserBorrowAndReturnWithAvailabilityValidated() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Great Gatsby");
        system.logout();

        system.login("borrower2", "password2");
        assertFalse(system.findBookTitle("The Great Gatsby").orElseThrow().isAvailableTo("borrower2"));
        system.logout();

        system.login("borrower1", "password1");
        system.returnBook("The Great Gatsby");
        system.logout();

        system.login("borrower2", "password2");
        system.borrow("The Great Gatsby");
        system.logout();
    }

    @Test
    void A_TEST_02_borrowerBorrowsBookLibrarianAddsBookThenBorrowerBorrowsIt() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();

        system.login("librarian1", "admin1");
        system.addBook("The Great Escape", "Paul Brickhill", "C021");
        system.logout();

        system.login("borrower1", "password1");
        assertTrue(system.findBookTitle("The Great Escape").isPresent());
        system.borrow("The Great Escape");
        system.logout();
    }
}
