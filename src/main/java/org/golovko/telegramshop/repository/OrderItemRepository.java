package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, UUID> {
}
