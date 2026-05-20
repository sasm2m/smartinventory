package com.cedia.smartinventory.repository;

import com.cedia.smartinventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Vacío — Spring Data JPA genera la implementación en tiempo de ejecución.
    // Métodos ya disponibles sin escribir nada:
    //   findAll()          → SELECT * FROM products
    //   findById(id)       → SELECT * FROM products WHERE id = ?
    //   save(product)      → INSERT o UPDATE según corresponda
    //   deleteById(id)     → DELETE FROM products WHERE id = ?
    //   existsById(id)     → SELECT COUNT(*) FROM products WHERE id = ?
}