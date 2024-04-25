package com.awbd.lab9.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Setter
@Getter
@ToString
@Document(collection = "comments")
public class Comment {

    private ObjectId id;
    private String name;
    private String email;
    private String text;
    private Date date;

    @Field("movie_id")
    private ObjectId movieId;
}
