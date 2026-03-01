package com.ecommerce.cart_service.dto;

public class CartResponseDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;

    public CartResponseDTO() {}

    public CartResponseDTO(Long id, Long userId, Long productId, int quantity) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
