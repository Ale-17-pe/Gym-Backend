# 🔄 Flujos del Sistema - Gym Management

## 📋 Índice

1. [Flujo de Autenticación](#1-flujo-de-autenticación)
2. [Flujo de Registro de Usuario](#2-flujo-de-registro-de-usuario)
3. [Flujo de Contratación de Plan](#3-flujo-de-contratación-de-plan)
4. [Flujo de Pago](#4-flujo-de-pago)
5. [Flujo de Asistencia](#5-flujo-de-asistencia)
6. [Flujo de Gestión de Membresías](#6-flujo-de-gestión-de-membresías)
7. [Flujo de Reportes](#7-flujo-de-reportes)
8. [Flujos por Rol](#8-flujos-por-rol)

---

## 1. Flujo de Autenticación

### 🔐 Login (Cualquier Rol)

```mermaid
sequenceDiagram
    participant U as Usuario
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    U->>F: Ingresa email/DNI y contraseña
    F->>B: POST /api/auth/login
    B->>DB: Buscar usuario
    DB-->>B: Usuario encontrado
    B->>B: Validar contraseña (BCrypt)
    B->>B: Verificar usuario activo
    B->>B: Generar JWT Token
    B-->>F: Token + Datos usuario (rol, id, email)
    F->>F: Guardar token en localStorage
    F->>F: Redirigir según rol
    F-->>U: Dashboard correspondiente
```

**Endpoints:**
- `POST /api/auth/login`

**Request:**
```json
{
  "emailOrDni": "admin@gym.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Admin",
    "apellido": "User",
    "email": "admin@gym.com",
    "rol": "ADMINISTRADOR"
  }
}
```

**Redirección por rol:**
- CLIENTE → `/cliente/dashboard`
- RECEPCIONISTA → `/recepcionista/dashboard`
- ADMINISTRADOR → `/admin/dashboard`

---

## 2. Flujo de Registro de Usuario

### 👤 Registro Público (Cliente)

```mermaid
sequenceDiagram
    participant U as Usuario
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    U->>F: Completa formulario registro
    F->>F: Validar datos (frontend)
    F->>B: POST /api/auth/register
    B->>B: Validar datos (backend)
    B->>DB: Verificar email/DNI único
    DB-->>B: Email/DNI disponible
    B->>B: Encriptar contraseña
    B->>DB: Crear usuario (rol: CLIENTE)
    DB-->>B: Usuario creado
    B->>B: Generar JWT Token
    B-->>F: Token + Datos usuario
    F->>F: Guardar token
    F-->>U: Redirigir a /cliente/dashboard
```

**Endpoint:**
- `POST /api/auth/register`

**Request:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@email.com",
  "dni": "12345678",
  "password": "password123",
  "telefono": "999888777",
  "genero": "MASCULINO",
  "fechaNacimiento": "1990-05-15"
}
```

### 👨‍💼 Creación de Usuario por Admin

```mermaid
sequenceDiagram
    participant A as Admin
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    A->>F: Selecciona "Crear Usuario"
    A->>F: Completa formulario (con rol)
    F->>B: POST /api/usuarios (con token admin)
    B->>B: Verificar rol ADMINISTRADOR
    B->>DB: Crear usuario con rol especificado
    DB-->>B: Usuario creado
    B-->>F: Datos del usuario creado
    F-->>A: Mostrar confirmación
```

**Endpoint:**
- `POST /api/usuarios` (Solo ADMIN)

---

## 3. Flujo de Contratación de Plan

### 📋 Cliente Contrata un Plan

```mermaid
sequenceDiagram
    participant C as Cliente
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    C->>F: Ver planes disponibles
    F->>B: GET /api/planes/activos
    B->>DB: Obtener planes activos
    DB-->>B: Lista de planes
    B-->>F: Planes disponibles
    F-->>C: Mostrar planes
    
    C->>F: Selecciona un plan
    C->>F: Click "Contratar"
    F->>B: POST /api/pagos/iniciar
    Note over B: Se crea: Pago + PaymentCode + Membresía PENDIENTE
    B->>DB: Crear pago (PENDIENTE)
    B->>DB: Generar código de pago único
    B->>DB: Crear membresía (estado: PENDIENTE)
    B->>B: Generar QR para código de pago
    DB-->>B: Todo creado
    B-->>F: Pago + Código + QR
    F-->>C: Mostrar código QR y código de pago
```

**Endpoints:**
1. `GET /api/planes/activos` - Ver planes
2. `POST /api/pagos/iniciar` - Iniciar contratación

**Request Iniciar Pago:**
```json
{
  "usuarioId": 5,
  "planId": 3,
  "metodoPago": "EFECTIVO"
}
```

**Response:**
```json
{
  "pagoId": 123,
  "codigoPago": "GYM-2024-XYZ789",
  "qrDataUri": "data:image/png;base64,iVBORw0...",
  "monto": 150.00,
  "estado": "PENDIENTE",
  "fechaExpiracion": "2024-11-25T23:59:59"
}
```

---

## 4. Flujo de Pago

### 💳 Confirmación de Pago (Recepcionista)

```mermaid
sequenceDiagram
    participant C as Cliente
    participant R as Recepcionista
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    C->>R: Presenta código/QR de pago
    R->>F: Escanea QR o ingresa código
    F->>B: GET /api/payment-codes/validar/{codigo}
    B->>DB: Buscar código de pago
    DB-->>B: Código encontrado
    B->>B: Validar: ACTIVO, no expirado
    B-->>F: Código válido + Detalles pago
    F-->>R: Mostrar detalles del pago
    
    R->>F: Click "Confirmar Pago"
    F->>B: POST /api/pagos/confirmar/{codigo}
    B->>DB: Actualizar pago a CONFIRMADO
    B->>DB: Marcar código como USADO
    B->>DB: Activar membresía (PENDIENTE → ACTIVA)
    B->>DB: Registrar en historial
    DB-->>B: Todo actualizado
    B-->>F: Pago confirmado
    F-->>R: Mostrar confirmación
    R-->>C: "Pago confirmado, membresía activa"
```

**Endpoints:**
1. `GET /api/payment-codes/validar/{codigo}` - Validar código
2. `POST /api/pagos/confirmar/{codigo}` - Confirmar pago

**Estados del Pago:**
- `PENDIENTE` → Cliente generó el pago
- `CONFIRMADO` → Recepcionista confirmó
- `RECHAZADO` → Admin rechazó (devolución)
- `CANCELADO` → Cliente canceló antes de pagar

---

## 5. Flujo de Asistencia

### 🚪 Registro de Entrada al Gimnasio

```mermaid
sequenceDiagram
    participant C as Cliente
    participant R as Recepcionista
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    C->>R: Llega al gimnasio
    R->>F: Busca cliente (por DNI/nombre)
    F->>B: GET /api/usuarios/dni/{dni}
    B->>DB: Buscar usuario
    DB-->>B: Usuario encontrado
    B-->>F: Datos del cliente
    
    R->>F: Verificar acceso
    F->>B: GET /api/membresias/verificar-acceso/{usuarioId}
    B->>DB: Buscar membresía activa
    DB-->>B: Membresía encontrada
    B->>B: Validar: ACTIVA + no vencida
    B-->>F: Tiene acceso = true
    
    alt Tiene acceso
        R->>F: Click "Registrar Entrada"
        F->>B: POST /api/asistencias/entrada/{usuarioId}
        B->>DB: Crear asistencia (horaEntrada)
        DB-->>B: Asistencia creada
        B-->>F: Asistencia registrada
        F-->>R: "Entrada registrada ✓"
        R-->>C: "Bienvenido, puede pasar"
    else No tiene acceso
        F-->>R: "Sin membresía activa ✗"
        R-->>C: "Debe renovar membresía"
    end
```

### 🚪 Registro de Salida

```mermaid
sequenceDiagram
    participant C as Cliente
    participant R as Recepcionista
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    C->>R: Sale del gimnasio
    R->>F: Busca cliente
    R->>F: Click "Registrar Salida"
    F->>B: POST /api/asistencias/salida/{usuarioId}
    B->>DB: Buscar última asistencia del día
    DB-->>B: Asistencia encontrada
    B->>DB: Actualizar horaSalida
    DB-->>B: Actualizado
    B-->>F: Salida registrada
    F-->>R: "Salida registrada ✓"
```

**Endpoints:**
1. `GET /api/membresias/verificar-acceso/{usuarioId}` - Verificar acceso
2. `POST /api/asistencias/entrada/{usuarioId}` - Registrar entrada
3. `POST /api/asistencias/salida/{usuarioId}` - Registrar salida

---

## 6. Flujo de Gestión de Membresías

### 📋 Estados de Membresía

```mermaid
stateDiagram-v2
    [*] --> PENDIENTE: Pago iniciado
    PENDIENTE --> ACTIVA: Pago confirmado
    PENDIENTE --> CANCELADA: Cliente cancela
    
    ACTIVA --> SUSPENDIDA: Admin suspende
    ACTIVA --> VENCIDA: Fecha fin alcanzada
    ACTIVA --> CANCELADA: Cliente cancela
    
    SUSPENDIDA --> ACTIVA: Admin reactiva
    SUSPENDIDA --> VENCIDA: Fecha fin alcanzada
    
    VENCIDA --> ACTIVA: Renovación/Extensión
    
    CANCELADA --> [*]
    VENCIDA --> [*]
```

### 🔄 Extensión de Membresía

```mermaid
sequenceDiagram
    participant A as Admin
    participant F as Frontend
    participant B as Backend
    participant DB as Database

    A->>F: Busca membresía del cliente
    F->>B: GET /api/membresias/usuario/{userId}
    B->>DB: Buscar membresías
    DB-->>B: Membresías encontradas
    B-->>F: Lista membresías
    
    A->>F: Selecciona membresía
    A->>F: Ingresa días a extender (ej: 30)
    F->>B: POST /api/membresias/{id}/extender?dias=30
    B->>DB: Actualizar fechaFin (+30 días)
    B->>DB: Registrar en historial
    DB-->>B: Membresía extendida
    B-->>F: Membresía actualizada
    F-->>A: "Membresía extendida 30 días"
```

**Endpoints:**
- `POST /api/membresias/{id}/extender?dias={dias}` - Extender
- `POST /api/membresias/{id}/suspender` - Suspender
- `POST /api/membresias/{id}/reactivar` - Reactivar
- `POST /api/membresias/{id}/cancelar` - Cancelar

---

## 7. Flujo de Reportes

### 📊 Generación de Reportes (Admin/Recepcionista)

```mermaid
sequenceDiagram
    participant U as Usuario (Admin/Recep)
    participant F as Frontend
    participant B as Backend
    participant DB as Database
    participant Cache as Cache

    U->>F: Selecciona tipo de reporte
    F->>B: GET /api/reportes/dashboard
    
    B->>Cache: Verificar cache
    
    alt Datos en cache
        Cache-->>B: Devolver datos cache
    else No hay cache
        B->>DB: Query ingresos mensuales
        B->>DB: Query asistencias diarias
        B->>DB: Query top planes
        B->>DB: Query estadísticas
        DB-->>B: Datos agregados
        B->>Cache: Guardar en cache
    end
    
    B-->>F: Datos del dashboard
    F->>F: Generar gráficos
    F-->>U: Mostrar dashboard
    
    U->>F: Click "Exportar PDF"
    F->>B: GET /api/reportes/export/top-planes/pdf
    B->>B: Generar PDF con iText
    B-->>F: Archivo PDF
    F-->>U: Descargar PDF
```

**Endpoints Principales:**
- `GET /api/reportes/dashboard` - Dashboard consolidado
- `GET /api/reportes/ingresos-mensuales` - Ingresos
- `GET /api/reportes/asistencias-diarias` - Asistencias
- `GET /api/reportes/top-planes` - Planes populares
- `GET /api/reportes/export/{tipo}/pdf` - Exportar PDF
- `GET /api/reportes/export/{tipo}/excel` - Exportar Excel

---

## 8. Flujos por Rol

### 👤 Flujo Completo: CLIENTE

```mermaid
graph TD
    A[Inicio] --> B[Registro/Login]
    B --> C[Ver Dashboard Cliente]
    C --> D[Ver Planes Disponibles]
    D --> E[Seleccionar Plan]
    E --> F[Generar Pago]
    F --> G[Recibir Código QR]
    G --> H[Pagar en Recepción]
    H --> I[Membresía Activa]
    I --> J[Ir al Gimnasio]
    J --> K[Recep. Registra Entrada]
    K --> L[Entrenar]
    L --> M[Recep. Registra Salida]
    M --> N[Ver Historial]
    N --> O{Membresía por vencer?}
    O -->|Sí| D
    O -->|No| J
```

**Acciones del Cliente:**
1. ✅ Registrarse
2. ✅ Login
3. ✅ Ver planes
4. ✅ Contratar plan
5. ✅ Ver código de pago/QR
6. ✅ Ver su membresía actual
7. ✅ Ver su historial de pagos
8. ✅ Ver su historial de asistencias
9. ✅ Actualizar su perfil

---

### 🧑‍💼 Flujo Completo: RECEPCIONISTA

```mermaid
graph TD
    A[Login] --> B[Dashboard Recepcionista]
    
    B --> C[Cliente Llega]
    C --> D[Buscar Cliente]
    D --> E{Tiene Membresía?}
    E -->|Sí| F[Registrar Entrada]
    E -->|No| G[Informar Cliente]
    
    B --> H[Cliente con Código Pago]
    H --> I[Validar Código]
    I --> J{Código Válido?}
    J -->|Sí| K[Confirmar Pago]
    J -->|No| L[Informar Error]
    K --> M[Activar Membresía]
    
    B --> N[Ver Reportes del Día]
    N --> O[Asistencias Hoy]
    N --> P[Pagos Confirmados]
    
    B --> Q[Cliente Sale]
    Q --> R[Registrar Salida]
```

**Acciones del Recepcionista:**
1. ✅ Verificar membresías
2. ✅ Registrar entradas/salidas
3. ✅ Validar códigos de pago
4. ✅ Confirmar pagos
5. ✅ Ver asistencias del día
6. ✅ Ver reportes
7. ✅ Exportar reportes
8. ✅ Generar códigos QR

---

### 👨‍💼 Flujo Completo: ADMINISTRADOR

```mermaid
graph TD
    A[Login Admin] --> B[Dashboard Admin]
    
    B --> C[Gestión Usuarios]
    C --> C1[Crear Usuarios]
    C --> C2[Modificar Usuarios]
    C --> C3[Activar/Desactivar]
    
    B --> D[Gestión Planes]
    D --> D1[Crear Planes]
    D --> D2[Modificar Precios]
    D --> D3[Ver Estadísticas]
    
    B --> E[Gestión Financiera]
    E --> E1[Ver Ingresos]
    E --> E2[Aprobar/Rechazar Pagos]
    E --> E3[Exportar Reportes]
    
    B --> F[Análisis]
    F --> F1[Top Planes]
    F --> F2[Usuarios Activos]
    F --> F3[Rendimiento Mensual]
    
    B --> G[Configuración]
    G --> G1[Configurar Sistema]
```

**Acciones del Administrador:**
1. ✅ Todo lo del Recepcionista +
2. ✅ Crear/modificar/eliminar usuarios
3. ✅ Crear/modificar/eliminar planes
4. ✅ Gestionar membresías completas
5. ✅ Aprobar/rechazar pagos
6. ✅ Ver todos los reportes
7. ✅ Configurar sistema
8. ✅ Exportar todo a PDF/Excel

---

## 🔄 Flujo de Estados Completo

### Estado del Pago
```
PENDIENTE → [Recep confirma] → CONFIRMADO
          → [Admin rechaza] → RECHAZADO
          → [Cliente cancela] → CANCELADO
```

### Estado de Membresía
```
PENDIENTE → [Pago confirmado] → ACTIVA → [Fecha fin] → VENCIDA
                                      → [Admin suspende] → SUSPENDIDA
                                      → [Cliente cancela] → CANCELADA
```

### Estado de Payment Code
```
ACTIVO → [Pago confirmado] → USADO
       → [Fecha límite] → EXPIRADO
       → [Cliente cancela] → CANCELADO
```

---

## 📝 Casos de Uso Especiales

### Caso 1: Cliente sin Membresía Intenta Entrar
```
1. Cliente llega al gimnasio
2. Recepcionista busca cliente
3. Verifica membresía: GET /api/membresias/verificar-acceso/{userId}
4. Response: { "tieneAcceso": false }
5. Recepcionista informa: "No tiene membresía activa"
6. Ofrece ver planes disponibles
```

### Caso 2: Código de Pago Expirado
```
1. Cliente trae código después de la fecha límite
2. Recepcionista valida: GET /api/payment-codes/validar/{codigo}
3. Backend responde: Error "Código expirado"
4. Admin puede: 
   - Generar nuevo código
   - Cancelar y crear nuevo pago
```

### Caso 3: Renovación de Membresía Vencida
```
1. Cliente con membresía VENCIDA quiere renovar
2. Cliente/Admin inicia nuevo pago
3. Se crea nueva membresía PENDIENTE
4. Al confirmar pago → ACTIVA
5. Membresía anterior queda VENCIDA (histórico)
```

---

## 🎯 Resumen de Endpoints por Flujo

| Flujo | Endpoints Principales |
|-------|----------------------|
| **Autenticación** | `POST /api/auth/login`, `POST /api/auth/register` |
| **Contratación** | `GET /api/planes/activos`, `POST /api/pagos/iniciar` |
| **Pago** | `GET /api/payment-codes/validar/{codigo}`, `POST /api/pagos/confirmar/{codigo}` |
| **Asistencia** | `POST /api/asistencias/entrada/{id}`, `POST /api/asistencias/salida/{id}` |
| **Membresías** | `GET /api/membresias/usuario/{id}`, `POST /api/membresias/{id}/extender` |
| **Reportes** | `GET /api/reportes/dashboard`, `GET /api/reportes/export/**` |

---

**🎯 Con esta documentación, tienes todos los flujos necesarios para implementar el frontend!**
