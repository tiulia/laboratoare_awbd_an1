package com.awbd.lab8.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

@Setter
@Getter
@ToString
public class Movie {

    private ObjectId id;
    private String title;
    private String plot;
    private Date released;
    private List<String> cast;
    private String year;
    private Imdb imdb;

    private Set<Comment> comments = new HashSet<>();
}

