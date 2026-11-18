package io.cloudops.ordercart;

import java.math.BigDecimal;
import java.util.List;

/** Read view of a student order cart with derived total amount. */
public record Cart(String cartId, List<CartItem> items, BigDecimal total) {

    /** Constructs a defensive copy of cart line items and sums line totals. */
    public static Cart fromItems(final String cartId, final List<CartItem> sourceItems) {
        List<CartItem> copy = List.copyOf(sourceItems);
        BigDecimal total = copy.stream()
            .map(CartItem::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Cart(cartId, copy, total);
    }
}
