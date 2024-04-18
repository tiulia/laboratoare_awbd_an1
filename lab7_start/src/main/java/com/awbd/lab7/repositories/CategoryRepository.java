package com.awbd.lab7.repositories;
import com.awbd.lab7.domain.Category;
import org.springframework.data.repository.CrudRepository;
public interface CategoryRepository extends CrudRepository<Category, Long> {

}