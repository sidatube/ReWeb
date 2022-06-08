package com.example.reweb.repository;

import com.example.reweb.entity.Product;
import com.example.reweb.model.UltraModel;

import java.util.List;

public class ProductRepository implements IProductRepository {
    private final UltraModel<Product> productModel = new UltraModel<>(Product.class);
    @Override
    public List<Product> findAll() {
        return productModel.getAll();
    }

    @Override
    public Product save(Product product) {
        productModel.save(product);
        return product;
    }

    @Override
    public Boolean update(Integer id, Product updateProduct) {
        Product existProduct = productModel.findByPF(id);

        existProduct.setName(updateProduct.getName());
        existProduct.setPrice(updateProduct.getPrice());

        return productModel.update(id, existProduct);
    }

    @Override
    public Boolean delete(Integer id) {
        return productModel.remove(id);
    }

    @Override
    public Product findById(Integer id) {
        return productModel.findByPF(id);
    }
}
