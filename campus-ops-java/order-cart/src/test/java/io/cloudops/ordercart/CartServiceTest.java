package io.cloudops.ordercart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void mergesMatchingSkuAndTotalsProperly() {
        cartService.addItem("c-101", new CartItem("NOTEBOOK", 2, new BigDecimal("4.50")));
        Cart cart = cartService.addItem("c-101", new CartItem("NOTEBOOK", 1, new BigDecimal("4.50")));

        assertEquals(1, cart.items().size());
        assertEquals(3, cart.items().getFirst().quantity());
        assertEquals(new BigDecimal("13.50"), cart.total());
    }

    @Test
    void clearCartEmptiesLineItems() {
        cartService.addItem("c-102", new CartItem("PEN", 4, new BigDecimal("1.25")));
        cartService.clearCart("c-102");

        Cart empty = cartService.getCart("c-102");
        assertEquals(0, empty.items().size());
        assertEquals(BigDecimal.ZERO, empty.total());
    }

    @Test
    void rejectsNegativeQuantityAndBlankSku() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem("", 1, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new CartItem("PEN", 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> cartService.getCart(" "));
    }
}
