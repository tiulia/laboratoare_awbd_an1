package com.awbd.lab4.domain;

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

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Info info;

    @ManyToOne
    private Participant seller;

    @ManyToMany(mappedBy = "products")
    private List<Category> categories;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

}
