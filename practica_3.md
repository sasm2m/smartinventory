# Sesión 04 — Práctica: CRUD completo + persistencia real con JPA
### Programación Backend Java con Spring Boot potenciada con IA
**Lunes 18 de mayo de 2026 — Segunda hora**

---

## Punto A → Punto B

```
Punto A: ProductController con GET en memoria, sin Service, sin JPA
Punto B: API CRUD completa — Controller + Service + Repository + BD real
         corriendo primero sobre H2, luego sobre PostgreSQL sin tocar Java
```

Al terminar tendrás:
- ✅ `ProductService` — lógica separada del controller
- ✅ `ProductController` — CRUD completo con `ResponseEntity` y códigos HTTP correctos
- ✅ `Product.java` — evolucionado a entidad JPA con `@Entity`
- ✅ `ProductRepository` — interface que Spring implementa sola
- ✅ API corriendo sobre **H2** — datos que persisten entre requests
- ✅ El mismo código sobre **PostgreSQL** — solo cambiando `application.properties`
- ✅ 8 commits que documentan cada avance

> **La pregunta que responde esta práctica:** ¿cuántas líneas de Java hay que cambiar para pasar de H2 a PostgreSQL? La respuesta la ves al final.

---

## Estado del proyecto al entrar

Vienes de la sesión 3 con esto exactamente:

```
✅ model/Product.java               — Lombok, SIN @Entity, SIN @Id JPA
✅ controller/ProductController.java — solo GET, datos en List.of() inmutable
⬜ service/                         — vacío
⬜ repository/                      — vacío
⬜ application.properties           — vacío
   pom.xml                          — SIN JPA, SIN drivers de BD
```

**No borres nada.** Esta sesión evoluciona lo que existe.

---

## Paso 1 — Agregar dependencias al pom.xml (5 min)

Abre `pom.xml` y agrega dentro de `<dependencies>`:

```xml
<!-- JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 — base de datos en memoria para desarrollo -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL driver — para cuando cambiemos en el paso 8 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Guarda y espera que Maven descargue las dependencias.

> **¿Por qué agregar PostgreSQL ahora si arrancamos con H2?** El driver solo se activa cuando el `application.properties` lo referencia. Tenerlo en el `pom.xml` no hace nada hasta que lo configures — así no tocas Maven más adelante.

### 🔖 Commit 1

```bash
git add .
git commit -m "chore: dependencias JPA, H2 y PostgreSQL en pom.xml"
```

---

## Paso 2 — Evolucionar Product.java a entidad JPA (10 min)

Abre `model/Product.java` — el que ya existe desde sesión 3. **No lo borres.** Agrega las anotaciones JPA marcadas con `← NUEVO`:

```java
package com.cedia.smartinventory.model;

import jakarta.persistence.*;          // ← NUEVO
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity                                // ← NUEVO: esta clase = tabla en BD
@Table(name = "products")             // ← NUEVO: nombre explícito de la tabla
public class Product {

    @Id                                // ← NUEVO: clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← NUEVO: BD genera el ID
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean active;
}
```

### ¿Qué cambió y por qué?

| Antes — sesión 3 | Después — hoy | Por qué |
|---|---|---|
| `private Long id` sin anotar | `@Id @GeneratedValue(IDENTITY)` | La BD asigna el ID — adiós `AtomicLong` |
| Clase Java pura | `@Entity @Table(name = "products")` | JPA mapea esta clase a una tabla |
| Sin imports JPA | `import jakarta.persistence.*` | Las anotaciones vienen de Jakarta EE |
| `@NoArgsConstructor` ya existía | Sin cambios | JPA lo necesita para instanciar objetos desde la BD |

> **¿Por qué `@NoArgsConstructor` es obligatorio con JPA?** Hibernate necesita crear instancias vacías para luego llenarlas con los datos que trae de la BD. Sin el constructor vacío, lanza excepción al arrancar. Lombok ya lo generaba — perfecto.

### 🔖 Commit 2

```bash
git add .
git commit -m "feat: Product evolucionado a entidad JPA (@Entity, @Id, @GeneratedValue)"
```

---

## Paso 3 — Crear ProductRepository (5 min)

Dentro del paquete `repository/`, crea `ProductRepository.java`:

```java
package com.cedia.smartinventory.repository;

import com.cedia.smartinventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Vacío — Spring Data JPA genera la implementación en tiempo de ejecución.
    // Métodos ya disponibles sin escribir nada:
    //   findAll()          → SELECT * FROM products
    //   findById(id)       → SELECT * FROM products WHERE id = ?
    //   save(product)      → INSERT o UPDATE según corresponda
    //   deleteById(id)     → DELETE FROM products WHERE id = ?
    //   existsById(id)     → SELECT COUNT(*) FROM products WHERE id = ?
}
```

No escribas implementación. Spring la genera automáticamente.

> **`JpaRepository<Product, Long>`** — primer tipo: entidad que maneja. Segundo tipo: tipo de la clave primaria. Si `id` fuera `Integer`, sería `JpaRepository<Product, Integer>`.

### 🔖 Commit 3

```bash
git add .
git commit -m "feat: ProductRepository — interface JpaRepository para Product"
```

---

## Paso 4 — Configurar H2 en application.properties (5 min)

Abre `src/main/resources/application.properties` — está vacío desde sesión 3. Agrega:

```properties
# ── Base de datos H2 (desarrollo) ──────────────────────────────────────────
spring.datasource.url=jdbc:h2:mem:smartinventory
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Consola web de H2 — accesible en localhost:8080/h2-console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA crea las tablas al arrancar y las elimina al detener el servidor
spring.jpa.hibernate.ddl-auto=create-drop

# Muestra el SQL que genera Hibernate — útil para entender qué pasa
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Reinicia el servidor. El log debe mostrar:

```
Hibernate: create table products (id bigint generated by default as identity, ...)
Tomcat started on port(s): 8080
Started SmartInventoryApplication in 2.8 seconds
```

Si ves `create table products` — JPA está funcionando y creó la tabla automáticamente.

### Abrir la consola H2

Ve a `http://localhost:8080/h2-console` en el navegador.

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:smartinventory` |
| User Name | `sa` |
| Password | *(vacío)* |

Haz clic en **Connect**. Verás la tabla `PRODUCTS` en el panel izquierdo. Todavía está vacía.

> **`ddl-auto=create-drop`:** JPA crea las tablas al arrancar y las borra al detener. Perfecto para H2 donde los datos son temporales. **Nunca uses `create-drop` en producción** — borras todo en cada deploy.

### 🔖 Commit 4

```bash
git add .
git commit -m "chore: application.properties configurado para H2 con consola habilitada"
```

---

## Paso 5 — Crear ProductService con Repository (15 min)

Dentro del paquete `service/`, crea `ProductService.java`.

Este Service **no tiene lista en memoria**. Delega todo al Repository que habla con JPA.

```java
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
```

### El antes y el después del Service

| ProductService con lista (si lo hubiéramos hecho así) | ProductService con Repository (hoy) |
|---|---|
| `ArrayList` hardcodeado con datos | Sin datos — el Repository los maneja |
| `AtomicLong` para generar IDs manualmente | BD genera el ID con `@GeneratedValue` |
| `.stream().filter(...)` para buscar | `productRepository.findById(id)` |
| `.add(product)` para crear | `productRepository.save(product)` |
| `.removeIf(...)` para eliminar | `productRepository.deleteById(id)` |

> **`@Transactional` en el Service:** si algo falla dentro de un método, la BD hace rollback automáticamente al estado anterior. Spring Data ya aplica `@Transactional` en los métodos del Repository — aquí lo pones en el Service para cubrir operaciones que combinan varios pasos.

### 🔖 Commit 5

```bash
git add .
git commit -m "feat: ProductService conectado a ProductRepository — sin datos en memoria"
```

---

## Paso 6 — Actualizar ProductController (10 min)

Reemplaza el contenido de `ProductController.java`. El Controller que venía de sesión 3 tenía solo GET y datos en `List.of()`. Ahora tiene CRUD completo y delega al Service.

```java
package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.model.Product;
import com.cedia.smartinventory.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
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
}
```

### Verbos HTTP y códigos correctos

| Anotación | Verbo | Código | Cuándo |
|---|---|---|---|
| `@GetMapping` | GET | 200 OK | Obtener — no modifica nada |
| `@PostMapping` | POST | 201 Created | Crear un recurso nuevo |
| `@PutMapping` | PUT | 200 OK | Reemplazar un recurso completo |
| `@DeleteMapping` | DELETE | 204 No Content | Eliminar — sin body de respuesta |
| — | GET/DELETE sin resultado | 404 Not Found | El recurso no existe |

### 🔖 Commit 6

```bash
git add .
git commit -m "refactor: ProductController con CRUD completo, ResponseEntity y códigos HTTP correctos"
```

---

## Paso 7 — Probar sobre H2 (10 min)

Reinicia el servidor. Verifica en el log:

```
Hibernate: create table products (id bigint generated by default as identity, ...)
Tomcat started on port(s): 8080
```

Prueba con Postman en este orden:

| Verbo | URL | Código esperado | Qué verificar |
|---|---|---|---|
| GET | `localhost:8080/api/products` | 200 OK | Lista vacía `[]` — BD recién creada |
| POST | `localhost:8080/api/products` | 201 Created | Laptop con `"id": 1` asignado por la BD |
| POST | `localhost:8080/api/products` | 201 Created | Mouse con `"id": 2` |
| GET | `localhost:8080/api/products` | 200 OK | Lista con 2 productos |
| GET | `localhost:8080/api/products/1` | 200 OK | Solo Laptop |
| GET | `localhost:8080/api/products/99` | 404 Not Found | No existe |
| PUT | `localhost:8080/api/products/1` | 200 OK | Laptop actualizado |
| DELETE | `localhost:8080/api/products/1` | 204 No Content | Body vacío |
| GET | `localhost:8080/api/products/1` | 404 Not Found | Laptop ya no existe |

### JSON para POST — Laptop

```json
{
  "name": "Laptop",
  "description": "Laptop de alto rendimiento",
  "price": 1200.0,
  "stock": 10,
  "active": true
}
```

### JSON para POST — Mouse

```json
{
  "name": "Mouse",
  "description": "Mouse inalámbrico",
  "price": 25.5,
  "stock": 50,
  "active": true
}
```

### Verificar en la consola H2

Después de los POST, ve a `localhost:8080/h2-console` y ejecuta:

```sql
SELECT * FROM products;
```

Ves los datos que creaste. Ahora **reinicia el servidor** y vuelve a ejecutar el `SELECT`. La tabla está vacía — porque H2 con `create-drop` es volátil. Eso cambia en el siguiente paso.

### 🔖 Commit 7

```bash
git add .
git commit -m "docs: CRUD verificado sobre H2 — datos persisten entre requests, no entre reinicios"
```

---

## Paso 8 — Cambiar a PostgreSQL en vivo (10 min)

### Levantar PostgreSQL con Docker

Abre una terminal y ejecuta:

```bash
docker run --name smartinventory-db \
  -e POSTGRES_DB=smartinventory \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

Verifica que está corriendo:

```bash
docker ps
# Debes ver: smartinventory-db   postgres:15   Up X seconds
```

### Cambiar application.properties — solo estas líneas

Reemplaza **todo** el contenido del archivo:

```properties
# ── Base de datos PostgreSQL ────────────────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/smartinventory
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# update — actualiza el esquema sin borrar datos existentes
spring.jpa.hibernate.ddl-auto=update

# Muestra el SQL generado por Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Reinicia el servidor. El log ahora debe mostrar:

```
HikariPool: Starting — jdbc:postgresql://localhost:5432/smartinventory
Hibernate: create table products (...)   ← primera vez, crea la tabla en PostgreSQL
Tomcat started on port(s): 8080
```

### Probar que funciona igual

Repite los mismos POST y GET del paso 7. Todo se comporta igual — mismos endpoints, mismos códigos HTTP, mismas respuestas JSON.

La diferencia real: **reinicia el servidor y vuelve a hacer GET**. Los datos siguen ahí. PostgreSQL persiste en disco.

```bash
# 1. Crea un producto con POST
# 2. Reinicia el servidor desde el IDE
# 3. GET /api/products → el producto sigue ahí
```

### La respuesta a la pregunta del inicio

> ¿Cuántas líneas de Java cambiaste para pasar de H2 a PostgreSQL?

**Cero.**

`Product.java`, `ProductRepository`, `ProductService` y `ProductController` no tocaron ni una línea. Solo cambió `application.properties`. Eso es exactamente lo que hace JPA.

### 🔖 Commit 8

```bash
git add .
git commit -m "feat: SmartInventory corriendo sobre PostgreSQL via Docker — mismo código, diferente BD"
```

---

## Estado final del proyecto

```
smartinventory/
├── pom.xml                                   ← JPA + H2 + PostgreSQL
└── src/
    ├── main/
    │   ├── java/com/cedia/smartinventory/
    │   │   ├── SmartInventoryApplication.java
    │   │   ├── controller/
    │   │   │   └── ProductController.java    ← CRUD completo, ResponseEntity
    │   │   ├── service/
    │   │   │   └── ProductService.java       ← delega a Repository, @Transactional
    │   │   ├── repository/
    │   │   │   └── ProductRepository.java    ← interface vacía, Spring implementa
    │   │   ├── model/
    │   │   │   └── Product.java              ← @Entity + Lombok
    │   │   ├── dto/                          ← sesión 5
    │   │   └── exception/                    ← sesión 5
    │   └── resources/
    │       └── application.properties        ← PostgreSQL activo
```

### Historial Git completo al cerrar

```bash
git log --oneline
# feat: SmartInventory corriendo sobre PostgreSQL via Docker
# docs: CRUD verificado sobre H2
# refactor: ProductController con CRUD completo y ResponseEntity
# feat: ProductService conectado a ProductRepository
# chore: application.properties configurado para H2
# feat: ProductRepository — interface JpaRepository para Product
# feat: Product evolucionado a entidad JPA
# chore: dependencias JPA, H2 y PostgreSQL en pom.xml
# feat: ProductController con GET /api/products y GET /api/products/{id}   ← sesión 3
# feat: entidad Product con Lombok (model layer)                            ← sesión 3
# chore: estructura de paquetes por capas                                   ← sesión 3
# chore: proyecto base SmartInventory generado con Spring Initializr        ← sesión 3
```

---

## 🔬 Challenges para quienes terminan antes

### Challenge A — queries derivados sin SQL

Agrega estos métodos a `ProductRepository` sin escribir SQL ni implementación — solo el nombre correcto:

```java
// Spring genera: SELECT * FROM products WHERE name = ?
List<Product> findByName(String name);

// Spring genera: SELECT * FROM products WHERE active = ?
List<Product> findByActive(Boolean active);

// Spring genera: SELECT * FROM products WHERE price <= ?
List<Product> findByPriceLessThanEqual(Double maxPrice);

// Spring genera: SELECT * FROM products WHERE LOWER(name) LIKE %?%
List<Product> findByNameContainingIgnoreCase(String keyword);
```

Expón cada uno como endpoint en el Controller con `@RequestParam`. Prueba `GET /api/products?keyword=lap` — debe devolver solo Laptop.

---

### Challenge B — datos iniciales con data.sql

Crea `src/main/resources/data.sql`:

```sql
INSERT INTO products (name, description, price, stock, active)
VALUES ('Laptop', 'Laptop de alto rendimiento', 1200.0, 10, true);

INSERT INTO products (name, description, price, stock, active)
VALUES ('Mouse', 'Mouse inalambrico', 25.5, 50, true);

INSERT INTO products (name, description, price, stock, active)
VALUES ('Teclado', 'Teclado mecanico', 45.0, 30, true);
```

Agrega al `application.properties` de H2:

```properties
spring.sql.init.mode=always
```

Reinicia. El `GET /api/products` debe devolver 3 productos sin haberlos creado manualmente. ¿Qué pasa si cambias `ddl-auto` a `update` en lugar de `create-drop`?

---

### Challenge C — @Transactional de solo lectura

El `@Transactional` en el Service aplica a todos los métodos — incluyendo `findAll()` y `findById()`. Investiga `@Transactional(readOnly = true)` y aplícalo solo a los métodos de lectura:

```java
@Transactional(readOnly = true)
public List<Product> findAll() { ... }

@Transactional(readOnly = true)
public Optional<Product> findById(Long id) { ... }
```

¿Qué ventaja da en producción con muchas lecturas concurrentes?

---

## Lo que viene

| Sesión | Tema | Qué agrega a SmartInventory |
|---|---|---|
| 4 (hoy) | CRUD + JPA | Controller + Service + Repository + H2 + PostgreSQL |
| 5 | DTOs y errores | `@Valid` + `@ExceptionHandler` — validaciones y errores limpios |
| 6 | Seguridad | Spring Security + JWT — endpoints protegidos |
| 7 | Agentes IA — Parte 1 | Spring AI + LLM + herramientas sobre la API |
| 8 | Agentes IA — Parte 2 | Memoria + agente integrado en SmartInventory |
| 9 | Calidad | Tests + Swagger/OpenAPI |
| 10 | Proyecto final | Presentación + retroalimentación |

> **Para la sesión 5:** la API acepta ahora cualquier dato — precio negativo, nombre vacío, stock nulo. Con `@Valid` + DTOs eso se valida antes de llegar al Service. Y cuando algo falla hoy, Spring devuelve un HTML de error genérico — con `@ExceptionHandler` devuelves JSON limpio con el mensaje correcto.
