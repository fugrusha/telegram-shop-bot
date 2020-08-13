package org.golovko.telegramshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private OrderCart orderCart;

    public OrderItem() {
    }

    public OrderItem(
            OrderCart orderCart,
            Product product,
            Integer quantity
    ) {
        this.orderCart = orderCart;
        this.product = product;
        this.quantity = quantity;
    }
}
