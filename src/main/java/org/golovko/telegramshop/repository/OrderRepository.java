package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.OrderCart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<OrderCart, UUID> {

    @Query("from OrderCart o where o.customer.chatId = :chatId")
    List<OrderCart> findOrderByChatIdOrderByCreatedDateDesc(long chatId);
}
