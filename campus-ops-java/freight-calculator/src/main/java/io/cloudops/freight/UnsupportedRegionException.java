package io.cloudops.freight;

/** Raised when a quote request specifies a missing or unsupported region. */
public class UnsupportedRegionException extends RuntimeException {

    /**
     * Constructs an UnsupportedRegionException.
     *
     * @param message descriptive error text
     */
    public UnsupportedRegionException(final String message) {
        super(message);
    }
}
