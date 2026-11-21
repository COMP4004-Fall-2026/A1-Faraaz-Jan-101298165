package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import library.BookCopy;
import library.BookTitle;
import library.Borrower;
import library.CopyStatus;
import library.LibraryException;
import library.LibrarySystem;
import library.Role;
import library.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibrarySteps {
    private LibrarySystem system;
    private Exception lastError;

    // ---------- Setup (Given) ----------

    @Given("a fresh library system")
    public void a_fresh_library_system() {
        system = new LibrarySystem();
        lastError = null;
        assertNotNull(system);
    }

    @Then("no user is logged in")
    public void no_user_is_logged_in() {
        assertTrue(system.getCurrentUser().isEmpty(), "Expected no user to be logged in");
    }

    @Given("{string} is logged in with password {string}")
    public void user_is_logged_in_with_password(String username, String password) {
        system.login(username, password);
    }

    @Given("{string} with password {string} has borrowed {string} and logged out")
    public void user_has_borrowed_and_logged_out(String username, String password, String title) {
        system.login(username, password);
        system.borrow(title);
        system.logout();
    }

    @Given("{string} with password {string} has placed a hold on {string} and logged out")
    public void user_has_placed_hold_and_logged_out(String username, String password, String title) {
        system.login(username, password);
        system.placeHold(title);
        system.logout();
    }

    @Given("{string} with password {string} has returned {string} and logged out")
    public void user_has_returned_and_logged_out(String username, String password, String title) {
        system.login(username, password);
        system.returnBook(title);
        system.logout();
    }

    @Given("{string} with password {string} has added copy {string} of {string} by {string} and logged out")
    public void librarian_has_added_copy_and_logged_out(
            String username, String password, String copyNumber, String title, String author) {
        system.login(username, password);
        system.addBook(title, author, copyNumber);
        system.logout();
    }

    @Given("{string} has already borrowed {string}")
    public void logged_in_borrower_has_borrowed_one(String username, String title) {
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        system.borrow(title);
    }

    @Given("{string} has already borrowed {string} and {string}")
    public void logged_in_borrower_has_borrowed_two(String username, String title1, String title2) {
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        system.borrow(title1);
        system.borrow(title2);
    }

    @Given("{string} has already borrowed {string}, {string}, and {string}")
    public void logged_in_borrower_has_borrowed_three(
            String username, String title1, String title2, String title3) {
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        system.borrow(title1);
        system.borrow(title2);
        system.borrow(title3);
    }

    @Given("{string} has placed a hold on {string}")
    public void logged_in_borrower_has_placed_hold(String username, String title) {
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        system.placeHold(title);
    }

    // ---------- Actions (When) ----------

    @When("{string} logs in with password {string}")
    @When("{string} attempts to log in with password {string}")
    public void user_logs_in_with_password(String username, String password) {
        lastError = null;
        try {
            system.login(username, password);
        } catch (LibraryException e) {
            lastError = e;
        }
    }

    @When("{string} borrows {string}")
    @When("{string} attempts to borrow {string}")
    public void borrower_borrows_title(String username, String title) {
        lastError = null;
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        try {
            system.borrow(title);
        } catch (LibraryException e) {
            lastError = e;
        }
    }

    @When("{string} places a hold on {string}")
    @When("{string} attempts to place a hold on {string}")
    public void borrower_places_hold_on_title(String username, String title) {
        lastError = null;
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        try {
            system.placeHold(title);
        } catch (LibraryException e) {
            lastError = e;
        }
    }

    @When("{string} returns {string}")
    @When("{string} attempts to return {string}")
    public void borrower_returns_title(String username, String title) {
        lastError = null;
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        try {
            system.returnBook(title);
        } catch (LibraryException e) {
            lastError = e;
        }
    }

    @When("{string} adds copy {string} of {string} by {string}")
    @When("{string} attempts to add copy {string} of {string} by {string}")
    public void librarian_adds_copy(String username, String copyNumber, String title, String author) {
        lastError = null;
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        try {
            system.addBook(title, author, copyNumber);
        } catch (LibraryException | IllegalArgumentException e) {
            lastError = e;
        }
    }

    @When("{string} removes copy {string} of {string}")
    @When("{string} attempts to remove copy {string} of {string}")
    public void librarian_removes_copy(String username, String copyNumber, String title) {
        lastError = null;
        assertEquals(username, system.getCurrentUser().orElseThrow().getUsername());
        try {
            system.removeBook(title, copyNumber);
        } catch (LibraryException e) {
            lastError = e;
        }
    }

    // ---------- Verification (Then) ----------

    @Then("the operation succeeds")
    public void the_operation_succeeds() {
        assertNull(lastError, () -> "Expected operation to succeed, but failed with: " + lastError.getMessage());
    }

    @Then("the operation fails")
    public void the_operation_fails() {
        assertNotNull(lastError, "Expected the operation to fail");
    }

    @Then("the operation fails with message {string}")
    public void the_operation_fails_with_message(String expectedMessage) {
        assertNotNull(lastError, "Expected the operation to fail");
        assertEquals(expectedMessage, lastError.getMessage());
    }

    @Then("{string} is logged in with role {string}")
    public void user_is_logged_in_with_role(String username, String expectedRole) {
        User currentUser = system.getCurrentUser().orElse(null);
        assertNotNull(currentUser, "Expected a user to be logged in");
        assertEquals(username, currentUser.getUsername());
        assertEquals(Role.valueOf(expectedRole), currentUser.getRole());
    }

    @Then("the logged-in borrower receives a hold notification for {string}")
    public void logged_in_borrower_receives_hold_notification(String title) {
        List<String> notifications = system.getHoldNotifications();
        assertTrue(
                notifications.stream().anyMatch(msg -> msg.contains(title)),
                "Expected hold notification for " + title + ", got: " + notifications
        );
    }

    @Then("the logged-in borrower has {int} hold notifications")
    public void logged_in_borrower_has_hold_notifications(int expectedCount) {
        assertEquals(expectedCount, system.getHoldNotifications().size());
    }

    @Then("{string} has {int} available copy")
    @Then("{string} has {int} available copies")
    public void title_has_available_copies(String title, int expectedAvailable) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        long actualAvailable = bookTitle.getCopies().stream()
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .count();
        assertEquals(expectedAvailable, actualAvailable);
    }

    @Then("{string} has {int} title borrowed")
    @Then("{string} has {int} titles borrowed")
    public void borrower_has_titles_borrowed(String username, int expectedCount) {
        Borrower borrower = findBorrower(username);
        assertEquals(expectedCount, borrower.getBorrowedBookCount());
    }

    @Then("{string} has borrowed {string}")
    public void borrower_has_borrowed_title(String username, String title) {
        Borrower borrower = findBorrower(username);
        assertTrue(borrower.hasBorrowed(title));
    }

    @Then("{string} is in the hold queue for {string}")
    public void borrower_is_in_hold_queue(String username, String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertTrue(bookTitle.hasHold(username));
    }

    @Then("{string} is not in the hold queue for {string}")
    public void borrower_is_not_in_hold_queue(String username, String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertFalse(bookTitle.hasHold(username));
    }

    @Then("{string} has no hold queue")
    public void title_has_no_hold_queue(String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertFalse(bookTitle.hasHoldQueue());
    }

    @Then("the hold queue for {string} has size {int}")
    public void hold_queue_has_size(String title, int expectedSize) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertEquals(expectedSize, bookTitle.getHoldQueue().size());
    }

    @Then("{string} is first in the hold queue for {string}")
    public void borrower_is_first_in_hold_queue(String username, String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertFalse(bookTitle.getHoldQueue().isEmpty());
        assertEquals(username, bookTitle.getHoldQueue().getFirst());
    }

    @Then("{string} is available to {string}")
    public void title_is_available_to_borrower(String title, String username) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertTrue(bookTitle.isAvailableTo(username));
    }

    @Then("{string} is not available to {string}")
    public void title_is_not_available_to_borrower(String title, String username) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertFalse(bookTitle.isAvailableTo(username));
    }

    @Then("{string} has a pending hold notification for {string}")
    public void borrower_has_pending_notification(String username, String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertTrue(bookTitle.hasNotificationFor(username));
    }

    @Then("{string} has no pending hold notification for {string}")
    public void borrower_has_no_pending_notification(String username, String title) {
        BookTitle bookTitle = system.findBookTitle(title).orElseThrow();
        assertFalse(bookTitle.hasNotificationFor(username));
    }

    @Then("{string} exists with {int} copy and {int} available copy")
    @Then("{string} exists with {int} copy and {int} available copies")
    @Then("{string} exists with {int} copies and {int} available copy")
    @Then("{string} exists with {int} copies and {int} available copies")
    public void title_exists_with_copies(String title, int expectedTotal, int expectedAvailable) {
        BookTitle bookTitle = system.findBookTitle(title).orElse(null);
        assertNotNull(bookTitle, "Expected title to exist in catalog: " + title);
        assertEquals(expectedTotal, bookTitle.getCopies().size());
        long actualAvailable = bookTitle.getCopies().stream()
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .count();
        assertEquals(expectedAvailable, actualAvailable);
    }

    @Then("the catalog contains {int} titles")
    public void the_catalog_contains_titles(int expectedTitles) {
        assertEquals(expectedTitles, system.getBookTitles().size());
    }

    @Then("{string} is not in the catalog")
    public void title_is_not_in_catalog(String title) {
        assertTrue(system.findBookTitle(title).isEmpty());
        assertFalse(system.getBookTitles().stream().anyMatch(bt -> bt.getTitle().equals(title)));
    }

    private Borrower findBorrower(String username) {
        User user = system.findUser(username).orElseThrow();
        assertTrue(user instanceof Borrower);
        return (Borrower) user;
    }
}
