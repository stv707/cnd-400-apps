package io.cloudops.library;

/** Port for looking up and updating book inventory state. */
public interface CatalogClient {

    /**
     * Retrieves catalog details for an ISBN.
     *
     * @param isbn normalized ISBN digits
     * @return book summary, or null when not present
     */
    BookSummary findByIsbn(String isbn);

    /**
     * Marks a copy checked out to the specified member.
     *
     * @param isbn normalized ISBN digits
     * @param memberId borrower identifier
     */
    void markCheckedOut(String isbn, String memberId);
}
