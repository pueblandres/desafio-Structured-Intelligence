# Import Declaration Service

Microservicio Spring Boot para recibir declaraciones de importacion en XML, validarlas contra XSD, transformarlas con XSLT, persistirlas con JPA y consultarlas por API REST.

## Stack

- Java 17 o superior.
- Spring Boot 3.3.6.
- Maven.
- Spring Web.
- Spring Data JPA.
- H2 in-memory.
- JUnit 5.
- springdoc-openapi / Swagger UI.

## Como levantar el proyecto

```bash
mvn spring-boot:run
```

La aplicacion levanta por defecto en `http://localhost:8080`.

## Como correr tests

```bash
mvn test
```

## URLs utiles

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Swagger UI alternativa: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console: `http://localhost:8080/h2-console`
- JDBC URL H2: `jdbc:h2:mem:declaracionesdb`
- Usuario H2: `sa`
- Password H2: vacio

## Endpoints REST

- `POST /api/declaraciones`
  - Consume: `application/xml`
  - Valida contra `src/main/resources/xml/declaracion.xsd`
  - Transforma con `src/main/resources/xml/declaracion-a-interno.xsl`
  - Persiste la declaracion
  - Respuestas esperadas: `201`, `400`, `409`, `500`

- `GET /api/declaraciones/{numeroDespacho}`
  - Devuelve una declaracion por numero de despacho
  - Respuestas esperadas: `200`, `404`

- `GET /api/declaraciones`
  - Lista declaraciones paginadas
  - Filtros opcionales: `desde=YYYY-MM-DD`, `hasta=YYYY-MM-DD`, `estado=RECIBIDA`
  - Soporta `page`, `size` y `sort` via `Pageable`

## Ejemplos curl

POST:

```bash
curl -X POST http://localhost:8080/api/declaraciones \
  -H "Content-Type: application/xml" \
  --data-binary @src/main/resources/xml/ejemplo-declaracion.xml
```

GET por `numeroDespacho`:

```bash
curl http://localhost:8080/api/declaraciones/26001IM04000123A
```

GET listado:

```bash
curl "http://localhost:8080/api/declaraciones?desde=2026-05-01&hasta=2026-05-31&estado=RECIBIDA&page=0&size=10"
```

## Decisiones tecnicas

- Se usa H2 in-memory para simplificar la ejecucion local y evitar dependencias externas.
- Se separaron validacion XML, transformacion XSLT, parseo XML interno y persistencia en servicios independientes.
- Se calcula `totalFOB` en Java con `BigDecimal` despues de parsear los items. La expresion `sum(items/item/(cantidad * valorUnitario))` mencionada en el desafio no es portable en XSLT 1.0 estandar.
- Se usa DOM para parsear el XML interno porque la estructura es chica y no justifica agregar librerias adicionales.
- Se usa `@RestControllerAdvice` para centralizar el manejo de errores y devolver un formato consistente.
- Se agregaron indices JPA sobre `numeroDespacho`, `fechaEmision` y `estado` para optimizar busquedas y filtros.
- `numeroDespacho` es unico a nivel de aplicacion y base de datos.
- La relacion `Declaracion` -> `ItemDeclaracion` usa `cascade = ALL` y `orphanRemoval = true`.

## Tests incluidos

- Integracion POST + GET con `MockMvc`.
- XML invalido devuelve `400 Bad Request`.
- Declaracion duplicada devuelve `409 Conflict`.
- Listado paginado con filtros.
- Unitarios de `XmlValidationService`.
- Unitarios de `XsltTransformationService`.
- Unitario de calculo de `totalFOB`.

## Que quedo afuera por tiempo

- Docker Compose con Postgres.
- Seguridad/autenticacion.
- CI/CD.
- Observabilidad avanzada.
- Versionado formal de API.
- Auditoria detallada de cambios de estado.

## Uso de IA

- Herramienta: Codex / ChatGPT.
- Uso: generacion inicial de estructura, apoyo en validacion XSD/XSLT, generacion de tests y revision de README.
- Correcciones manuales: calculo de `totalFOB` en Java, manejo especifico de excepciones, ajuste de arquitectura, nombres y alcance.
- Todo el codigo fue revisado y entendido antes de dejarlo como solucion final.
