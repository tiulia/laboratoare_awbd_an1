package com.awbd.lab2.domain;

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
    @OneToOne(mappedBy = "info")
    private Product product;

}
