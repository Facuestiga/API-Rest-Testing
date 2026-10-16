# DummyJSON API Tests

Base de automatización de API para `https://dummyjson.com`, construida con Java 11, Maven,
JUnit 5, REST Assured, JSON Schema Validator y Allure. Incluye 10 escenarios para `/products`,
10 para `/comments` y 10 para `/users`, con casos positivos y negativos.

La capa común utiliza SLF4J para registrar en consola el método y la URL de cada request,
además del código de estado, status line y body completo de cada response.

## Ejecutar las pruebas

```bash
mvn clean test
```

La URL base puede reemplazarse sin modificar código:

```bash
mvn test -Dbase.uri=https://dummyjson.com
```

También se admite la variable de entorno `BASE_URI`.

## Reportes Allure

Las pruebas escriben sus resultados en `target/allure-results` e incluyen como adjuntos las
solicitudes y respuestas HTTP.

```bash
mvn allure:report
```

El reporte HTML se genera en `target/allure-report`. Para abrir un servidor local interactivo,
si Allure CLI está instalado, se puede ejecutar:

```bash
allure serve target/allure-results
```

Para ejecutar la suite y generar el reporte HTML en un único comando:

```bash
mvn clean verify
```

## Estructura

- `src/main/java/.../client`: encapsula las operaciones HTTP de `/products`, `/comments` y `/users`.
- `src/main/java/.../config`: centraliza URL, especificación REST Assured y filtros.
- `src/main/java/.../model`: representa los contratos de respuesta usados por las pruebas.
- `src/main/java/.../base`: contiene la configuración base reutilizable.
- `src/main/java/.../logging`: centraliza el logging de requests y responses.
- `src/main/resources/jsonSchemas`: contiene los contratos JSON de productos, comentarios, usuarios y errores.
- `src/test/java`: contiene únicamente los 30 escenarios funcionales.
