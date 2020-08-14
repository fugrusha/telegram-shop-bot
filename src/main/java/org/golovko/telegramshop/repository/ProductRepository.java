package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {
    Product findByCategoryNameAndOrderNumber(String categoryName, int orderNumber);
}
