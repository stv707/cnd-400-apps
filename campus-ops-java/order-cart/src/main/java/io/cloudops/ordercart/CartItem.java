package io.cloudops.ordercart;

import java.math.BigDecimal;

/** Immutable line item stored inside a shopping cart. */
public record CartItem(String sku, int quantity, BigDecimal unitPrice) {

    /** Canonical constructor enforcing non-blank SKU and positive pricing constraints. */
    public CartItem {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
    }

    /** Computes subtotal for this item line. */
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
