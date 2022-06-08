package com.example.reweb.repository;

import com.example.reweb.entity.Product;

import java.util.List;

public interface IProductRepository {
    List<Product> findAll();
    Product save(Product product);
    Boolean update(Integer id, Product product);
    Boolean delete(Integer id);
    Product findById(Integer id);
}
