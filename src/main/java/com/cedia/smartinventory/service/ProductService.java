package com.cedia.smartinventory.service;

import com.cedia.smartinventory.model.Product;
import com.cedia.smartinventory.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
        // Hibernate genera: SELECT * FROM products
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
        // Hibernate genera: SELECT * FROM products WHERE id = ?
    }

    public Product create(Product product) {
        return productRepository.save(product);
        // Hibernate genera: INSERT INTO products (...) VALUES (...)
        // La BD asigna el id automáticamente — no necesitas AtomicLong
    }

    public Optional<Product> update(Long id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setStock(updated.getStock());
            existing.setActive(updated.getActive());
            return productRepository.save(existing);
            // Hibernate genera: UPDATE products SET ... WHERE id = ?
        });
    }

    public boolean delete(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            // Hibernate genera: DELETE FROM products WHERE id = ?
            return true;
        }
        return false;
    }
}