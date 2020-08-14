package org.golovko.telegramshop.repository;

import org.golovko.telegramshop.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, UUID> {

    boolean existsByChatId(Long chatId);
}
