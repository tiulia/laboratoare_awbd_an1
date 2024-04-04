package com.awbd.lab5.repositories;

import com.awbd.lab5.domain.Product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    Optional<Product> findById (Long id);
    Optional<Product> findByName (String name);

    void deleteById(Long id);
    Product save(Product product);

    @Query("select p from Product p where p.seller.id = ?1")
    List<Product> findBySeller(Long sellerId);

    @Query("select p from Product p where p.seller.firstName = :firstName and p.seller.lastName = :lastName")
    List<Product> findBySellerName(@Param("firstName") String sellerFirstName, @Param("lastName") String sellerLastName);

}