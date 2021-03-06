package org.golovko.telegramshop.service;

import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductByCategoryName(String categoryName) {
        return productRepository.findByCategoryNameOrderByOrderNumber(categoryName);
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).get();
    }
}
