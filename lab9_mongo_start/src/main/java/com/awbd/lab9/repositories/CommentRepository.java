package com.awbd.lab9.repositories;

import com.awbd.lab9.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {

}
