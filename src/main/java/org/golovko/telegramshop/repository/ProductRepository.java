package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {
    List<Product> findByCategoryNameOrderByOrderNumber(String categoryName);
}
