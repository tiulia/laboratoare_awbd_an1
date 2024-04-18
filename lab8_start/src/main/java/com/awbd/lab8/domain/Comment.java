package com.awbd.lab8.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import org.bson.types.ObjectId;


@Setter
@Getter
@ToString
public class Comment {

    private ObjectId id;

    private String name;
    private String email;
    private String text;
    private Date date;
}
