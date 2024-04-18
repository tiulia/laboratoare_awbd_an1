package com.awbd.lab6.repositories;
import com.awbd.lab6.domain.Category;
import org.springframework.data.repository.CrudRepository;
public interface CategoryRepository extends CrudRepository<Category, Long> {

}