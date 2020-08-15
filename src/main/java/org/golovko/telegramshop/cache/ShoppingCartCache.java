package org.golovko.telegramshop.cache;

import org.golovko.telegramshop.domain.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
public class ShoppingCartCache implements CartCache {

    private final AtomicInteger lastCartItemId = new AtomicInteger();
    private final Map<Long, List<CartItem>> cartItems = new HashMap<>();

    private final Map<Long, Integer> cartPageNumbers = new HashMap<>();

    @Override
    public void saveCartItem(Long chatId, CartItem cartItem) {
        cartItems.computeIfAbsent(chatId, orderItems -> new ArrayList<>());

        cartItem.setId(lastCartItemId.incrementAndGet());
        cartItems.get(chatId).add(cartItem);
    }

    @Override
    public void updateCartItem(Long chatId, CartItem cartItem) {
        cartItems.computeIfAbsent(chatId, cartItems -> new ArrayList<>());
        List<CartItem> receivedCartItems = cartItems.get(chatId);

        IntStream.range(0, receivedCartItems.size())
                .filter(i -> cartItem.getId().equals(receivedCartItems.get(i).getId()))
                .findFirst()
                .ifPresent(i -> receivedCartItems.set(i, cartItem));
    }

    @Override
    public void deleteCartItem(Long chatId, Integer cartItemId) {
        cartItems.computeIfAbsent(chatId, cartItems -> new ArrayList<>());
        List<CartItem> receivedCartItems = cartItems.get(chatId);

        receivedCartItems.stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst()
                .ifPresent(receivedCartItems::remove);
    }

    @Override
    public CartItem findCartItemByChatIdAndProductId(Long chatId, UUID productId) {
        cartItems.computeIfAbsent(chatId, cartItems -> new ArrayList<>());
        return cartItems.get(chatId).stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CartItem> findAllCartItemsByChatId(Long chatId) {
        cartItems.computeIfAbsent(chatId, cartItems -> new ArrayList<>());

        return new ArrayList<>(cartItems.get(chatId));
    }

    @Override
    public void deleteAllCartItemsByChatId(Long chatId) {
        cartItems.remove(chatId);
    }

    @Override
    public void setPageNumber(Long chatId, Integer pageNumber) {
        cartPageNumbers.put(chatId, pageNumber);
    }

    @Override
    public Integer findPageNumberByChatId(Long chatId) {
        return cartPageNumbers.getOrDefault(chatId, 0);
    }
}
