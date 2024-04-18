package com.awbd.lab8.repositories;

import com.awbd.lab8.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {

}
