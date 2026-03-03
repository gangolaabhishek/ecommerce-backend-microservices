package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.CartRequestDTO;
import com.ecommerce.cart_service.dto.CartResponseDTO;
import com.ecommerce.cart_service.dto.ProductResponseDTO;
import com.ecommerce.cart_service.entity.Cart;
import com.ecommerce.cart_service.exception.ResourceNotFoundException;
import com.ecommerce.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository, RestTemplate restTemplate) {
        this.cartRepository = cartRepository;
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;

    @Override
    public CartResponseDTO addToCart(CartRequestDTO request) {

        // Call Product Service rest template
        String productServiceUrl =
                "http://localhost:8082/api/products/" + request.getProductId();

        ProductResponseDTO product =
                restTemplate.getForObject(productServiceUrl,
                        ProductResponseDTO.class);

        if (product == null) {
            throw new RuntimeException("Product not found in Product Service");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock in Product Service");
        }


        synchronized (this) {

            Optional<Cart> existingCart =
                    cartRepository.findByUserIdAndProductId(
                            request.getUserId(),
                            request.getProductId()
                    );

            Cart cart;

            if (existingCart.isPresent()) {

                cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + request.getQuantity());

            } else {

                cart = new Cart(
                        request.getUserId(),
                        request.getProductId(),
                        request.getQuantity()
                );
            }

            Cart savedCart = cartRepository.save(cart);

            return mapToResponse(savedCart);
        }

    }

    @Override
    public List<CartResponseDTO> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        cartRepository.delete(cart);

    }

    @Override
    public void clearCart(Long userId) {
        List<Cart> cartList = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(cartList);

    }

    @Override
    public void simulateConcurrentCartUpdate(Long userId, Long productId) {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> addToCart(createRequest(userId, productId, 1));
        Runnable task2 = () -> addToCart(createRequest(userId, productId, 2));
        Runnable task3 = () -> addToCart(createRequest(userId, productId, 3));

        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();
    }

    private CartRequestDTO createRequest(Long userId, Long productId, int qty) {

        CartRequestDTO dto = new CartRequestDTO();
        dto.setUserId(userId);
        dto.setProductId(productId);
        dto.setQuantity(qty);

        return dto;
    }

    private CartResponseDTO mapToResponse(Cart cart) {

        return new CartResponseDTO(
                cart.getId(),
                cart.getUserId(),
                cart.getProductId(),
                cart.getQuantity()
        );
    }
}
