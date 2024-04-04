package com.awbd.lab5.dtos;

import com.awbd.lab5.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO {

    private Long id;
    private String lastName;
    private String firstName;
    private java.util.Date birthDate;

    private List<Product> products;

}
