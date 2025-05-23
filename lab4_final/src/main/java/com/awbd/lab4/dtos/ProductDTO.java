package com.awbd.lab4.dtos;


import com.awbd.lab4.domain.Category;
import com.awbd.lab4.domain.Currency;

import com.awbd.lab4.domain.Info;
import com.awbd.lab4.domain.Participant;
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
