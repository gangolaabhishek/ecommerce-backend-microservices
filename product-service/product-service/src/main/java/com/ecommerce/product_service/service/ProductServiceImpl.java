package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product savedProduct = productRepository.save(product);

        return mapRoResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id));


        return mapRoResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product updated = productRepository.save(product);
        return new ProductResponse(
                product.getId(),product.getName(), product.getDescription(), product.getPrice(), product.getStock()
        );
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);

    }

//    just mapping for return statement as it is repeating itself

    private ProductResponse mapRoResponse(Product product){
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());

        return response;
    }


    //multithreading example
    public void reduceInventory(Long productId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Product not found"));

        synchronized (product) {

            if (product.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }

            product.setStock(product.getStock() - quantity);

            productRepository.save(product);
        }
    }

    // JUST FOR STIMULATING THAT 3 THREAD REDUCE IT ALL AT ONCE
    public void simulateConcurrentInventoryUpdate(Long productId) {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> reduceInventory(productId, 2);
        Runnable task2 = () -> reduceInventory(productId, 3);
        Runnable task3 = () -> reduceInventory(productId, 1);

        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();
    }
}
