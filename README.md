# Import Declaration Service

Microservicio Spring Boot para recibir declaraciones de importacion en XML, validarlas contra XSD, transformarlas con XSLT, persistirlas con JPA y consultarlas por API REST.

## Stack

- Java 17
- Spring Boot 3.3.6
- Maven
- Spring Web
- Spring Data JPA
- H2 in-memory
- JUnit 5
- springdoc-openapi / Swagger UI

## Como levantar el proyecto

```bash
mvn spring-boot:run
```

La aplicacion levanta por defecto en:

```text
http://localhost:8080
```

## Como correr tests

```bash
mvn test
```

## URLs utiles

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console: `http://localhost:8080/h2-console`
- JDBC URL H2: `jdbc:h2:mem:declaracionesdb`
- Usuario H2: `sa`
- Password H2: vacio

## Endpoints REST

### POST `/api/declaraciones`

- Consume `application/xml`.
- Valida contra `src/main/resources/xml/declaracion.xsd`.
- Transforma con `src/main/resources/xml/declaracion-a-interno.xsl`.
- Persiste la declaracion y devuelve `201 Created` con el id interno generado.
- Devuelve `400` si el XML no cumple el XSD.
- Devuelve `409` si ya existe el `numeroDespacho`.

### GET `/api/declaraciones/{numeroDespacho}`

- Devuelve una declaracion persistida en JSON.
- Devuelve `404` si no existe.

### GET `/api/declaraciones`

- Lista declaraciones paginadas.
- Filtros opcionales:
  - `desde=YYYY-MM-DD`
  - `hasta=YYYY-MM-DD`
  - `estado=RECIBIDA`
- Soporta `page`, `size` y `sort` via `Pageable`.

## Ejemplos curl

```bash
curl -X POST http://localhost:8080/api/declaraciones \
  -H "Content-Type: application/xml" \
  --data-binary @src/main/resources/xml/ejemplo-declaracion.xml
```

```bash
curl http://localhost:8080/api/declaraciones/26001IM04000123A
```

```bash
curl "http://localhost:8080/api/declaraciones?desde=2026-05-01&hasta=2026-05-31&estado=RECIBIDA&page=0&size=10"
```

## Decisiones tecnicas

- Use H2 in-memory para que el proyecto se pueda levantar con un solo comando, sin depender de Docker ni de una base externa.
- Separe validacion XML, transformacion XSLT, parseo del XML interno, mapeo y persistencia en clases distintas para que cada pieza sea testeable y facil de explicar.
- El `totalFOB` se genera en el XSLT y se parsea desde el modelo interno, alineado con el anexo del ejercicio. El XSLT usa una plantilla recursiva compatible con XSLT 1.0 porque el motor por defecto de Java no soporta XPath 2.0.
- Use `BigDecimal` para persistir importes y cantidades, evitando errores de precision de `double` en el modelo Java.
- Agregue indices sobre `numeroDespacho`, `fechaEmision` y `estado`, porque son los campos usados para unicidad y filtros de listado.
- Centralice errores con `@RestControllerAdvice` para devolver status codes correctos y mensajes consistentes.
- La relacion `Declaracion -> ItemDeclaracion` usa `cascade = ALL` y `orphanRemoval = true` para persistir la declaracion completa como agregado.

## Tests incluidos

- Integracion `POST -> GET` con `MockMvc`.
- XML invalido devuelve `400 Bad Request`.
- Declaracion duplicada devuelve `409 Conflict`.
- Listado paginado con filtros.
- Unitarios de `XmlValidationService`.
- Unitarios de `XsltTransformationService`.
- Unitario de parseo del XML interno incluyendo `totalFOB`.

## Que quedo afuera por tiempo

- Docker Compose con Postgres.
- Autenticacion/autorizacion.
- CI/CD.
- Observabilidad avanzada.
- Versionado formal de API.
- Auditoria detallada de cambios de estado.

## Uso de IA

Use Codex / ChatGPT como asistente de implementacion y revision.

Partes donde use IA:

- Estructura inicial de capas Spring Boot: controller, service, mapper, repository, DTOs y entidades.
- Validacion XSD y transformacion XSLT usando APIs seguras de Java.
- Generacion y revision de tests con MockMvc y JUnit 5.
- Revision del README contra la consigna.

Prompts criticos usados o equivalentes:

- "Construir un microservicio Spring Boot 3 con Java 17 que reciba XML, valide contra XSD, transforme con XSLT, persista con JPA y exponga endpoints REST."
- "Revisar si el manejo de errores devuelve 400 para XML invalido, 409 para duplicado y 404 para no encontrado."
- "Comparar la implementacion contra esta consigna tecnica y detectar brechas antes de entregar."
- "Alinear el calculo de totalFOB con el XSLT provisto, manteniendo compatibilidad con XSLT 1.0 de Java."

Cosas que tuve que corregir o decidir manualmente:

- El calculo de `totalFOB` no debia quedar solo en Java si el anexo indicaba que el modelo interno lo generaba con XSLT.
- La expresion `sum(items/item/(cantidad * valorUnitario))` del enunciado no es compatible con el motor XSLT 1.0 por defecto de Java, por eso use una plantilla recursiva.
- Ajuste los errores para que las excepciones de dominio no terminen como `500`.
- Revise los ejemplos XML para dejarlos como recursos de prueba/documentacion, no como parte del flujo productivo.

Todo el codigo entregado fue revisado y puedo explicarlo linea por linea.

## Nota para Swagger

Para probar el endpoint POST desde Swagger UI, usar `application/xml` y pegar el contenido completo de `src/main/resources/xml/ejemplo-declaracion.xml`.
