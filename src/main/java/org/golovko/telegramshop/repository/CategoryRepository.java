package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends CrudRepository<Category, UUID> {

    @Query("from Category c order by c.name asc")
    List<Category> getAllCategories();
}
