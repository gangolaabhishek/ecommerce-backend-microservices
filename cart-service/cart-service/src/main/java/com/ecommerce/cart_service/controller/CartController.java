package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CartRequestDTO;
import com.ecommerce.cart_service.dto.CartResponseDTO;
import com.ecommerce.cart_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartRequestDTO requestDTO){
        return new ResponseEntity<>(cartService.addToCart(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDTO>> getCart(@PathVariable Long userId){
        return new ResponseEntity<>(cartService.getCartByUserId(userId),HttpStatus.FOUND);
    }

    //Remove Specific Item
    @DeleteMapping("/remove")
    public String removeFromCart(@RequestParam Long userId,
                                 @RequestParam Long productId) {

        cartService.removeFromCart(userId, productId);
        return "Item removed from cart";
    }

    // Clear Entire Cart
    @DeleteMapping("/clear/{userId}")
    public String clearCart(@PathVariable Long userId) {

        cartService.clearCart(userId);
        return "Cart cleared successfully";
    }

    @PostMapping("/simulate")
    public String simulateCartUpdate(@RequestParam Long userId,
                                     @RequestParam Long productId) {

        cartService.simulateConcurrentCartUpdate(userId, productId);
        return "Concurrent cart update simulation started";
    }
}
