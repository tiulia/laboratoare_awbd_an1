package com.awbd.lab3.repositories;
import com.awbd.lab3.domain.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

}