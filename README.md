[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Sz8OKlS5)
# Stockify: Sistema de Valorización y Control de Inventario para Restaurantes  
### Repositorio oficial del Proyecto Backend  

**Universidad de Ingeniería y Tecnología – UTEC**  
**Curso:** CS2031 – Desarrollo Basado en Plataformas  
**Semestre:** 2025-2

**Integrantes:**  
 
- Espinoza Torres, Hector Miguel
- Medina Reyes, Patrick Ricardo
- Teran Taica, Mauricio Eduardo
- Torres Ccencho, Leo Alexander
- Vizcardo Chavez, Juan Diego

---

## 🔵 Índice

1. [Introducción](#sec1)  

2. [Identificación del Problema o Necesidad](#sec2)  

3. [Descripción de la Solución](#sec3)  

4. [Modelo de Entidades](#sec4)  

5. [Testing y Manejo de Errores](#sec5)  

6. [Medidas de Seguridad Implementadas](#sec6)  

7. [Eventos y Asincronía](#sec7)  

8. [GitHub & Management](#sec8)  


9. [Conclusión](#sec9)  

10. [Apéndices](#sec10)  

11. [Referencias](#sec11)

---

<a id="sec1"></a>
## 1. Introducción

El presente informe documenta el desarrollo del proyecto **Stockify: Sistema de Valorización y Control de Inventario para Restaurantes**, elaborado como parte del curso **CS2031 – Desarrollo Basado en Plataformas** en la Universidad de Ingeniería y Tecnología (UTEC).

Stockify surge como respuesta a la necesidad de **automatizar la gestión de inventarios y la valorización de insumos** dentro del sector gastronómico, particularmente en las **pequeñas y medianas empresas (PYMEs)**, donde los procesos de control de costos y existencias suelen ser manuales o poco precisos.

### 1.1 Contexto
En el sector de restaurantes, la rentabilidad depende directamente de un control eficiente de los costos de los insumos. Sin embargo, la mayoría de negocios no cuenta con un sistema digital que les permita conocer el valor real de su stock ni calcular correctamente el **costo de venta unitario (COGS)**. Esto genera decisiones financieras poco informadas, desperdicio de materia prima y sobrecompra de insumos. Dándole una herramienta de apoyo que optimiza los procesos de control económico, en beneficio de su utilidad.

### 1.2 Objetivos del Proyecto
El objetivo general de Stockify es **desarrollar una API backend robusta y modular** que permita:
- Registrar y valorizar automáticamente entradas y salidas de inventario.  
- Controlar costos mediante métodos contables como **FIFO** y **Promedio Ponderado**.  
- Facilitar la **toma de decisiones operativas y financieras** mediante reportes consolidados.  
- Implementar buenas prácticas de arquitectura y diseño en el desarrollo backend.  

---

<a id="sec2"></a>
## 2. Identificación del Problema o Necesidad

### 2.1 Descripción del Problema
En el entorno gastronómico peruano, los restaurantes suelen manejar inventarios de productos perecibles con rotación diaria. Sin un sistema estructurado, es común que:
- No se conozca el valor actual del inventario.  
- Se calculen precios de venta de forma empírica.  
- No exista trazabilidad de los movimientos de insumos por tienda o fecha.  

De acuerdo con datos del Ministerio de la Producción (PRODUCE, 2024), más del **70% de las pymes del rubro gastronómico** carece de herramientas digitales integradas para la gestión de inventarios. Esto conlleva pérdidas económicas y un margen operativo menor al esperado.

### 2.2 Justificación
El desarrollo de Stockify responde a la necesidad de **digitalizar procesos críticos** en las pymes gastronómicas, ofreciendo:
- Control y trazabilidad de inventarios.  
- Valorización automática de productos y materias primas.  

Con esta solución, se contribuye al fortalecimiento de la gestión empresarial y a la reducción de desperdicios en el sector alimentos, alineándose con los objetivos de transformación digital del país.

---

<a id="sec3"></a>
## 3. Descripción de la Solución

Stockify se implementó bajo una arquitectura **RESTful** con **Spring Boot**, separando la lógica en capas bien definidas:
- **domain/** – Entidades JPA y modelos de negocio.  
- **dto/** – Objetos de transferencia de datos (RequestDTO y NewDTO).  
- **infrastructure/** – Repositorios JPA.  
- **service/** – Lógica de negocio con `ModelMapper`.  
- **controller/** – Endpoints REST.  

### 3.1 Funcionalidades Implementadas
| Módulo | Descripción |
|--------|--------------|
| **Productos** | Registro y gestión de productos e insumos con unidad base y stock mínimo. |
| **Lotes** | Administración de lotes por producto con costo unitario, cantidad y fecha. |
| **Movimientos** | Control de entradas y salidas que afectan el stock. |
| **Recetas** | Asociación entre productos terminados e insumos usados. |
| **Valorizaciones** | Cierre de periodos contables y cálculo automático de costos. |
| **Usuarios** | Módulo básico de gestión de usuarios y autenticación. |

### 3.2 Tecnologías Utilizadas
| Categoría | Tecnología                  |
|------------|-----------------------------|
| Lenguaje | Java 21                     |
| Framework | Spring Boot 3.5.6           |
| ORM | Hibernate / Spring Data JPA |
| Base de datos | PostgreSQL                  |
| Mapper | ModelMapper                 |
| Validación | Jakarta Validation          |
| IDE | IntelliJ IDEA               |
| Control de versiones | Git / GitHub                |
| Testing | JUnit / Postman             |

---

<a id="sec4"></a>
## 4. Modelo de Entidades

El modelo de entidades fue diseñado para reflejar las operaciones reales de un sistema de inventario gastronómico. A continuación se presenta un esquema simplificado en formato ASCII:

```
+-------------------+
|     PRODUCTO      |
+-------------------+
| id_producto (PK)  |
| nombre            |
| unidad_base       |
| stock_minimo      |
+---------+---------+
          |
          | 1..*
          v
+-------------------+
|       LOTE        |
+-------------------+
| id_lote (PK)      |
| id_producto (FK)  |
| costo_unitario    |
| cantidad_actual   |
| fecha_compra      |
+---------+---------+
          |
          | 1..*
          v
+-------------------+
|    MOVIMIENTO     |
+-------------------+
| id_movimiento (PK)|
| tipo (entrada/salida)|
| fecha             |
| cantidad          |
| id_lote (FK)      |
| id_usuario (FK)   |
+-------------------+

+-------------------+
|    RECETA_BASE    |
+-------------------+
| id_receta (PK)    |
| nombre_producto   |
+---------+---------+
          |
          | 1..*
          v
+-------------------+
|  RECETA_DETALLE   |
+-------------------+
| id_detalle (PK)   |
| id_receta (FK)    |
| id_producto (FK)  |
| cantidad_usada    |
+-------------------+

+-------------------+
|   USUARIO         |
+-------------------+
| id_usuario (PK)   |
| nombre            |
| rol               |
| correo            |
+-------------------+

+--------------------------+
|  VALORIZACION_PERIODO    |
+--------------------------+
| id_periodo (PK)          |
| fecha_inicio             |
| fecha_fin                |
| valor_total_inventario   |
+--------------------------+
```

### 4.1 Descripción de Entidades
|Entidad|Atributos|Relaciones|Descripción|
|-------|---------|----------|-----------|
|**Usuario**|- id: Long<br> - nombre: String<br> - apellido: String<br> - email: String<br> - password: String<br> - rol: Rol<br> - telefono: String<br> - sede: String<br> - fechaRegistro: Date<br> - activo: Boolean| - Movimiento: OneToMany <br> - Valorizacion: OneToMany|Entidad destinada a guardar la información de los usuarios que interactúen con la aplicación, a esta se le agrega configuraciones de seguridad.|
|**Producto**|- id: Long<br> - nombre: String<br> - unidadMedida: String<br> - categoria: String<br> - stockMinimo: Double<br> - stockActual: Double<br> - activo: Boolean<br> - fechaCreacion: Date<br> - ultimoActualizado: Date| - Lote: OneToMany<br> - Movimiento: OneToMany<br> - AlertaStock: OneToMany<br> - RecetaDetalle: OneToMany|Esta entidad almacena todo lo relacionado a los productos e insumos que maneja el negocio.|
|**Lote**|- id: Long<br> - codigoLote: String<br> - costoUnitario: Double<br> - costoTotal: Double<br> - cantidadInicial: Double<br> - cantidadDisponible: Double<br> - fechaCompra: Date<br> - fechaVencimiento: Date<br> - estado: Estado| - Producto: ManyToOne<br> - Almacen: ManyToOne|Entidad encargada de registrar la compra de productos, con detalles específicos.|
|**Movimiento**|- id: Long<br> - tipoMovimiento: TipoMovimiento<br> - cantidad: Double<br> - costoUnitario: Double<br> - costoTotal: Double<br> - fechaMovimiento: Date<br> - observacion: String<br> - origen: String<br> - anulado: Boolean|- Producto: ManyToOne<br> - Lote: ManyToOne<br> - Usuario: ManyToOne<br> - Almacen: ManyToOne|Entidad que registra cada una de las entradas y salidas que ocurren al inventario.|
|**Almacen**|- id: Long<br> - nombre: String<br> - ubicacion: String<br> - responsable: String<br> - capacidadMaxima: Double<br> - activo: Boolean<br> - fechaCreacion: Date<br> - ultimoActualizado: Date|- Lote: OneToMany<br> - Movimiento: OneToMany|Entidad encargada guardar la infomación necesaria de los lugares donde físicamente se guardan los productos.|
|**ValorizacionPeriodo**|- id: Long<br> - periodo: String<br> - metodoValorizacion: MetodoValorizacion<br> - valorInventario: Double<br> - costoVentas: Double<br> - observacion: String<br> - fechaValorizacion: Date<br> - cerrado: Boolean|- Usuario: ManyToOne<br>|Entidad que guarda los resultados de los cálculos del valor del inventario en un periodo.|
|**AlertaStock**|- id: Long<br> - mensaje: String<br> - fechaAlerta: Date<br> - atendido: Boolean<br> - prioridad: Prioridad|- Producto: ManyToOne|Entidad detecta y hace un registro de las situaciones críticas que se presentan en el inventario.|
|**RecetaBase**|- id: Long<br> - nombrePlato: String<br> - descripcion: String<br> - porcionesBase: Integer<br> - unidadPoricion: String<br> - fechaCreacion: Date|- RecetaDetalle: OneToMany|Es la entidad que se encarga de guardar las receta base para su preparación en el restaurante|
|**RecetaDetalle**|- id: Long<br> - cantidadNecesaria: Double<br> - unidadMedida: String|- RecetaBase: ManyToOne<br> - Producto: ManyToOne|Entidad que registra los detalles de una preparación, como la cantidad a elaborar con la cantidad de los productos|
|**Reporte**|- id: Long<br> - periodo: String<br> - fechaGeneracion: Date<br> - formato: FormatoReporte<br> - nombreArchivo: String<br> - observaciones: String<br>|- Usuario: ManyToOne|Entidad encargada de generar y registrara reportes que hagan más visibles y profesionales el estado del inventario.|

---

<a id="sec5"></a>
## 5. Testing y Manejo de Errores

### 5.1 Niveles de Testing
Se aplicaron pruebas a nivel:
- **Unitario:** sobre los servicios (`Service`) y repositorios (`Repository`).  
- **Integración:** validación de endpoints CRUD en Postman.  
- **Sistema:** flujo completo de alta de producto, registro de movimiento y valorización.

### 5.2 Resultados
Durante las pruebas se detectaron inconsistencias en la conversión entre DTOs y entidades, las cuales fueron solucionadas mediante ajustes en `ModelMapper`.  
También se validaron excepciones por stock insuficiente y duplicidad de lotes.

### 5.3 Manejo de Errores
Se implementó un **`GlobalExceptionHandler`** con `@ControllerAdvice` para capturar y devolver mensajes estructurados.  
Esto garantiza respuestas estandarizadas (400, 404, 500) con detalles en formato JSON, evitando interrupciones inesperadas.

---

<a id="sec6"></a>
## 6. Medidas de Seguridad Implementadas

- **Validación de entrada:** se aplicaron anotaciones `@NotBlank`, `@Positive`, `@Email`, entre otras, sobre los DTOs.  
- **Seguridad de datos:** los endpoints se diseñaron para soportar posteriormente autenticación JWT.  
- **Prevención de vulnerabilidades:** al utilizar **Spring Data JPA**, se evita exposición a **SQL Injection**.  
- Los objetos `@RequestBody` se validan automáticamente, previniendo inyección de código malicioso.

---

<a id="sec7"></a>
## 7. Eventos y Asincronía

Aunque el proyecto se centra en la API principal, se incluyó el diseño conceptual de **eventos asincrónicos** para futuros módulos (por ejemplo, envío de alertas por correo cuando un producto está por debajo del stock mínimo).  
El uso de asincronía permitirá:
- Reducir latencia en operaciones no críticas.  
- Mejorar la experiencia del usuario final.  
- Escalar el sistema a microservicios.  

---

<a id="sec8"></a>
## 8. GitHub & Management

El equipo utilizó **GitHub Projects** para la asignación y seguimiento de tareas. Sin embargo, se pudo mejorar el flujo de este con el uso de issues o tasks, que la misma herramienta provee, para poder tener un mejor mapeo de que realizaría cada integrante ya que la forma de organización.  
Cada integrante trabajó en una **rama propia**, integrando cambios mediante **pull requests** y revisiones de código.  
Además, se configuraron **GitHub Actions** para compilar el proyecto y ejecutar pruebas automáticas con Maven en cada push.

---

<a id="sec9"></a>
## 9. Conclusión

El desarrollo de **Stockify** permitió comprender de manera práctica la aplicación de los principios de **arquitectura en capas, DTOs, validaciones y modelado de datos**.  
El sistema ofrece una base sólida para escalar hacia una plataforma completa de gestión gastronómica con integración de analítica y proyección de costos.

Se logró cubrir los objetivos planteados:
- Implementar un backend funcional y estructurado.  
- Aplicar buenas prácticas de desarrollo profesional.  
- Resolver una problemática real del entorno empresarial.  

Por otra parte, quedaron puntos sin ser tratados y atribuimos eso a una falta de ordenamiento claro y organización. Por lo que hemos caido en cuenta que nuestro punto débil y la mira a mejorar pra siguiente proyectos es la organización, pero consideramos que el trabajo que hemos realizado cumple con espectativas


---

<a id="sec10"></a>
## 10. Apéndices

**Licencia:**  
Este proyecto se distribuye bajo la licencia **MIT**, permitiendo su uso y modificación con atribución al equipo desarrollador.

---

<a id="sec11"></a>
## 11. Referencias

- Ministerio de la Producción del Perú (2024). *Digitalización y competitividad de las pymes gastronómicas.*  
- Gestión (2023). *Más del 70% de restaurantes pierde control de costos por mala gestión de inventarios.*  
- Instituto Nacional de Estadística e Informática (INEI, 2024). *Estadísticas del sector servicios: rubro restaurantes.*  
- FAO (2022). *Informe sobre reducción del desperdicio alimentario en Latinoamérica.*  
- Documentación oficial de Spring Boot: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)

---
