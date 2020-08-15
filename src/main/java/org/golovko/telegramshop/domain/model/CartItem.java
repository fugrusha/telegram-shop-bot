package org.golovko.telegramshop.domain.model;

import lombok.Data;
import org.golovko.telegramshop.domain.Product;

@Data
public class CartItem {

    private Integer id;
    private Product product;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return quantity * product.getPrice();
    }
}
