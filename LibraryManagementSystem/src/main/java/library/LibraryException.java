package library;

public final class LibraryException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public LibraryException(String message) {
        super(message);
    }
}
