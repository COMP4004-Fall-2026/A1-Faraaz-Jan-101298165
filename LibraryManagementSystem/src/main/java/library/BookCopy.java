package library;

public final class BookCopy {
    private final String title;
    private final String copyNumber;
    private CopyStatus status = CopyStatus.AVAILABLE;

    BookCopy(String title, String copyNumber) {
        this.title = User.requireText(title, "Title");
        this.copyNumber = User.requireText(copyNumber, "Copy number");
    }

    public String getTitle() {
        return title;
    }

    public String getCopyNumber() {
        return copyNumber;
    }

    public CopyStatus getStatus() {
        return status;
    }

    void checkOut() {
        if (status != CopyStatus.AVAILABLE) {
            throw new LibraryException("That book copy is already checked out.");
        }
        status = CopyStatus.CHECKED_OUT;
    }

    void returnToLibrary() {
        status = CopyStatus.AVAILABLE;
    }
}
