# 🏋️ Sistema de Gestión de Gimnasio - Backend

Sistema backend completo para la gestión integral de un gimnasio, desarrollado con **Spring Boot** siguiendo principios de **arquitectura hexagonal** (puertos y adaptadores).

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Características Principales](#características-principales)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Documentación de API](#documentación-de-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Módulos del Sistema](#módulos-del-sistema)

---

## 🎯 Descripción General

Backend REST API para la administración completa de un gimnasio que incluye gestión de usuarios, planes, membresías, asistencias, pagos y reportes. El sistema implementa autenticación JWT y proporciona 156 endpoints organizados en 14 módulos funcionales.

---

## ✨ Características Principales

- ✅ **Autenticación y Autorización** con JWT
- ✅ **Gestión de Usuarios** con roles (Administrador, Recepcionista, Cliente)
- ✅ **Gestión de Planes y Membresías** con estados y validaciones
- ✅ **Control de Asistencias** con registro de entradas/salidas
- ✅ **Sistema de Pagos** con códigos de pago y QR
- ✅ **Historial de Auditoría** para pagos y membresías
- ✅ **Reportes y Estadísticas** completos
- ✅ **Exportación de Reportes** a PDF y Excel
- ✅ **Paginación** en todos los listados
- ✅ **Validaciones** robustas
- ✅ **Manejo de Excepciones** centralizado

---

## 🛠️ Tecnologías Utilizadas

- **Java 17+**
- **Spring Boot 3.x**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Base de Datos:**
  - PostgreSQL (Producción)
  - H2 (Desarrollo/Testing)
- **Seguridad:**
  - JWT (JSON Web Tokens)
  - BCrypt para encriptación de contraseñas
- **Librerías adicionales:**
  - Lombok
  - MapStruct (Mappers)
  - ZXing (Generación de QR)
  - Apache POI (Exportación Excel)
  - iText (Exportación PDF)
- **Build Tool:** Maven
- **Contenedores:** Docker

---

## 🏗️ Arquitectura

El proyecto sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** organizada en capas:

```
📦 Módulo
 ┣ 📂 Application      # DTOs, Mappers
 ┣ 📂 Domain          # Entidades, Casos de Uso, Excepciones
 ┗ 📂 Infrastructure  # Controladores, Repositorios, Configuración
```

### Principios aplicados:
- **DDD** (Domain-Driven Design)
- **SOLID**
- **Clean Architecture**
- **Separation of Concerns**

---

## 📋 Requisitos

- **JDK 17** o superior
- **Maven 3.8+**
- **PostgreSQL 17+** (o usar H2 para desarrollo)
- **Docker** (opcional)

---

## 🚀 Instalación

### Opción 1: Local

```bash
# Clonar el repositorio
git clone <repository-url>
cd backend

# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### Opción 2: Docker

```bash
# Construir la imagen
docker build -t gym-backend .

# Ejecutar el contenedor
docker run -p 8080:8080 gym-backend
```

---

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/gym_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# JWT
jwt.secret=tu_secreto_jwt
jwt.expiration=86400000

# Puerto del servidor
server.port=8080
```

---

## 📚 Documentación de API

La documentación completa de todos los endpoints está disponible en:

📄 **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)**

### Resumen rápido:

- **Total de Endpoints:** 156
- **Base URL:** `http://localhost:8080/api`
- **Autenticación:** JWT Bearer Token

#### Módulos principales:

| Módulo | Base URL | Endpoints |
|--------|----------|-----------|
| Autenticación | `/api/auth` | 4 |
| Usuarios | `/api/usuarios` | 15 |
| Planes | `/api/planes` | 17 |
| Membresías | `/api/membresias` | 19 |
| Asistencias | `/api/asistencias` | 14 |
| Pagos | `/api/pagos` | 11 |
| Códigos de Pago | `/api/payment-codes` | 15 |
| Reportes | `/api/reportes` | 18 |
| QR | `/api/qr` | 7 |
| Sistema | `/api/health`, `/api/info` | 2 |

### Ejemplo de uso:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"emailOrDni": "user@example.com", "password": "password123"}'

# Obtener usuarios (requiere autenticación)
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer <tu-token>"
```

---

## 📁 Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/gym/backend/
│   │   │   ├── Asistencias/
│   │   │   ├── Auth/
│   │   │   ├── HistorialMembresias/
│   │   │   ├── HistorialPagos/
│   │   │   ├── Membresias/
│   │   │   ├── Pago/
│   │   │   ├── PaymentCode/
│   │   │   ├── Planes/
│   │   │   ├── Qr/
│   │   │   ├── Reportes/
│   │   │   ├── Shared/
│   │   │   └── Usuarios/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── Dockerfile
├── README.md
└── API_DOCUMENTATION.md
```

---

## 🧩 Módulos del Sistema

### 1. **Autenticación (Auth)**
- Login y registro de usuarios
- Validación de tokens JWT
- Gestión de sesiones

### 2. **Usuarios**
- CRUD completo de usuarios
- Gestión de roles (ADMINISTRADOR, RECEPCIONISTA, CLIENTE)
- Activación/desactivación de cuentas
- Filtros por rol, género, estado

### 3. **Planes**
- Gestión de planes de membresía
- Categorización y precios
- Planes destacados
- Estadísticas de contratación

### 4. **Membresías**
- Asignación de planes a usuarios
- Control de estados (ACTIVA, SUSPENDIDA, VENCIDA, CANCELADA)
- Extensión y renovación
- Verificación de acceso

### 5. **Asistencias**
- Registro de entradas y salidas
- Historial por usuario
- Estadísticas diarias, mensuales
- Control de asistencias por hora

### 6. **Pagos**
- Gestión de pagos y transacciones
- Métodos de pago
- Estados (PENDIENTE, CONFIRMADO, RECHAZADO, CANCELADO)
- Reportes de ingresos

### 7. **Códigos de Pago (Payment Codes)**
- Generación de códigos únicos
- Validación y expiración
- Estados (ACTIVO, USADO, EXPIRADO, CANCELADO)
- Integración con sistema de pagos

### 8. **Códigos QR**
- Generación de códigos QR para pagos
- Múltiples formatos (PNG, Data URI)
- Tamaños personalizados
- Validación de QR

### 9. **Historiales**
- **Historial de Pagos:** Auditoría de cambios en pagos
- **Historial de Membresías:** Trazabilidad de acciones en membresías
- Timeline completa de eventos

### 10. **Reportes**
- Ingresos mensuales
- Asistencias diarias
- Top planes más contratados
- Usuarios activos
- Membresías por estado
- Dashboard consolidado

### 11. **Exportación**
- Exportación de reportes a PDF
- Exportación de reportes a Excel
- Reportes personalizados

### 12. **Sistema**
- Health checks
- Información del sistema
- Monitoreo

---

## 🔒 Seguridad

- **Autenticación:** JWT (JSON Web Tokens)
- **Encriptación:** BCrypt para contraseñas
- **Autorización:** Basada en roles
- **Validación:** Bean Validation (Jakarta Validation)
- **CORS:** Configurado para desarrollo

---

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

---

## 📝 Convenciones de Código

- **Nomenclatura:** camelCase para variables y métodos, PascalCase para clases
- **Comentarios:** JavaDoc para clases y métodos públicos
- **Validaciones:** Usar anotaciones de Jakarta Validation
- **DTOs:** Separados de entidades de dominio
- **Excepciones:** Custom exceptions por módulo

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📞 Contacto

Para más información sobre el proyecto, consulta la [Documentación de API](./API_DOCUMENTATION.md).

---

## 📄 Licencia

Este proyecto es privado y confidencial.

---

**Desarrollado con ❤️ para la gestión eficiente de gimnasios**
