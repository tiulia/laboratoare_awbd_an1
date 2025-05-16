package com.awbd.lab9.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@Document(collection = "movies")
public class Movie {

    private ObjectId id;
    private String title;
    private String plot;
    private Date released;
    private List<String> cast;
    private String year;
    private Imdb imdb;
}

