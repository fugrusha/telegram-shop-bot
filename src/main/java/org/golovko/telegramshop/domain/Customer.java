package org.golovko.telegramshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private Long chatId;

    private String name;

    private String surname;

    private String phone;

    private String city;

    private String address;

    @OneToMany(mappedBy = "customer")
    private List<OrderCart> orderCarts = new ArrayList<>();
}
