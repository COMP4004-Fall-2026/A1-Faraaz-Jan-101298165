package library;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LibrarySystem {
    private final Map<String, BookTitle> titles = new LinkedHashMap<>();
    private final Map<String, User> users = new LinkedHashMap<>();
    private User currentUser;

    public LibrarySystem() {
        initializeCollection();
        initializeUsers();
    }

    public void initializeCollection() {
        titles.clear();
        String[][] books = {
                {"The Great Gatsby", "F. Scott Fitzgerald"}, {"The Hobbit", "J. R. R. Tolkien"},
                {"Pride and Prejudice", "Jane Austen"}, {"1984", "George Orwell"},
                {"To Kill a Mockingbird", "Harper Lee"}, {"The Catcher in the Rye", "J. D. Salinger"},
                {"The Lord of the Rings", "J. R. R. Tolkien"}, {"The Alchemist", "Paulo Coelho"},
                {"The Book Thief", "Markus Zusak"}, {"Dune", "Frank Herbert"},
                {"Jane Eyre", "Charlotte Bronte"}, {"The Kite Runner", "Khaled Hosseini"},
                {"Little Women", "Louisa May Alcott"}, {"The Odyssey", "Homer"},
                {"The Road", "Cormac McCarthy"}, {"The Shining", "Stephen King"},
                {"The Stranger", "Albert Camus"}, {"The Sun Also Rises", "Ernest Hemingway"},
                {"Wuthering Heights", "Emily Bronte"}, {"The Yellow Wallpaper", "Charlotte Perkins Gilman"}
        };
        for (int index = 0; index < books.length; index++) {
            addBookInternal(books[index][0], books[index][1], String.format("C%03d", index + 1));
        }
    }

    public void initializeUsers() {
        users.clear();
        for (int index = 1; index <= 5; index++) {
            registerUser(new Borrower("borrower" + index, "password" + index));
        }
        registerUser(new Librarian("librarian1", "admin1"));
        registerUser(new Librarian("librarian2", "admin2"));
    }

    public Collection<BookTitle> getBookTitles() {
        return titles.values().stream()
                .sorted(Comparator.comparing(BookTitle::getTitle))
                .toList();
    }

    public Optional<BookTitle> findBookTitle(String title) {
        return Optional.ofNullable(titles.get(title));
    }

    public Optional<User> findUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && user.passwordMatches(password) ? Optional.of(user) : Optional.empty();
    }

    public User login(String username, String password) {
        if (currentUser != null) {
            throw new LibraryException("A user is already logged in.");
        }
        currentUser = authenticate(username, password)
                .orElseThrow(() -> new LibraryException("Invalid username or password."));
        return currentUser;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void logout() {
        requireLoggedIn();
        currentUser = null;
    }

    public BookCopy borrow(String title) {
        Borrower borrower = requireBorrower();
        BookTitle bookTitle = requireTitle(title);
        if (borrower.hasBorrowed(title)) {
            throw new LibraryException("This borrower already has that title checked out.");
        }
        if (borrower.getBorrowedBookCount() >= Borrower.MAX_BORROWED_BOOKS) {
            throw new LibraryException("The maximum borrowing limit of three books has been reached.");
        }
        if (!bookTitle.isAvailableTo(borrower.getUsername())) {
            throw new LibraryException("This title is unavailable to this borrower.");
        }
        BookCopy copy = bookTitle.takeAvailableCopy();
        copy.checkOut();
        borrower.borrow(copy);
        bookTitle.removeHoldFor(borrower.getUsername());
        return copy;
    }

    public void placeHold(String title) {
        Borrower borrower = requireBorrower();
        BookTitle bookTitle = requireTitle(title);
        if (borrower.hasBorrowed(title)) {
            throw new LibraryException("A borrower cannot place a hold on a title already checked out.");
        }
        bookTitle.placeHold(borrower.getUsername());
    }

    public BookCopy returnBook(String title) {
        Borrower borrower = requireBorrower();
        if (borrower.getBorrowedBookCount() == 0) {
            throw new LibraryException("No books are currently borrowed.");
        }
        BookTitle bookTitle = requireTitle(title);
        BookCopy copy = borrower.returnBook(title);
        copy.returnToLibrary();
        bookTitle.refreshNotification();
        return copy;
    }

    public BookCopy addBook(String title, String author, String copyNumber) {
        requireLibrarian();
        if (isCopyNumberInUse(copyNumber)) {
            throw new LibraryException("Copy number already exists.");
        }
        BookTitle existing = titles.get(title);
        if (existing != null && existing.hasHoldQueue()) {
            throw new LibraryException("A copy cannot be added while this title has borrowers waiting.");
        }
        return addBookInternal(title, author, copyNumber);
    }

    public void removeBook(String title, String copyNumber) {
        requireLibrarian();
        BookTitle bookTitle = requireTitle(title);
        bookTitle.removeCopy(copyNumber);
        if (bookTitle.getCopies().isEmpty()) {
            titles.remove(title);
        }
    }

    public List<String> getHoldNotifications() {
        Borrower borrower = requireBorrower();
        return titles.values().stream()
                .filter(title -> title.hasNotificationFor(borrower.getUsername()))
                .map(title -> title.getTitle() + " is available for you to borrow.")
                .toList();
    }

    private BookCopy addBookInternal(String title, String author, String copyNumber) {
        BookTitle bookTitle = titles.computeIfAbsent(title, ignored -> new BookTitle(title, author));
        if (!bookTitle.getAuthor().equals(author)) {
            throw new LibraryException("An existing title must use its existing author.");
        }
        BookCopy copy = new BookCopy(title, copyNumber);
        bookTitle.addCopy(copy);
        return copy;
    }

    private boolean isCopyNumberInUse(String copyNumber) {
        return titles.values().stream()
                .flatMap(title -> title.getCopies().stream())
                .anyMatch(copy -> copy.getCopyNumber().equals(copyNumber));
    }

    private BookTitle requireTitle(String title) {
        return findBookTitle(title).orElseThrow(() -> new LibraryException("Book title not found."));
    }

    private User requireLoggedIn() {
        if (currentUser == null) {
            throw new LibraryException("A user must be logged in.");
        }
        return currentUser;
    }

    private Borrower requireBorrower() {
        User user = requireLoggedIn();
        if (!(user instanceof Borrower borrower)) {
            throw new LibraryException("This operation is available only to borrowers.");
        }
        return borrower;
    }

    private Librarian requireLibrarian() {
        User user = requireLoggedIn();
        if (!(user instanceof Librarian librarian)) {
            throw new LibraryException("This operation is available only to librarians.");
        }
        return librarian;
    }

    private void registerUser(User user) {
        users.put(user.getUsername(), user);
    }
}
