package com.awbd.lab3.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private byte[] photo;
    private String description;
    @OneToOne
    private Product product;

}
