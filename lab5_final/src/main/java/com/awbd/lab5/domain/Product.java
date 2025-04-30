package com.awbd.lab5.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private Double reservePrice;
    private Boolean restored;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Info info;

    @ManyToOne
    private Participant seller;

    @ManyToMany
    @JoinTable(name = "product_category",
            inverseJoinColumns =@JoinColumn(name="category_id",referencedColumnName = "id"),
            joinColumns =@JoinColumn(name="product_id",referencedColumnName="id"))
    private List<Category> categories;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

}
