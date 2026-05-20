package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.model.Product;

import main.java.com.cedia.smartinventory.service.ProductService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // Constructor injection — Spring inyecta ProductService automáticamente
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products
    @GetMapping
    public ResponseEntity<List<Product>> listar() {
        return ResponseEntity.ok(productService.findAll());
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> buscarPorId(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products
    @PostMapping
    public ResponseEntity<Product> crear(@RequestBody Product product) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(productService.create(product));
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> actualizar(
            @PathVariable Long id,
            @RequestBody Product product) {
        return productService.update(id, product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }    
    
    // GET /api/products/{id}
    //@GetMapping("/{id}")
    //public Product buscarPorId(@PathVariable Long id) {
    //    return products.stream()
    //        .filter(p -> p.getId().equals(id))
    //        .findFirst()
    //        .orElse(null);
    //}
}