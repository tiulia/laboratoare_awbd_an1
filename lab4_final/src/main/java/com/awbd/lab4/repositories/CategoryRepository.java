package com.awbd.lab4.repositories;
import com.awbd.lab4.domain.Category;
import org.springframework.data.repository.CrudRepository;
public interface CategoryRepository extends CrudRepository<Category, Long> {

}