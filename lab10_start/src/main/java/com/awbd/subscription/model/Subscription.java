package com.awbd.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;




@Entity
@Getter
@Setter
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name="coach")
    @Size(max=20, message = "max 20 ch.")
    private String coach;

    @Column(name="sport")
    private String sport;

    @Column(name="price")
    private int price;

}
