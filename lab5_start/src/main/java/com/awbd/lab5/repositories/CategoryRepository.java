package com.awbd.lab5.repositories;
import com.awbd.lab5.domain.Category;
import org.springframework.data.repository.CrudRepository;
public interface CategoryRepository extends CrudRepository<Category, Long> {

}