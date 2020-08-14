package org.golovko.telegramshop.service;

import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductByCategoryNameAndOrderNumber(String categoryName, int orderNumber) {
        return productRepository.findByCategoryNameAndOrderNumber(categoryName, orderNumber);
    }
}
