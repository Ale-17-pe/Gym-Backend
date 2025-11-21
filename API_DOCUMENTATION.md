# 📋 Documentación de Endpoints - Backend Gym

## Resumen del Sistema

El backend del sistema de gestión de gimnasio está construido con **Spring Boot** y sigue una arquitectura por módulos. A continuación se detallan todos los endpoints disponibles organizados por módulos funcionales.

**Total de Controladores:** 14  
**Base URL:** `/api`

---

## 📑 Índice de Módulos

1. [Autenticación](#1-autenticación)
2. [Usuarios](#2-usuarios)
3. [Planes](#3-planes)
4. [Membresías](#4-membresías)
5. [Asistencias](#5-asistencias)
6. [Pagos](#6-pagos)
7. [Flujo de Pagos](#7-flujo-de-pagos)
8. [Códigos de Pago](#8-códigos-de-pago)
9. [Historial de Pagos](#9-historial-de-pagos)
10. [Historial de Membresías](#10-historial-de-membresías)
11. [Códigos QR](#11-códigos-qr)
12. [Reportes](#12-reportes)
13. [Exportación de Reportes](#13-exportación-de-reportes)
14. [Sistema](#14-sistema)

---

## 1. Autenticación

**Base URL:** `/api/auth`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Iniciar sesión con email/DNI y contraseña |
| POST | `/register` | Registrar un nuevo usuario |
| POST | `/validate` | Validar token JWT (Header: `Authorization: Bearer <token>`) |
| POST | `/refresh` | Refrescar token (No implementado) |

---

## 2. Usuarios

**Base URL:** `/api/usuarios`

### Operaciones CRUD

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/` | Crear un nuevo usuario |
| GET | `/` | Listar todos los usuarios |
| GET | `/paginated?page={page}&size={size}` | Listar usuarios con paginación |
| GET | `/{id}` | Obtener usuario por ID |
| PUT | `/{id}` | Actualizar usuario |
| DELETE | `/{id}` | Eliminar usuario |

### Filtros y Búsquedas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/activos` | Listar usuarios activos |
| GET | `/inactivos` | Listar usuarios inactivos |
| GET | `/email/{email}` | Obtener usuario por email |
| GET | `/dni/{dni}` | Obtener usuario por DNI |
| GET | `/rol/{rol}` | Listar usuarios por rol (ADMINISTRADOR, RECEPCIONISTA, CLIENTE) |
| GET | `/genero/{genero}` | Listar usuarios por género |

### Acciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| PATCH | `/{id}/desactivar` | Desactivar usuario |
| PATCH | `/{id}/activar` | Activar usuario |
| GET | `/{id}/verificar-activo` | Verificar si el usuario está activo |

---

## 3. Planes

**Base URL:** `/api/planes`

### Operaciones CRUD

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/` | Crear un nuevo plan |
| GET | `/` | Listar todos los planes |
| GET | `/paginated?page={page}&size={size}` | Listar planes con paginación |
| GET | `/{id}` | Obtener plan por ID |
| PUT | `/{id}` | Actualizar plan |
| DELETE | `/{id}` | Eliminar plan |

### Filtros y Búsquedas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/activos` | Listar planes activos |
| GET | `/activos/paginated?page={page}&size={size}` | Listar planes activos con paginación |
| GET | `/inactivos` | Listar planes inactivos |
| GET | `/categoria/{categoria}` | Buscar planes por categoría |
| GET | `/destacados` | Buscar planes destacados |
| GET | `/precio/max/{precioMax}` | Buscar planes con precio menor o igual |
| GET | `/precio/rango?precioMin={min}&precioMax={max}&page={page}&size={size}` | Buscar planes por rango de precio |

### Acciones y Estadísticas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| PATCH | `/{id}/desactivar` | Desactivar plan |
| PATCH | `/{id}/activar` | Activar plan |
| PATCH | `/{id}/incrementar-contrataciones` | Incrementar contador de contrataciones |
| PATCH | `/{id}/rating?rating={rating}` | Actualizar rating del plan |
| GET | `/stats` | Obtener estadísticas de planes |

---

## 4. Membresías

**Base URL:** `/api/membresias`

### Operaciones CRUD

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/` | Crear una nueva membresía |
| GET | `/` | Listar todas las membresías |
| GET | `/paginated?page={page}&size={size}` | Listar membresías con paginación |
| GET | `/{id}` | Obtener membresía por ID |

### Filtros y Búsquedas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuario/{usuarioId}` | Listar membresías por usuario |
| GET | `/usuario/{usuarioId}/paginated?page={page}&size={size}` | Listar membresías por usuario con paginación |
| GET | `/activas` | Listar membresías activas |
| GET | `/activas/paginated?page={page}&size={size}` | Listar membresías activas con paginación |
| GET | `/por-vencer` | Listar membresías próximas a vencer |
| GET | `/vencidas` | Listar membresías vencidas |
| GET | `/activa/{usuarioId}` | Obtener membresía activa de un usuario |

### Acciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/extender?dias={dias}` | Extender membresía por días |
| POST | `/{id}/suspender` | Suspender membresía |
| POST | `/{id}/reactivar` | Reactivar membresía |
| POST | `/{id}/cancelar` | Cancelar membresía |
| GET | `/verificar-acceso/{usuarioId}` | Verificar si el usuario tiene acceso |

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/estadisticas` | Obtener estadísticas de membresías |
| GET | `/reporte/fechas?fechaInicio={inicio}&fechaFin={fin}` | Buscar membresías por rango de fechas |

---

## 5. Asistencias

**Base URL:** `/api/asistencias`

### Registro

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/entrada/{usuarioId}` | Registrar entrada de usuario |
| POST | `/salida/{usuarioId}` | Registrar salida de usuario |
| POST | `/{id}/cancelar` | Cancelar asistencia |

### Consultas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todas las asistencias |
| GET | `/paginated?page={page}&size={size}` | Listar asistencias con paginación |
| GET | `/usuario/{usuarioId}` | Listar asistencias por usuario |
| GET | `/usuario/{usuarioId}/paginated?page={page}&size={size}` | Listar asistencias por usuario con paginación |
| GET | `/fecha/{fecha}` | Listar asistencias por fecha (formato: YYYY-MM-DD) |
| GET | `/rango-fechas?inicio={inicio}&fin={fin}` | Listar asistencias por rango de fechas |
| GET | `/estado/{usuarioId}` | Obtener estado actual de asistencia del usuario |
| GET | `/verificar-acceso/{usuarioId}` | Verificar si el usuario puede acceder |

### Estadísticas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/estadisticas` | Obtener estadísticas generales de asistencias |
| GET | `/estadisticas/mes?año={año}&mes={mes}` | Obtener estadísticas por mes |

---

## 6. Pagos

**Base URL:** `/api/pagos`

### Consultas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todos los pagos |
| GET | `/paginated?page={page}&size={size}` | Listar pagos con paginación |
| GET | `/{id}` | Obtener pago por ID |
| GET | `/usuario/{usuarioId}` | Listar pagos por usuario |
| GET | `/usuario/{usuarioId}/paginated?page={page}&size={size}` | Listar pagos por usuario con paginación |
| GET | `/pendientes` | Listar pagos pendientes |

### Acciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/rechazar` | Rechazar pago |
| POST | `/{id}/cancelar` | Cancelar pago |

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reporte/ingresos?fechaInicio={inicio}&fechaFin={fin}` | Obtener ingresos totales por rango de fechas |
| GET | `/estadisticas/mensual?año={año}&mes={mes}` | Obtener estadísticas mensuales |

---

## 7. Flujo de Pagos

**Base URL:** `/api/pagos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/iniciar` | Iniciar proceso de pago (genera código de pago) |
| POST | `/confirmar/{codigoPago}` | Confirmar pago con código de pago |

---

## 8. Códigos de Pago

**Base URL:** `/api/payment-codes`

### Operaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/generar/{pagoId}` | Generar código de pago para un pago |
| GET | `/validar/{codigo}` | Validar código de pago |
| GET | `/pago/{pagoId}` | Obtener código de pago por ID de pago |
| GET | `/{id}` | Obtener código de pago por ID |

### Consultas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todos los códigos |
| GET | `/paginated?page={page}&size={size}` | Listar códigos con paginación |
| GET | `/estado/{estado}` | Listar códigos por estado (ACTIVO, USADO, EXPIRADO, CANCELADO) |
| GET | `/activos` | Listar códigos activos |
| GET | `/expirados` | Listar códigos expirados |
| GET | `/por-vencer` | Listar códigos próximos a vencer |

### Acciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/usado` | Marcar código como usado |
| POST | `/{id}/cancelar` | Cancelar código |
| POST | `/{codigo}/procesar-pago` | Procesar pago con código |

### Estadísticas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/estadisticas` | Obtener estadísticas de códigos de pago |

---

## 9. Historial de Pagos

**Base URL:** `/api/historial-pagos`

### Operaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/` | Registrar cambio en historial |
| GET | `/` | Listar todo el historial |
| GET | `/paginated?page={page}&size={size}` | Listar historial con paginación |

### Consultas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuario/{usuarioId}` | Listar historial por usuario |
| GET | `/usuario/{usuarioId}/paginated?page={page}&size={size}` | Listar historial por usuario con paginación |
| GET | `/pago/{pagoId}` | Listar historial por pago |
| GET | `/estado/{estado}` | Listar historial por estado |
| GET | `/rango-fechas?inicio={inicio}&fin={fin}` | Listar historial por rango de fechas |
| GET | `/ultimo-cambio/{pagoId}` | Obtener último cambio de un pago |
| GET | `/recientes?limite={limite}` | Obtener cambios recientes (default: 10) |
| GET | `/timeline/{pagoId}` | Obtener timeline completa de un pago |

### Estadísticas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/estadisticas` | Obtener estadísticas del historial |
| GET | `/estadisticas/mes?año={año}&mes={mes}` | Obtener estadísticas por mes |

---

## 10. Historial de Membresías

**Base URL:** `/api/historial-membresias`

### Operaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/` | Registrar cambio en historial |
| GET | `/` | Listar todo el historial |
| GET | `/paginated?page={page}&size={size}` | Listar historial con paginación |

### Consultas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuario/{usuarioId}` | Listar historial por usuario |
| GET | `/usuario/{usuarioId}/paginated?page={page}&size={size}` | Listar historial por usuario con paginación |
| GET | `/membresia/{membresiaId}` | Listar historial por membresía |
| GET | `/accion/{accion}` | Listar historial por acción |
| GET | `/rango-fechas?inicio={inicio}&fin={fin}` | Listar historial por rango de fechas |
| GET | `/ultimo-cambio/{membresiaId}` | Obtener último cambio de una membresía |
| GET | `/recientes?limite={limite}` | Obtener cambios recientes (default: 10) |
| GET | `/timeline/{membresiaId}` | Obtener timeline completa de una membresía |

### Estadísticas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/estadisticas` | Obtener estadísticas del historial |
| GET | `/estadisticas/mes?año={año}&mes={mes}` | Obtener estadísticas por mes |

---

## 11. Códigos QR

**Base URL:** `/api/qr`

### Generación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/{codigoPago}` | Generar QR para código de pago (tamaño por defecto) |
| GET | `/{codigoPago}/custom?tamaño={tamaño}` | Generar QR con tamaño personalizado |
| GET | `/{codigoPago}/rectangular?ancho={ancho}&alto={alto}` | Generar QR rectangular |
| GET | `/{codigoPago}/bytes` | Generar QR como bytes (imagen PNG) |
| GET | `/{codigoPago}/data-uri` | Generar QR como Data URI |

### Validación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/validar` | Validar contenido de QR |

### Sistema

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Health check del servicio QR |

---

## 12. Reportes

**Base URL:** `/api/reportes`

### Reportes Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ingresos-mensuales` | Ingresos mensuales |
| GET | `/membresias-estado` | Membresías por estado |
| GET | `/asistencias-diarias` | Asistencias diarias |
| GET | `/top-planes` | Top planes más contratados |
| GET | `/usuarios-nuevos` | Usuarios nuevos por mes |
| GET | `/membresias-plan` | Membresías por plan |
| GET | `/pagos-metodo` | Pagos agrupados por método de pago |

### Reportes con Filtros

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ingresos-rango?inicio={inicio}&fin={fin}` | Ingresos por rango de fechas |
| GET | `/asistencias-rango?inicio={inicio}&fin={fin}` | Asistencias por rango de fechas |
| GET | `/asistencias-hora?fecha={fecha}` | Asistencias por hora en una fecha |
| GET | `/usuarios-activos?inicio={inicio}&fin={fin}` | Usuarios más activos por rango |

### Análisis

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/rendimiento-mensual` | Rendimiento mensual del gimnasio |
| GET | `/planes-populares` | Planes más populares |
| GET | `/estadisticas-generales` | Estadísticas generales |
| GET | `/estadisticas-fecha?inicio={inicio}&fin={fin}` | Estadísticas por rango de fechas |
| GET | `/dashboard` | Reporte consolidado para dashboard |

### Sistema

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/limpiar-cache` | Limpiar cache de reportes |

---

## 13. Exportación de Reportes

**Base URL:** `/api/reportes/export`

### Pagos por Método

| Método | Endpoint | Descripción | Tipo |
|--------|----------|-------------|------|
| GET | `/pagos-metodo/pdf` | Exportar pagos por método a PDF | `application/pdf` |
| GET | `/pagos-metodo/excel` | Exportar pagos por método a Excel | `application/xlsx` |

### Usuarios Nuevos

| Método | Endpoint | Descripción | Tipo |
|--------|----------|-------------|------|
| GET | `/usuarios-nuevos/pdf` | Exportar usuarios nuevos a PDF | `application/pdf` |
| GET | `/usuarios-nuevos/excel` | Exportar usuarios nuevos a Excel | `application/xlsx` |

### Top Planes

| Método | Endpoint | Descripción | Tipo |
|--------|----------|-------------|------|
| GET | `/top-planes/pdf` | Exportar top planes a PDF | `application/pdf` |
| GET | `/top-planes/excel` | Exportar top planes a Excel | `application/xlsx` |

---

## 14. Sistema

**Base URL:** `/api`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Health check del sistema |
| GET | `/info` | Información del sistema (nombre, versión, entorno) |

---

## 📊 Resumen de Endpoints por Módulo

| Módulo | Cantidad de Endpoints |
|--------|---------------------|
| Autenticación | 4 |
| Usuarios | 15 |
| Planes | 17 |
| Membresías | 19 |
| Asistencias | 14 |
| Pagos | 11 |
| Flujo de Pagos | 2 |
| Códigos de Pago | 15 |
| Historial de Pagos | 13 |
| Historial de Membresías | 13 |
| Códigos QR | 7 |
| Reportes | 18 |
| Exportación de Reportes | 6 |
| Sistema | 2 |
| **TOTAL** | **156** |

---

## 🔐 Notas de Seguridad

- Los endpoints de **Autenticación** (`/api/auth`) son públicos
- La mayoría de los endpoints requieren autenticación mediante **JWT Token**
- El token debe enviarse en el header: `Authorization: Bearer <token>`
- Los roles de usuario son: **ADMINISTRADOR**, **RECEPCIONISTA**, **CLIENTE**

---

## 📅 Formatos de Fecha

- **Fecha simple:** `YYYY-MM-DD` (ejemplo: `2025-11-20`)
- **Fecha y hora:** `YYYY-MM-DDTHH:mm:ss` (ejemplo: `2025-11-20T22:30:00`)
- Los parámetros de fecha usan `@DateTimeFormat` de Spring

---

## 🔄 Paginación

Los endpoints con paginación aceptan los siguientes parámetros:
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 10)

Formato de respuesta:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

---

## ✅ Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Error en la solicitud |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - No autorizado |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Conflicto (ej: duplicado) |
| 500 | Internal Server Error - Error del servidor |
