package com.ecommerce.cart_service.dto;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private double price;
    private int stock;

    public ProductResponseDTO() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
