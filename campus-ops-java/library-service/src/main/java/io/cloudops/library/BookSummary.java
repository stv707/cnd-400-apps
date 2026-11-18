package io.cloudops.library;

/** Immutable catalog summary returned for a library title. */
public record BookSummary(String isbn, String title, boolean available) {
}
