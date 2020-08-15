package org.golovko.telegramshop.cache;

import org.golovko.telegramshop.domain.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartCache {

    void saveCartItem(Long chatId, CartItem cartItem);

    void updateCartItem(Long chatId, CartItem cartItem);

    void deleteCartItem(Long chatId, Integer cartItemId);

    CartItem findCartItemByChatIdAndProductId(Long chatId, UUID productId);

    List<CartItem> findAllCartItemsByChatId(Long chatId);

    void deleteAllCartItemsByChatId(Long chatId);

    void setPageNumber(Long chatId, Integer pageNumber);

    Integer findPageNumberByChatId(Long chatId);
}
