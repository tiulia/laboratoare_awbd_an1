package com.awbd.lab5.dtos;


import com.awbd.lab5.domain.Category;
import com.awbd.lab5.domain.Currency;

import com.awbd.lab5.domain.Info;
import com.awbd.lab5.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String code;
    private Double reservePrice;
    private Boolean restored;
    private Info info;
    private Participant seller;
    private List<Category> categories;
    private Currency currency;

}
