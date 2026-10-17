package library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LibrarySystemTest {
    @Test
    void RESP_01_test_1() {
        LibrarySystem system = new LibrarySystem();
        assertEquals(20, system.getBookTitles().size());
        assertTrue(system.getBookTitles().stream().allMatch(title -> title.getCopies().size() == 1));
    }

    @Test
    void RESP_02_test_1() {
        LibrarySystem system = new LibrarySystem();
        assertTrue(system.authenticate("borrower5", "password5").isPresent());
        assertTrue(system.authenticate("librarian2", "admin2").isPresent());
    }

    @Test
    void RESP_03_test_1() {
        LibrarySystem system = new LibrarySystem();
        assertTrue(system.authenticate("borrower1", "password1").isPresent());
        assertTrue(system.authenticate("borrower1", "incorrect").isEmpty());
    }

    @Test
    void RESP_04_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        assertEquals(Role.BORROWER, system.getCurrentUser().orElseThrow().getRole());
        system.logout();
        assertTrue(system.getCurrentUser().isEmpty());
    }

    @Test
    void RESP_05_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        BookCopy copy = system.borrow("The Hobbit");
        assertEquals(CopyStatus.CHECKED_OUT, copy.getStatus());
        assertEquals(1, ((Borrower) system.getCurrentUser().orElseThrow()).getBorrowedBookCount());
    }

    @Test
    void RESP_06_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        BookCopy copy = system.returnBook("The Hobbit");
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertEquals(0, ((Borrower) system.getCurrentUser().orElseThrow()).getBorrowedBookCount());
    }

    @Test
    void RESP_07_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.borrow("1984");
        system.borrow("Dune");
        assertThrows(LibraryException.class, () -> system.borrow("Jane Eyre"));
    }

    @Test
    void RESP_08_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        assertFalse(system.findBookTitle("The Hobbit").orElseThrow().isAvailableTo("borrower2"));
    }

    @Test
    void RESP_09_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        system.placeHold("The Hobbit");
        assertEquals(java.util.List.of("borrower2"), system.findBookTitle("The Hobbit").orElseThrow().getHoldQueue());
    }

    @Test
    void RESP_10_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        system.placeHold("The Hobbit");
        assertThrows(LibraryException.class, () -> system.placeHold("The Hobbit"));
    }

    @Test
    void RESP_11_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        system.placeHold("The Hobbit");
        system.logout();
        system.login("borrower1", "password1");
        system.returnBook("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        assertEquals(java.util.List.of("The Hobbit is available for you to borrow."), system.getHoldNotifications());
    }

    @Test
    void RESP_12_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("librarian1", "admin1");
        BookCopy copy = system.addBook("The Great Escape", "Paul Brickhill", "C021");
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertTrue(system.findBookTitle("The Great Escape").isPresent());
    }

    @Test
    void RESP_13_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("librarian1", "admin1");
        system.removeBook("The Hobbit", "C002");
        assertTrue(system.findBookTitle("The Hobbit").isEmpty());
    }

    @Test
    void RESP_14_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        assertThrows(LibraryException.class, () -> system.addBook("X", "Y", "C999"));
    }

    @Test
    void RESP_15_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("librarian1", "admin1");
        assertThrows(LibraryException.class, () -> system.removeBook("The Hobbit", "C002"));
    }

    @Test
    void RESP_16_test_1() {
        LibrarySystem system = new LibrarySystem();
        system.login("borrower1", "password1");
        system.borrow("The Hobbit");
        system.logout();
        system.login("borrower2", "password2");
        system.placeHold("The Hobbit");
        system.logout();
        system.login("librarian1", "admin1");
        assertThrows(LibraryException.class, () -> system.addBook("The Hobbit", "J. R. R. Tolkien", "C021"));
    }
}
