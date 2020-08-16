package org.golovko.telegramshop.service;

import org.golovko.telegramshop.cache.ShoppingCartCache;
import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.domain.OrderItem;
import org.golovko.telegramshop.domain.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartCache cartCache;

    public List<CartItem> findAllCartItemsByChatId(long chatId) {
        return cartCache.findAllCartItemsByChatId(chatId);
    }

    public CartItem findCartItemByChatIdAndProductId(long chatId, UUID productId) {
        return cartCache.findCartItemByChatIdAndProductId(chatId, productId);
    }

    public void updateCartItem(long chatId, CartItem cartItem) {
        cartCache.updateCartItem(chatId, cartItem);
    }

    public void saveCartItem(long chatId, CartItem cartItem) {
        cartCache.saveCartItem(chatId, cartItem);
    }

    public void deleteAllCartItemsByChatId(long chatId) {
        cartCache.deleteAllCartItemsByChatId(chatId);
    }

    public void copyOrderToShoppingCart(OrderCart order, long chatId) {
        deleteAllCartItemsByChatId(chatId);

        for (OrderItem item : order.getItems()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(item.getProduct());
            cartItem.setQuantity(item.getQuantity());

            saveCartItem(chatId, cartItem);
        }
    }
}
