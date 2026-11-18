package io.cloudops.ordercart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** In-memory shopping cart state manager for Campus Ops orders. */
@Service
public class CartService {

    private final Map<String, List<CartItem>> carts = new ConcurrentHashMap<>();

    /**
     * Appends an item to the specified cart, merging matching SKUs at the same price.
     *
     * @param cartId cart identifier
     * @param item item to add
     * @return updated Cart snapshot
     */
    public Cart addItem(final String cartId, final CartItem item) {
        String normalizedId = normalizeCartId(cartId);
        List<CartItem> items = carts.computeIfAbsent(normalizedId, ignored -> new ArrayList<>());
        synchronized (items) {
            int existingIdx = findSkuIndex(items, item.sku());
            if (existingIdx >= 0) {
                CartItem existing = items.get(existingIdx);
                items.set(existingIdx, new CartItem(
                    existing.sku(),
                    existing.quantity() + item.quantity(),
                    item.unitPrice()));
            } else {
                items.add(item);
            }
            return Cart.fromItems(normalizedId, items);
        }
    }

    /**
     * Fetches the current snapshot of a cart.
     *
     * @param cartId cart identifier
     * @return Cart view (empty list when unknown)
     */
    public Cart getCart(final String cartId) {
        String normalizedId = normalizeCartId(cartId);
        List<CartItem> items = carts.getOrDefault(normalizedId, List.of());
        synchronized (items) {
            return Cart.fromItems(normalizedId, items);
        }
    }

    /**
     * Removes all items stored under the specified cart.
     *
     * @param cartId cart identifier
     */
    public void clearCart(final String cartId) {
        carts.remove(normalizeCartId(cartId));
    }

    private int findSkuIndex(final List<CartItem> items, final String sku) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).sku().equals(sku)) {
                return i;
            }
        }
        return -1;
    }

    private String normalizeCartId(final String cartId) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId must not be blank");
        }
        return cartId.trim();
    }
}
