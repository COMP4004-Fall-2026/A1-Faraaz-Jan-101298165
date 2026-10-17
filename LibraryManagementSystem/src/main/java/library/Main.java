package library;

import java.util.Scanner;

public final class Main {
    private final LibrarySystem system = new LibrarySystem();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("Library Management System");
        while (true) {
            login();
            if (system.getCurrentUser().orElseThrow().getRole() == Role.BORROWER) {
                borrowerMenu();
            } else {
                librarianMenu();
            }
        }
    }

    private void login() {
        while (system.getCurrentUser().isEmpty()) {
            try {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                system.login(username, scanner.nextLine());
                if (system.getCurrentUser().orElseThrow().getRole() == Role.BORROWER) {
                    system.getHoldNotifications().forEach(notification -> System.out.println("Notification: " + notification));
                }
            } catch (LibraryException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void borrowerMenu() {
        while (system.getCurrentUser().isPresent()) {
            System.out.println("\n1. Borrow book  2. Return book  3. Log out");
            switch (scanner.nextLine()) {
                case "1" -> borrow();
                case "2" -> returnBook();
                case "3" -> system.logout();
                default -> System.out.println("Choose a listed option.");
            }
        }
    }

    private void librarianMenu() {
        while (system.getCurrentUser().isPresent()) {
            System.out.println("\n1. Add book copy  2. Remove book copy  3. Log out");
            switch (scanner.nextLine()) {
                case "1" -> addBook();
                case "2" -> removeBook();
                case "3" -> system.logout();
                default -> System.out.println("Choose a listed option.");
            }
        }
    }

    private void borrow() {
        Borrower borrower = (Borrower) system.getCurrentUser().orElseThrow();
        System.out.println("Borrowed: " + borrower.getBorrowedBookCount() + "/3");
        system.getBookTitles().forEach(title -> System.out.printf("%s by %s - %s%n", title.getTitle(), title.getAuthor(),
                title.isAvailableTo(borrower.getUsername()) ? "Available" : "Unavailable"));
        System.out.print("Title: ");
        String title = scanner.nextLine();
        try {
            if (!system.findBookTitle(title).orElseThrow(() -> new LibraryException("Book title not found."))
                    .isAvailableTo(borrower.getUsername())) {
                System.out.print("Unavailable. Place a hold? (yes/no): ");
                if ("yes".equalsIgnoreCase(scanner.nextLine())) {
                    system.placeHold(title);
                    System.out.println("Hold placed.");
                }
                return;
            }
            System.out.print("Confirm borrowing? (yes/no): ");
            if ("yes".equalsIgnoreCase(scanner.nextLine())) {
                System.out.println("Borrowed copy " + system.borrow(title).getCopyNumber() + ".");
            }
        } catch (LibraryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void returnBook() {
        Borrower borrower = (Borrower) system.getCurrentUser().orElseThrow();
        if (borrower.getBorrowedBookCount() == 0) {
            System.out.println("No books are currently borrowed.");
            return;
        }
        borrower.getBorrowedCopies().forEach(copy -> System.out.println(copy.getTitle() + " (" + copy.getCopyNumber() + ")"));
        System.out.print("Title to return: ");
        try {
            System.out.println("Returned " + system.returnBook(scanner.nextLine()).getTitle() + ".");
        } catch (LibraryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void addBook() {
        try {
            System.out.print("Title: "); String title = scanner.nextLine();
            System.out.print("Author: "); String author = scanner.nextLine();
            System.out.print("Copy number: "); String copyNumber = scanner.nextLine();
            system.addBook(title, author, copyNumber);
            System.out.println("Book copy added.");
        } catch (LibraryException | IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void removeBook() {
        system.getBookTitles().forEach(title -> title.getCopies().forEach(copy ->
                System.out.printf("%s - %s (%s)%n", title.getTitle(), copy.getCopyNumber(), copy.getStatus())));
        try {
            System.out.print("Title: "); String title = scanner.nextLine();
            System.out.print("Copy number: "); String copyNumber = scanner.nextLine();
            system.removeBook(title, copyNumber);
            System.out.println("Book copy removed.");
        } catch (LibraryException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
