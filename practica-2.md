# Sesión 03 — Práctica: De Spring Initializr a tu primera API
### Programación Backend Java con Spring Boot potenciada con IA
**Jueves 14 de mayo de 2026 — Segunda hora**

---

## Punto A → Punto B

```
Punto A: Tienes el contexto teórico — Java moderno, arquitecturas y patrones
Punto B: Tienes el proyecto SmartInventory corriendo y respondiendo JSON real
```

Al terminar esta práctica tendrás en tu máquina:
- ✅ Proyecto Spring Boot creado y arrancando
- ✅ Repositorio Git inicializado con historial limpio
- ✅ Estructura de paquetes del proyecto completo
- ✅ Clase `Product` con Lombok
- ✅ Primer endpoint respondiendo en `localhost:8080/api/products`

---

## Git en este curso — la regla de trabajo

Usaremos Git como lo hace cualquier equipo profesional: **un commit por cada avance significativo**, con mensajes que describen qué se hizo y por qué.

Convención de mensajes que usaremos:

| Prefijo | Cuándo usarlo |
|---|---|
| `feat:` | Cuando agregas algo nuevo (clase, endpoint, configuración) |
| `refactor:` | Cuando reorganizas código sin cambiar su comportamiento |
| `fix:` | Cuando corriges un error |
| `docs:` | Cuando agregas o editas documentación |
| `chore:` | Tareas de mantenimiento (dependencias, estructura) |

> **¿Por qué mensajes descriptivos?** Porque el historial de Git es la documentación del proyecto. En 3 meses vas a agradecer saber exactamente qué cambió y cuándo.

---

## Paso 1 — Crear el proyecto en Spring Initializr (15 min)

Abre tu navegador y ve a **[https://start.spring.io](https://start.spring.io)**

Configura exactamente así:

| Campo | Valor |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.4.x (la más reciente estable) |
| Group | `com.cedia` |
| Artifact | `smartinventory` |
| Name | `smartinventory` |
| Description | `SmartInventory API — Curso Backend Java CEDIA` |
| Package name | `com.cedia.smartinventory` |
| Packaging | Jar |
| Java | 17 (mínimo) o 21 |

### Dependencias — agrega estas tres:

Busca y selecciona una por una en el panel derecho:

| Dependencia | Para qué sirve |
|---|---|
| **Spring Web** | Levantar el servidor HTTP y crear endpoints REST |
| **Spring Boot DevTools** | Recarga automática cuando guardas cambios |
| **Lombok** | Eliminar código repetitivo (getters, setters, constructores) |

> **¿Por qué no agregamos JPA ni base de datos hoy?**
> Porque Spring Boot intentaría conectarse a una BD que aún no existe y el proyecto no arrancaría. JPA entra en la sesión de persistencia cuando tenga sentido completo.

Una vez configurado, haz clic en **GENERATE** — descarga un archivo `.zip`.

### Importar en tu IDE

1. Descomprime el zip en una carpeta de tu elección
2. Abre tu IDE (IntelliJ IDEA, Eclipse o VS Code)
3. **File → Open** y selecciona la carpeta descomprimida
4. Espera a que Maven descargue las dependencias (barra de progreso)
5. Cuando termine, busca y ejecuta `SmartInventoryApplication.java`

### Verificar que funciona

Abre el navegador en `http://localhost:8080`

Deberías ver algo como:
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found"
}
```

> **¿Un error 404 es buena señal?** Sí. Significa que el servidor está corriendo — solo que aún no tiene rutas definidas. Eso lo arreglamos en el paso 3.

### 🔖 Commit 1 — Proyecto base

El proyecto arranca. Es el mejor momento para el primer commit — antes de tocar nada propio.

Abre una terminal en la carpeta del proyecto:

```bash
git init
git add .
git commit -m "chore: proyecto base SmartInventory generado con Spring Initializr"
```

Verifica con `git log` — deberías ver tu primer commit.

> **¿Por qué commitear antes de agregar código propio?** Porque si algo sale mal después, puedes volver a este punto limpio. El proyecto generado por Initializr es tu línea base.

---

## Paso 2 — Explorar la estructura generada (10 min)

Antes de escribir una sola línea, recorre cada archivo del proyecto. Nada está ahí por accidente.

```
smartinventory/
├── src/
│   ├── main/
│   │   ├── java/com/cedia/smartinventory/
│   │   │   └── SmartInventoryApplication.java   ← punto de entrada
│   │   └── resources/
│   │       └── application.properties           ← configuración
│   └── test/
│       └── java/com/cedia/smartinventory/
│           └── SmartInventoryApplicationTests.java
└── pom.xml                                      ← dependencias Maven
```

### ¿Qué hace cada archivo?

**`SmartInventoryApplication.java`**
```java
@SpringBootApplication
public class SmartInventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartInventoryApplication.class, args);
    }
}
```

`@SpringBootApplication` es en realidad tres anotaciones en una:
- `@Configuration` — esta clase define configuración
- `@EnableAutoConfiguration` — Spring configura todo automáticamente
- `@ComponentScan` — escanea todos los paquetes buscando componentes

> **Reflexión:** ¿Recuerdas el `main()` de Java puro de la sesión anterior? Este es el equivalente en Spring Boot — el punto de arranque. La diferencia es que `SpringApplication.run()` levanta un servidor HTTP completo.

**`application.properties`**
Vacío por ahora. Aquí irá la configuración de BD, puerto, seguridad. Lo iremos llenando sesión a sesión.

**`pom.xml`**
Las dependencias que seleccionaste en Spring Initializr. Maven las descarga automáticamente.

---

## Paso 3 — Crear la estructura de paquetes (5 min)

Esta es la arquitectura por capas que viste en la primera hora. Créala ahora — aunque la mayoría de carpetas queden vacías hoy.

Dentro de `com.cedia.smartinventory` crea estos paquetes:

```
com.cedia.smartinventory/
├── controller/     ← recibe peticiones HTTP        (sesión 5)
├── service/        ← lógica de negocio             (sesión 5)
├── repository/     ← acceso a datos                (sesión 6)
├── model/          ← entidades del dominio         (HOY ✅)
├── dto/            ← objetos de transferencia      (sesión 7)
└── exception/      ← manejo de errores             (sesión 7)
```

**En IntelliJ:** clic derecho sobre `smartinventory` → New → Package → escribe el nombre

> **¿Por qué crear carpetas vacías?**
> Porque la estructura es arquitectura. Cuando un compañero abra este proyecto en 3 meses, la estructura le dice cómo funciona antes de leer una línea de código.

### 🔖 Commit 2 — Estructura de paquetes

```bash
git add .
git commit -m "chore: estructura de paquetes por capas (controller, service, repository, model, dto, exception)"
```

---

## Paso 4 — Primera entidad: clase `Product` (15 min)

Dentro del paquete `model`, crea `Product.java`.

Esta es la misma clase que construiste a mano en la sesión anterior — pero ahora con Lombok haciendo el trabajo repetitivo:

```java
package com.cedia.smartinventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean active;

}
```

### ¿Qué hace cada anotación Lombok?

| Anotación | Lo que genera automáticamente |
|---|---|
| `@Data` | Todos los getters, setters, `equals()`, `hashCode()` y `toString()` |
| `@Builder` | Patrón Builder — `Product.builder().name("Laptop").build()` |
| `@NoArgsConstructor` | Constructor vacío — `new Product()` |
| `@AllArgsConstructor` | Constructor con todos los campos |

> **Compara con la sesión anterior:** en Java puro escribiste ~30 líneas para una clase similar. Lombok las genera en tiempo de compilación — el código existe, solo no lo ves. Puedes verificarlo en IntelliJ con el panel "Lombok" o haciendo clic en un getter que "no existe".

### Verifica que Lombok funciona

Escribe esto en cualquier parte del código y verifica que no da error de compilación:

```java
Product laptop = Product.builder()
    .id(1L)
    .name("Laptop")
    .description("Laptop de alto rendimiento")
    .price(1200.0)
    .stock(10)
    .active(true)
    .build();

System.out.println(laptop); // toString() generado por @Data
```

### 🔖 Commit 3 — Entidad Product

```bash
git add .
git commit -m "feat: entidad Product con Lombok (model layer)"
```

---

## Paso 5 — Primer endpoint real (15 min)

Dentro del paquete `controller`, crea `ProductController.java`.

Este controlador no tiene Service ni Repository todavía — los datos están en memoria. El objetivo es ver una respuesta JSON real en el navegador.

```java
package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Datos en memoria — temporal, solo para esta sesión
    private final List<Product> products = List.of(
        Product.builder().id(1L).name("Laptop")
            .description("Laptop de alto rendimiento")
            .price(1200.0).stock(10).active(true).build(),
        Product.builder().id(2L).name("Mouse")
            .description("Mouse inalámbrico")
            .price(25.5).stock(50).active(true).build(),
        Product.builder().id(3L).name("Teclado")
            .description("Teclado mecánico")
            .price(45.0).stock(30).active(true).build()
    );

    // GET /api/products
    @GetMapping
    public List<Product> listar() {
        return products;
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public Product buscarPorId(@PathVariable Long id) {
        return products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
```

### Ejecuta y prueba

1. Guarda el archivo — DevTools recarga automáticamente
2. Abre el navegador:

| URL | Qué deberías ver |
|---|---|
| `localhost:8080/api/products` | Lista de 3 productos en JSON |
| `localhost:8080/api/products/1` | Solo el producto con id 1 |
| `localhost:8080/api/products/99` | `null` — producto no existe |

> **Reflexión:** ¿Recuerdas el método `listar()` y `buscarPorId()` que escribiste en Java puro? Es exactamente el mismo — pero ahora Spring Boot lo expone como endpoint HTTP automáticamente con `@GetMapping`.

### ¿Qué hace cada anotación?

| Anotación | Qué hace |
|---|---|
| `@RestController` | Marca esta clase como controlador REST — devuelve JSON automáticamente |
| `@RequestMapping("/api/products")` | Prefijo de ruta para todos los métodos |
| `@GetMapping` | Responde a peticiones GET en `/api/products` |
| `@GetMapping("/{id}")` | Responde a GET en `/api/products/1`, `/api/products/2`, etc. |
| `@PathVariable` | Extrae el `{id}` de la URL y lo pasa como parámetro |

### 🔖 Commit 4 — Primer endpoint

```bash
git add .
git commit -m "feat: ProductController con GET /api/products y GET /api/products/{id}"
```

Verifica tu historial completo:

```bash
git log --oneline
```

Deberías ver los 4 commits del día:

```
a1b2c3d feat: ProductController con GET /api/products y GET /api/products/{id}
e4f5g6h feat: entidad Product con Lombok (model layer)
i7j8k9l chore: estructura de paquetes por capas
m0n1o2p chore: proyecto base SmartInventory generado con Spring Initializr
```

> **Esto es tu historial de trabajo.** Cada sesión del curso vas a sumar commits a este repositorio — al final tendrás la evolución completa de SmartInventory documentada en Git.

---

## Cierre — El mapa de lo que construirán (5 min)

Guarda y deja el proyecto corriendo. Este es el estado actual y lo que viene:

```
SmartInventory — estado al final de hoy
─────────────────────────────────────────
✅ controller/ProductController.java   ← datos en memoria, temporal
✅ model/Product.java                  ← entidad base con Lombok
⬜ service/                            ← sesión 5
⬜ repository/                         ← sesión 6
⬜ dto/                                ← sesión 7
⬜ exception/                          ← sesión 7
```

### Sesión a sesión — lo que construirás

| Sesión | Qué agregas | Resultado |
|---|---|---|
| 3 (hoy) | Proyecto base + `Product` + primer endpoint | API respondiendo JSON |
| 4 | Java puro — Repository, Service, DTO sin framework | Lógica sin Spring |
| 5 | `@Service` + `@RestController` completo | Capas separadas correctamente |
| 6 | `@Entity` + JPA + `@Repository` | Datos en base de datos real |
| 7 | DTOs + `@Valid` + manejo de errores | API con validaciones |
| 8 | Spring Security + JWT | API protegida |
| 9 | Tests + Swagger/OpenAPI | API documentada y probada |
| 10 | Agentes de IA integrados | Backend potenciado con IA |

> **Para la próxima sesión:** construirás la misma lógica que tiene `ProductController` hoy — pero en Java puro, sin Spring Boot, organizando tú mismo las capas. Cuando vuelvas a Spring Boot en sesión 5, vas a entender exactamente qué está automatizando.

---

## Estado final del proyecto

```
smartinventory/
└── src/main/java/com/cedia/smartinventory/
    ├── SmartInventoryApplication.java
    ├── controller/
    │   └── ProductController.java      ← creado hoy
    ├── model/
    │   └── Product.java                ← creado hoy
    ├── service/                        ← vacío, sesión 5
    ├── repository/                     ← vacío, sesión 6
    ├── dto/                            ← vacío, sesión 7
    └── exception/                      ← vacío, sesión 7
```

**Historial Git al cerrar la sesión:**

```bash
git log --oneline
# m0n1o2p chore: proyecto base SmartInventory generado con Spring Initializr
# i7j8k9l chore: estructura de paquetes por capas
# e4f5g6h feat: entidad Product con Lombok (model layer)
# a1b2c3d feat: ProductController con GET /api/products y GET /api/products/{id}
```
