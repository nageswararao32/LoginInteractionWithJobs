package com.strms.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strms.demo.Entites.Products;

public interface ProductsRepository extends JpaRepository<Products, String> {

}
