package org.golovko.telegramshop.service;

import org.golovko.telegramshop.cache.ShoppingCartCache;
import org.golovko.telegramshop.domain.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartCache cartCache;

    public List<CartItem> findAllCartItemsByChatId(Long chatId) {
        return cartCache.findAllCartItemsByChatId(chatId);
    }

    public CartItem findCartItemByChatIdAndProductId(Long chatId, UUID productId) {

        return cartCache.findCartItemByChatIdAndProductId(chatId, productId);
    }

    public void updateCartItem(Long chatId, CartItem cartItem) {
        cartCache.updateCartItem(chatId, cartItem);
    }

    public void saveCartItem(Long chatId, CartItem cartItem) {
        cartCache.saveCartItem(chatId, cartItem);
    }

    public void deleteAllCartItemsByChatId(Long chatId) {
        cartCache.deleteAllCartItemsByChatId(chatId);
    }
}
