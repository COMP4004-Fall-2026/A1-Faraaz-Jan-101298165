package library;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Borrower extends User {
    public static final int MAX_BORROWED_BOOKS = 3;
    private final Map<String, BookCopy> borrowedCopies = new LinkedHashMap<>();

    public Borrower(String username, String password) {
        super(username, password, Role.BORROWER);
    }

    public int getBorrowedBookCount() {
        return borrowedCopies.size();
    }

    public boolean hasBorrowed(String title) {
        return borrowedCopies.containsKey(title);
    }

    public Collection<BookCopy> getBorrowedCopies() {
        return borrowedCopies.values().stream().toList();
    }

    void borrow(BookCopy copy) {
        if (getBorrowedBookCount() >= MAX_BORROWED_BOOKS) {
            throw new LibraryException("The maximum borrowing limit of three books has been reached.");
        }
        borrowedCopies.put(copy.getTitle(), copy);
    }

    BookCopy returnBook(String title) {
        BookCopy copy = borrowedCopies.remove(title);
        if (copy == null) {
            throw new LibraryException("This borrower does not have that book checked out.");
        }
        return copy;
    }
}
