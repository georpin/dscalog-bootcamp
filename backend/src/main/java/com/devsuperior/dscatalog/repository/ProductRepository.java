package com.devsuperior.dscatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
