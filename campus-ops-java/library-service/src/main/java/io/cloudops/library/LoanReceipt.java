package io.cloudops.library;

/** Receipt issued after a successful book checkout. */
public record LoanReceipt(String isbn, String memberId, String confirmationCode) {
}
