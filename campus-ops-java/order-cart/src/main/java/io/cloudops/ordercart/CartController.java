package io.cloudops.ordercart;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API endpoints for managing Campus Ops shopping carts. */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    /**
     * Creates a CartController backed by CartService.
     *
     * @param cartService cart domain service
     */
    public CartController(final CartService cartService) {
        this.cartService = cartService;
    }

    /** Retrieves the contents and total of a cart. */
    @GetMapping("/{cartId}")
    public Cart getCart(@PathVariable final String cartId) {
        return cartService.getCart(cartId);
    }

    /** Adds an item to the cart and returns HTTP 201 Created. */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<Cart> addItem(@PathVariable final String cartId, @RequestBody final CartItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(cartId, item));
    }

    /** Clears all items from the cart. */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable final String cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    /** Translates validation errors to HTTP 400 JSON payloads. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
