package io.cloudops.library;

import java.util.Objects;

/** Coordinates campus book checkouts using catalog and notification collaborators. */
public class LoanService {

    private final CatalogClient catalogClient;
    private final NotificationSender notificationSender;

    /**
     * Constructs a LoanService with its required ports.
     *
     * @param catalogClient catalog lookup and update client
     * @param notificationSender member notification dispatcher
     */
    public LoanService(final CatalogClient catalogClient, final NotificationSender notificationSender) {
        this.catalogClient = Objects.requireNonNull(catalogClient, "catalogClient must not be null");
        this.notificationSender = Objects.requireNonNull(
            notificationSender, "notificationSender must not be null");
    }

    /**
     * Checks out a book for a member when the ISBN is valid and available.
     *
     * @param rawIsbn raw ISBN input (hyphens allowed)
     * @param memberId non-blank borrower identifier
     * @return loan receipt with confirmation code
     */
    public LoanReceipt checkout(final String rawIsbn, final String memberId) {
        String isbn = normalizeAndValidateIsbn(rawIsbn);
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member ID must not be blank");
        }

        BookSummary book = catalogClient.findByIsbn(isbn);
        if (book == null) {
            throw new IllegalStateException("Book not found for ISBN: " + isbn);
        }
        if (!book.available()) {
            throw new IllegalStateException("Book currently checked out: " + isbn);
        }

        catalogClient.markCheckedOut(isbn, memberId.trim());
        String code = "LN-" + isbn.substring(isbn.length() - 4) + "-" + memberId.trim().toUpperCase();
        notificationSender.sendLoanConfirmation(memberId.trim(), isbn, code);
        return new LoanReceipt(isbn, memberId.trim(), code);
    }

    private String normalizeAndValidateIsbn(final String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) {
            throw new InvalidIsbnException("ISBN must not be blank");
        }
        String compact = rawIsbn.replace("-", "").trim();
        if (compact.length() != 10 && compact.length() != 13) {
            throw new InvalidIsbnException("ISBN must contain 10 or 13 characters: " + rawIsbn);
        }
        if (!compact.chars().allMatch(Character::isDigit)) {
            throw new InvalidIsbnException("ISBN must contain digits only: " + rawIsbn);
        }
        return compact;
    }
}
