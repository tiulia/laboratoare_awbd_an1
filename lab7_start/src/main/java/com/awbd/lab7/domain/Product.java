package com.awbd.lab7.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "[A-Z]*", message = "only capital letters")
    private String code;

    private String name;

    @Min(value=100, message ="min price 100")
    private Double reservePrice;

    private Boolean restored;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Info info;

    @ManyToOne
    private Participant seller;

    @ManyToMany
    @JoinTable(name = "product_category",
            joinColumns =@JoinColumn(name="product_id",referencedColumnName = "id"),
            inverseJoinColumns =@JoinColumn(name="category_id",referencedColumnName="id"))

    private List<Category> categories;

    @Enumerated(EnumType.STRING)
    private Currency currency;


}
