# Import Declaration Service

Microservicio Spring Boot para recibir declaraciones de importación en XML, validarlas contra XSD, transformarlas con XSLT, persistirlas con JPA y consultarlas por API REST.

## Stack

- Java 17+
- Spring Boot 3.3.6
- Maven
- Spring Web
- Spring Data JPA
- H2 in-memory
- JUnit 5
- springdoc-openapi / Swagger UI

## Cómo levantar el proyecto

```bash
mvn spring-boot:run
```

La aplicación levanta por defecto en:

```text
http://localhost:8080
```

## Cómo correr tests

```bash
mvn test
```

## URLs útiles

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Swagger UI alternativa: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console: `http://localhost:8080/h2-console`
- JDBC URL H2: `jdbc:h2:mem:declaracionesdb`
- Usuario H2: `sa`
- Password H2: vacío

## Endpoints REST

### POST `/api/declaraciones`

- Consume: `application/xml`
- Valida contra `src/main/resources/xml/declaracion.xsd`
- Transforma con `src/main/resources/xml/declaracion-a-interno.xsl`
- Persiste la declaración
- Respuestas esperadas: `201`, `400`, `409`, `500`

### GET `/api/declaraciones/{numeroDespacho}`

- Devuelve una declaración por número de despacho.
- Respuestas esperadas: `200`, `404`

### GET `/api/declaraciones`

- Lista declaraciones paginadas.
- Filtros opcionales:
  - `desde=YYYY-MM-DD`
  - `hasta=YYYY-MM-DD`
  - `estado=RECIBIDA`
- Soporta `page`, `size` y `sort` vía `Pageable`.

## Ejemplos curl

### POST

```bash
curl -X POST http://localhost:8080/api/declaraciones \
  -H "Content-Type: application/xml" \
  --data-binary @src/main/resources/xml/ejemplo-declaracion.xml
```

### GET por número de despacho

```bash
curl http://localhost:8080/api/declaraciones/26001IM04000123A
```

### GET listado

```bash
curl "http://localhost:8080/api/declaraciones?desde=2026-05-01&hasta=2026-05-31&estado=RECIBIDA&page=0&size=10"
```

## Decisiones técnicas

- Se usa H2 in-memory para simplificar la ejecución local y evitar dependencias externas.
- Se separaron validación XML, transformación XSLT, parseo XML interno y persistencia en servicios independientes.
- Se calcula `totalFOB` en Java con `BigDecimal` después de parsear los items.
- Se usa DOM para parsear el XML interno porque la estructura es chica y no justifica agregar librerías adicionales.
- Se usa `@RestControllerAdvice` para centralizar el manejo de errores y devolver un formato consistente.
- Se agregaron índices JPA sobre `numeroDespacho`, `fechaEmision` y `estado` para optimizar búsquedas y filtros.
- `numeroDespacho` es único a nivel de aplicación y base de datos.
- La relación `Declaracion -> ItemDeclaracion` usa `cascade = ALL` y `orphanRemoval = true`.

## Tests incluidos

- Integración `POST -> GET` con `MockMvc`.
- XML inválido devuelve `400 Bad Request`.
- Declaración duplicada devuelve `409 Conflict`.
- Listado paginado con filtros.
- Unitarios de `XmlValidationService`.
- Unitarios de `XsltTransformationService`.
- Unitario de cálculo de `totalFOB`.

## Qué quedó afuera por tiempo

- Docker Compose con Postgres.
- Seguridad/autenticación.
- CI/CD.
- Observabilidad avanzada.
- Versionado formal de API.
- Auditoría detallada de cambios de estado.

## Uso de IA

- Herramienta: Codex / ChatGPT.
- Uso: generación inicial de estructura, apoyo en validación XSD/XSLT, generación de tests y revisión de README.
- Correcciones manuales: cálculo de `totalFOB` en Java, manejo específico de excepciones, ajuste de arquitectura, nombres y alcance.
- Todo el código fue revisado y entendido antes de dejarlo como solución final.

## Nota para pruebas en Swagger

Para probar el endpoint POST desde Swagger UI, utilizar el content-type `application/xml` y pegar el contenido completo de uno de los XML de ejemplo provistos en el proyecto.
