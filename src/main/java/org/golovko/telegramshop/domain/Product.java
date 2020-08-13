package org.golovko.telegramshop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    private String photoUrl;

    private Double price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @Column(unique = true)
    private Integer orderNumber;
}
