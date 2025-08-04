# Exchange Rate Comparison Service

Este proyecto proporciona un microservicio para comparar tasas de cambio de múltiples APIs y seleccionar la mejor oferta para clientes bancarios. El servicio consulta tres APIs diferentes de tasas de cambio en paralelo, maneja fallos con gracia y retorna la mejor tasa de conversión disponible.

## 🎯 Descripción del Proyecto

Este servicio está diseñado con patrones de resiliencia para funcionar incluso si una o más APIs no están disponibles, garantizando siempre la mejor experiencia para el usuario final.

## ✨ Características Principales

- **Integración Multi-Proveedor**: Se conecta a 3 APIs diferentes de tasas de cambio con formatos de respuesta distintos:
  - **API1**: JSON simple
  - **API2**: XML
  - **API3**: JSON anidado
- **Arquitectura Resiliente**:
  - Circuit breakers
  - Reintentos automáticos
  - Manejo de timeouts
  - Mecanismos de fallback
- **Optimizado para Rendimiento**:
  - Llamadas paralelas a APIs
  - Programación reactiva
  - Baja huella de memoria
- **Listo para Producción**:
  - Health checks
  - Métricas
  - Soporte para contenedores
  - Integración de seguridad

## 🏗️ Arquitectura

### Proveedores de API

| Proveedor | Formato | Input | Output |
|-----------|---------|-------|--------|
| API1 | JSON | `{sourceCurrency, targetCurrency, amount}` | `{rate}` |
| API2 | XML | `<exchangeRequest><sourceCurrency/><targetCurrency/><amount/></exchangeRequest>` | `<exchangeResponse><result/></exchangeResponse>` |
| API3 | JSON Anidado | `{sourceCurrency, targetCurrency, amount}` | `{data: {total}}` |

### Componentes Principales

- **ExchangeRateResource**: Endpoint REST para comparación de tasas
- **ExchangeRateService**: Orquesta las llamadas a los proveedores
- **Api[1-3]Provider**: Maneja cada integración de API
- **Fault Tolerance**: Circuit breakers, reintentos y timeouts

## 🚀 Tecnologías

- **OpenJDK 21**: Plataforma de desarrollo para la creación de aplicaciones en Java
- **Quarkus 3.8.0.Final**: Framework Java nativo para la nube, optimizado para contenedores
- **Maven 3.9.8**: Herramienta de gestión y construcción de proyectos
- **SmallRye Mutiny**: Biblioteca para programación reactiva
- **MicroProfile Fault Tolerance**: Patrones de resiliencia
- **JAXB**: Para procesamiento XML
- **JaCoCo**: Herramienta para medir la cobertura del código

## 📋 Requisitos para el Despliegue

### Tecnologías Necesarias

- **Docker**: Requerido para crear y gestionar contenedores de las APIs mock
  - **Versión recomendada**: Docker Desktop última versión
  - **Nota**: Docker debe estar instalado y en ejecución antes de iniciar el despliegue

### Variables de Ambiente

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `quarkus.rest-client.api1-client.url` | `http://localhost:8081` | URL del servicio API1 mock |
| `quarkus.rest-client.api2-client.url` | `http://localhost:8082` | URL del servicio API2 mock |
| `quarkus.rest-client.api3-client.url` | `http://localhost:8083` | URL del servicio API3 mock |

## 🚀 Inicio Rápido

### Comandos de Ejecución

```bash
# Clonar repositorio
git clone <your-repo-url>
cd exchange-rate-comparison-service

# Levantar servicios mock con Docker Compose
docker-compose up --build

# En otra terminal, compilar y empaquetar
mvn clean package

# O para instalar dependencias
mvn clean install

# Ejecutar en modo desarrollo
mvn quarkus:dev
```

### Sin Maven instalado
```bash
./mvnw clean package
./mvnw quarkus:dev
```

## 📡 Endpoints

### Comparar Tasas de Cambio

> **Método: POST**

> **URL:**
```
http://localhost:8080/api/v1/exchange-rate
```

> **Request ejemplo:**
```json
{
    "sourceCurrency": "USD",
    "targetCurrency": "EUR", 
    "amount": 100.00
}
```

> **Response (éxito):**
```json
{
    "provider": "API3",
    "rate": 0.87,
    "convertedAmount": 87.00,
    "responseTimeMs": 1140
}
```

### Respuestas de Error

**Campo requerido faltante:**
```json
{
    "message": "El campo 'sourceCurrency' es requerido"
}
```

**Ningún proveedor disponible:**
```json
{
    "header": {
        "responseCode": 404,
        "responseMessage": "No hay tasas válidas disponibles de ningún proveedor"
    },
    "body": null
}
```

**Error interno del servicio:**
```json
{
    "header": {
        "responseCode": 500,
        "responseMessage": "Error interno del servidor"
    },
    "body": null
}
```

## 🏥 Endpoints de Salud y Monitoreo

### Health Check

> **URL Liveness**
```
http://localhost:8080/api/q/health/live
```

> **URL Readiness**
```
http://localhost:8080/api/q/health/ready
```

### Métricas

> **URL Métricas Prometheus**
```
http://localhost:8080/q/metrics
```

### Documentación

> **URL OpenAPI**
```
http://localhost:8080/api/q/openapi?format=json
```

> **URL Swagger UI**
```
http://localhost:8080/api/q/swagger-ui
```

## 🧪 Pruebas y Cobertura

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con cobertura
mvn clean verify
```

### Ver Reporte de Cobertura

El informe de cobertura estará disponible en:
```
target/site/jacoco/index.html
```

**Objetivo de Cobertura**: >80% de líneas cubiertas

## 📦 Empaquetado y Despliegue

### Aplicación JAR

```bash
# Crear JAR ejecutable
mvn package

# Ejecutar JAR
java -jar target/quarkus-app/quarkus-run.jar
```

### Über-JAR

```bash
# Crear über-jar
mvn package -Dquarkus.package.type=uber-jar

# Ejecutar über-jar
java -jar target/*-runner.jar
```

### Ejecutable Nativo

```bash
# Con GraalVM instalado
mvn package -Dnative

# Sin GraalVM (usando contenedor)
mvn package -Dnative -Dquarkus.native.container-build=true

# Ejecutar nativo
./target/exchange-rate-service-1.0.0-runner
```

## 🐳 Docker

### Dockerfile incluido

```bash
# Construir imagen Docker
docker build -t exchange-rate-service .

# Ejecutar contenedor
docker run -p 8080:8080 exchange-rate-service
```

### Docker Compose

```bash
# Levantar toda la infraestructura (app + mocks)
docker-compose up --build

# Solo servicios mock
docker-compose up mock-api1 mock-api2 mock-api3
```

## ⚙️ Configuración

### Fault Tolerance

```properties
# Timeouts
api1.timeout=2s
api2.timeout=3s  
api3.timeout=2s

# Reintentos
api1.retry.maxRetries=2
api2.retry.maxRetries=3
api3.retry.maxRetries=2

# Circuit Breaker (API1)
api1.circuitBreaker.requestVolumeThreshold=4
api1.circuitBreaker.failureRatio=0.5
```

### Logging

```properties
# Nivel de logging
quarkus.log.category."com.currency".level=INFO
%dev.quarkus.log.category."com.currency".level=DEBUG

# Formato de console
quarkus.log.console.format=%d{dd/MM/yyyy HH:mm:ss} %-5p [%c{1}] %s%e%n
```

## 🔒 Seguridad

El servicio incluye configuración para integración con Keycloak/OIDC:

```properties
# OIDC Configuration (opcional)
quarkus.oidc.client-id=exchange-rate-service
quarkus.oidc.credentials.secret=${OIDC_SECRET}
quarkus.oidc.auth-server-url=${OIDC_SERVER_URL}
```

## 🛠️ Desarrollo

### Ejecutar en Modo Dev

```bash
mvn quarkus:dev
```

> **Nota**: Quarkus proporciona una UI de desarrollo disponible en http://localhost:8080/q/dev/

### Live Coding

El modo dev soporta hot reload automático de cambios en código Java.

### Configuración IDE

Recomendado usar:
- IntelliJ IDEA con plugin de Quarkus
- VS Code con extensión Quarkus
- Eclipse con Quarkus Tools

## 📊 Monitoreo y Observabilidad

### Métricas Disponibles

- Tiempo de respuesta por proveedor
- Tasa de éxito/fallo por API
- Contadores de circuit breaker
- Métricas JVM y sistema

### Health Checks

- **Liveness**: Verifica si la aplicación está corriendo
- **Readiness**: Verifica si la aplicación puede recibir tráfico
- **Custom**: Verifica conectividad con APIs externas

## 🐛 Troubleshooting

### Problemas Comunes

1. **APIs mock no responden**:
   ```bash
   # Verificar estado de contenedores
   docker-compose ps
   
   # Ver logs de contenedores
   docker-compose logs mock-api1
   ```

2. **Error de JAXB marshalling**:
   - Verificar que las clases DTO tengan `@XmlRootElement`
   - Revisar logs para detalles del error XML

3. **Circuit breaker abierto**:
   - Verificar métricas en `/q/metrics`
   - Esperar tiempo de recovery o reiniciar aplicación

### Logs Útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f exchange-rate-service

# Filtrar logs por nivel
grep "ERROR" logs/application.log
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### Estándares de Código

- Seguir convenciones de Java
- Escribir pruebas unitarias
- Mantener cobertura >80%
- Documentar APIs con OpenAPI

## 📚 Guías Relacionadas

- [RESTEasy Reactive](https://quarkus.io/guides/resteasy-reactive): Implementación de Jakarta REST
- [SmallRye Mutiny](https://quarkus.io/guides/mutiny-primer): Programación reactiva en Java
- [SmallRye Health](https://quarkus.io/guides/smallrye-health): Monitoreo de salud del servicio
- [SmallRye OpenAPI](https://quarkus.io/guides/openapi-swaggerui): Documentación de APIs REST
- [Jacoco Code Coverage](https://quarkus.io/guides/tests-with-coverage): Cobertura de código
- [MicroProfile Fault Tolerance](https://quarkus.io/guides/microprofile-fault-tolerance): Tolerancia a fallos

## 👨‍💻 Autor

**Edwin Enrique Vasquez Cruz** - *Desarrollador* - vasqeuzedwin954@ejemplo.com

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia Apache 2.0 - ver el archivo [LICENSE](LICENSE) para detalles.

---

