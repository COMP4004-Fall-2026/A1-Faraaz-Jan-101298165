package library;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class BookTitle {
    private final String title;
    private final String author;
    private final List<BookCopy> copies = new java.util.ArrayList<>();
    private final Deque<String> holdQueue = new ArrayDeque<>();
    private final Set<String> notifiedBorrowers = new LinkedHashSet<>();

    BookTitle(String title, String author) {
        this.title = User.requireText(title, "Title");
        this.author = User.requireText(author, "Author");
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Collection<BookCopy> getCopies() {
        return List.copyOf(copies);
    }

    public List<String> getHoldQueue() {
        return List.copyOf(holdQueue);
    }

    public boolean hasAvailableCopy() {
        return copies.stream().anyMatch(copy -> copy.getStatus() == CopyStatus.AVAILABLE);
    }

    public boolean isAvailableTo(String username) {
        return hasAvailableCopy() && (holdQueue.isEmpty() || holdQueue.peekFirst().equals(username));
    }

    public boolean hasHoldQueue() {
        return !holdQueue.isEmpty();
    }

    public boolean hasHold(String username) {
        return holdQueue.contains(username);
    }

    void addCopy(BookCopy copy) {
        copies.add(copy);
    }

    void removeCopy(String copyNumber) {
        BookCopy copy = findCopy(copyNumber);
        if (copy.getStatus() != CopyStatus.AVAILABLE || hasHoldQueue()) {
            throw new LibraryException("An unavailable copy or a title with active holds cannot be removed.");
        }
        copies.remove(copy);
    }

    BookCopy takeAvailableCopy() {
        return copies.stream()
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(() -> new LibraryException("No available copy exists for this title."));
    }

    void placeHold(String username) {
        if (hasAvailableCopy()) {
            throw new LibraryException("A hold can only be placed when no copy is available.");
        }
        if (hasHold(username)) {
            throw new LibraryException("This borrower already has a hold on that title.");
        }
        holdQueue.addLast(username);
    }

    void removeHoldFor(String username) {
        if (username.equals(holdQueue.peekFirst())) {
            holdQueue.removeFirst();
        } else {
            holdQueue.remove(username);
        }
        refreshNotification();
    }

    void refreshNotification() {
        notifiedBorrowers.clear();
        if (hasAvailableCopy() && !holdQueue.isEmpty()) {
            notifiedBorrowers.add(holdQueue.peekFirst());
        }
    }

    boolean hasNotificationFor(String username) {
        return notifiedBorrowers.contains(username);
    }

    private BookCopy findCopy(String copyNumber) {
        return copies.stream()
                .filter(copy -> copy.getCopyNumber().equals(copyNumber))
                .findFirst()
                .orElseThrow(() -> new LibraryException("Book copy not found."));
    }
}
