package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.CartRequestDTO;
import com.ecommerce.cart_service.dto.CartResponseDTO;

import java.util.List;

public interface CartService {

    public CartResponseDTO addToCart(CartRequestDTO cartRequestDTO);

    public List<CartResponseDTO> getCartByUserId(Long userId);
    void removeFromCart(Long userId, Long productId);

    void clearCart(Long userId);

    void simulateConcurrentCartUpdate(Long userId, Long productId);
}
