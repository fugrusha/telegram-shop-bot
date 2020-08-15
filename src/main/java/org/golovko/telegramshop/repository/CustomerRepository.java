package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    boolean existsByChatId(long chatId);

    Customer findByChatId(long chatId);
}
