package com.awbd.lab6.dtos;

import com.awbd.lab6.domain.Category;
import com.awbd.lab6.domain.Currency;
import com.awbd.lab6.domain.Info;
import com.awbd.lab6.domain.Participant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "[A-Z]*", message = "only capital letters")
    private String code;

    @Min(value=100, message ="min price 100")
    private Double reservePrice;
    private Boolean restored;
    private Info info;
    private Participant seller;
    private List<Category> categories;
    private Currency currency;

}
