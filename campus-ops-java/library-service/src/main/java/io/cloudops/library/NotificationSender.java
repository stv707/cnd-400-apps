package io.cloudops.library;

/** Outbound notification port for borrower receipts. */
public interface NotificationSender {

    /**
     * Sends a confirmation alert to a borrower.
     *
     * @param memberId borrower identifier
     * @param isbn checked-out ISBN
     * @param confirmationCode generated loan confirmation token
     */
    void sendLoanConfirmation(String memberId, String isbn, String confirmationCode);
}
