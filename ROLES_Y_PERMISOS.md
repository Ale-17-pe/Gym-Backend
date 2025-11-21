# 🔐 Roles y Permisos del Sistema - Gym Backend

## 📋 Roles Disponibles

El sistema tiene **3 roles** principales definidos en `Rol.java`:

1. **CLIENTE** - Usuario final del gimnasio
2. **RECEPCIONISTA** - Personal de recepción
3. **ADMINISTRADOR** - Personal administrativo con acceso completo

---

## 🎯 Lógica de Negocio por Rol

### 👤 CLIENTE

**¿Quién es?**
- Usuario final que tiene una membresía en el gimnasio
- Puede ser creado mediante registro público o por un administrador

**¿Qué puede hacer?**

#### ✅ Accesos Permitidos:

**1. Autenticación (Público)**
- ✅ `POST /api/auth/login` - Iniciar sesión
- ✅ `POST /api/auth/register` - Registrarse
- ✅ `POST /api/auth/validate` - Validar token
- ✅ `POST /api/auth/refresh` - Renovar token

**2. Membresías (Autenticado)**
- ✅ Ver sus propias membresías
- ✅ Ver estado de su membresía actual
- ✅ Consultar historial de membresías

**3. Pagos (Autenticado)**
- ✅ Ver sus propios pagos
- ✅ Iniciar proceso de pago
- ✅ Confirmar pagos con código
- ✅ Ver historial de pagos

**4. Perfil (Autenticado)**
- ✅ Ver su perfil
- ✅ Actualizar sus datos personales

**5. Planes (Solo lectura)**
- ✅ Ver planes disponibles (para contratar)
- ✅ Ver detalles de planes

#### ❌ Restricciones:

- ❌ NO puede gestionar otros usuarios
- ❌ NO puede crear/modificar/eliminar planes
- ❌ NO puede ver reportes del gimnasio
- ❌ NO puede registrar asistencias de otros
- ❌ NO puede ver datos de otros clientes
- ❌ NO puede aprobar/rechazar pagos

**Flujo típico del CLIENTE:**
```
1. Registrarse o ser registrado
2. Login
3. Ver planes disponibles
4. Contratar un plan (generar pago)
5. Pagar (obtener código QR/código de pago)
6. Confirmar pago en recepción
7. Acceder al gimnasio (asistencia registrada por recepcionista)
8. Consultar su historial (pagos, membresías, asistencias)
```

---

### 🧑‍💼 RECEPCIONISTA

**¿Quién es?**
- Personal de recepción del gimnasio
- Creado únicamente por un administrador
- Gestiona el día a día del gimnasio

**¿Qué puede hacer?**

#### ✅ Accesos Permitidos:

**1. Todo lo del CLIENTE +**

**2. Asistencias (RECEPCIONISTA + ADMIN)**
- ✅ `POST /api/asistencias/entrada/{usuarioId}` - Registrar entrada
- ✅ `POST /api/asistencias/salida/{usuarioId}` - Registrar salida
- ✅ `POST /api/asistencias/{id}/cancelar` - Cancelar asistencia
- ✅ `GET /api/asistencias/**` - Ver todas las asistencias
- ✅ `GET /api/asistencias/estadisticas/**` - Ver estadísticas
- ✅ `GET /api/asistencias/verificar-acceso/{usuarioId}` - Verificar si puede entrar

**3. Reportes (RECEPCIONISTA + ADMIN)**
- ✅ `GET /api/reportes/**` - Ver todos los reportes
- ✅ `GET /api/reportes/dashboard` - Ver dashboard
- ✅ `GET /api/reportes/ingresos-mensuales` - Ver ingresos
- ✅ `GET /api/reportes/asistencias-diarias` - Ver asistencias
- ✅ `GET /api/reportes/export/**` - Exportar reportes

**4. Códigos QR**
- ✅ Generar códigos QR para pagos
- ✅ Validar códigos QR en la entrada

**5. Verificaciones**
- ✅ Verificar estado de membresías
- ✅ Consultar datos de clientes (para validación)
- ✅ Ver historial de pagos de clientes

#### ❌ Restricciones:

- ❌ NO puede crear/modificar/eliminar usuarios
- ❌ NO puede crear/modificar/eliminar planes
- ❌ NO puede aprobar/rechazar pagos (solo confirmar)
- ❌ NO puede modificar configuración del sistema

**Flujo típico del RECEPCIONISTA:**
```
1. Login
2. Cliente llega al gimnasio
3. Verificar si tiene membresía activa
   - GET /api/membresias/verificar-acceso/{usuarioId}
4. Si tiene acceso:
   - Registrar entrada: POST /api/asistencias/entrada/{usuarioId}
5. Cliente trae código de pago:
   - Validar código: GET /api/payment-codes/validar/{codigo}
   - Confirmar pago: POST /api/pagos/confirmar/{codigo}
6. Al final del día:
   - Ver reportes de asistencias: GET /api/reportes/asistencias-diarias
   - Ver estadísticas: GET /api/asistencias/estadisticas
```

---

### 👨‍💼 ADMINISTRADOR

**¿Quién es?**
- Personal administrativo con acceso completo
- Gestiona configuración y operaciones del gimnasio
- Único rol que puede crear otros administradores

**¿Qué puede hacer?**

#### ✅ Accesos Permitidos:

**1. Todo lo del RECEPCIONISTA +**

**2. Usuarios (SOLO ADMIN)**
- ✅ `POST /api/usuarios` - Crear usuarios (todos los roles)
- ✅ `GET /api/usuarios/**` - Ver todos los usuarios
- ✅ `PUT /api/usuarios/{id}` - Actualizar usuarios
- ✅ `DELETE /api/usuarios/{id}` - Eliminar usuarios
- ✅ `PATCH /api/usuarios/{id}/activar` - Activar usuarios
- ✅ `PATCH /api/usuarios/{id}/desactivar` - Desactivar usuarios
- ✅ `GET /api/usuarios/rol/**` - Filtrar por rol
- ✅ `GET /api/usuarios/activos` - Ver activos/inactivos

**3. Planes (SOLO ADMIN)**
- ✅ `POST /api/planes` - Crear planes
- ✅ `PUT /api/planes/{id}` - Actualizar planes
- ✅ `DELETE /api/planes/{id}` - Eliminar planes
- ✅ `PATCH /api/planes/{id}/activar` - Activar planes
- ✅ `PATCH /api/planes/{id}/desactivar` - Desactivar planes
- ✅ `PATCH /api/planes/{id}/rating` - Actualizar rating
- ✅ `GET /api/planes/stats` - Ver estadísticas de planes

**4. Configuración (SOLO ADMIN)**
- ✅ `/api/configuracion/**` - Toda la configuración del sistema

**5. Gestión Completa de:**
- ✅ Membresías (todas las operaciones)
- ✅ Pagos (aprobar, rechazar, cancelar)
- ✅ Payment Codes (gestión completa)
- ✅ Reportes (todos + exportaciones)
- ✅ Asistencias (todas las operaciones)

#### ❌ Restricciones:

- ✅ No hay restricciones, tiene acceso completo al sistema

**Flujo típico del ADMINISTRADOR:**
```
1. Login
2. Gestión de usuarios:
   - Crear recepcionistas: POST /api/usuarios
   - Activar/Desactivar usuarios
   - Ver reportes de usuarios nuevos
3. Gestión de planes:
   - Crear planes: POST /api/planes
   - Modificar precios
   - Ver estadísticas de contratación
4. Gestión financiera:
   - Ver reportes de ingresos: GET /api/reportes/ingresos-mensuales
   - Aprobar/Rechazar pagos
   - Exportar reportes financieros
5. Análisis del negocio:
   - Dashboard: GET /api/reportes/dashboard
   - Estadísticas generales
   - Top planes más contratados
   - Usuarios más activos
```

---

## 🔒 Matriz de Permisos

| Módulo | Endpoint Base | CLIENTE | RECEPCIONISTA | ADMIN |
|--------|---------------|---------|---------------|-------|
| **Auth** | `/api/auth/**` | ✅ Público | ✅ Público | ✅ Público |
| **Usuarios** | `/api/usuarios/**` | ❌ | ❌ | ✅ |
| **Planes** | `/api/planes/**` | 👁️ Solo lectura | 👁️ Solo lectura | ✅ Full |
| **Membresías** | `/api/membresias/**` | 👤 Solo las suyas | ✅ Todas | ✅ Todas |
| **Asistencias** | `/api/asistencias/**` | 👤 Solo las suyas | ✅ Todas | ✅ Todas |
| **Pagos** | `/api/pagos/**` | 👤 Solo los suyos | 👁️ Consulta | ✅ Full |
| **Payment Codes** | `/api/payment-codes/**` | 👤 Solo los suyos | ✅ Validar | ✅ Full |
| **Historial Pagos** | `/api/historial-pagos/**` | 👤 Solo el suyo | 👁️ Consulta | ✅ Full |
| **Historial Membresías** | `/api/historial-membresias/**` | 👤 Solo el suyo | 👁️ Consulta | ✅ Full |
| **QR** | `/api/qr/**` | ✅ Generar | ✅ Generar/Validar | ✅ Full |
| **Reportes** | `/api/reportes/**` | ❌ | ✅ Todos | ✅ Todos |
| **Export** | `/api/reportes/export/**` | ❌ | ✅ PDF/Excel | ✅ PDF/Excel |
| **Sistema** | `/api/health`, `/api/info` | ✅ Público | ✅ Público | ✅ Público |
| **Configuración** | `/api/configuracion/**` | ❌ | ❌ | ✅ |

**Leyenda:**
- ✅ Acceso completo
- 👁️ Solo lectura/consulta
- 👤 Solo sus propios datos
- ❌ Sin acceso

---

## 🚦 Configuración de Seguridad en el Backend

Según `WebSecurityConfig.java`:

```java
// PÚBLICOS (sin autenticación)
.permitAll()
  - /api/auth/**
  - /api/health/**
  - /api/info/**

// SOLO ADMINISTRADOR
.hasRole("ADMINISTRADOR")
  - /api/usuarios/**
  - /api/planes/**
  - /api/configuracion/**

// RECEPCIONISTA + ADMINISTRADOR
.hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
  - /api/asistencias/**
  - /api/reportes/**

// CUALQUIER USUARIO AUTENTICADO
.authenticated()
  - /api/membresias/**
  - /api/pagos/**
  - /api/perfil/**
  - Todos los demás endpoints
```

---

## 🎨 Recomendaciones para el Frontend

### Para CLIENTE:
```javascript
// Menú del cliente
const menuCliente = [
  { label: 'Mi Perfil', route: '/perfil' },
  { label: 'Mi Membresía', route: '/mi-membresia' },
  { label: 'Mis Pagos', route: '/mis-pagos' },
  { label: 'Planes Disponibles', route: '/planes' },
  { label: 'Mis Asistencias', route: '/mis-asistencias' },
];
```

### Para RECEPCIONISTA:
```javascript
// Menú del recepcionista
const menuRecepcionista = [
  { label: 'Dashboard', route: '/dashboard' },
  { label: 'Registrar Asistencia', route: '/asistencias/registrar' },
  { label: 'Asistencias Hoy', route: '/asistencias/hoy' },
  { label: 'Confirmar Pagos', route: '/pagos/confirmar' },
  { label: 'Verificar Membresía', route: '/membresias/verificar' },
  { label: 'Reportes', route: '/reportes' },
  { label: 'Mi Perfil', route: '/perfil' },
];
```

### Para ADMINISTRADOR:
```javascript
// Menú del administrador
const menuAdmin = [
  { label: 'Dashboard', route: '/dashboard' },
  { label: 'Usuarios', route: '/usuarios' },
  { label: 'Planes', route: '/planes' },
  { label: 'Membresías', route: '/membresias' },
  { label: 'Pagos', route: '/pagos' },
  { label: 'Asistencias', route: '/asistencias' },
  { label: 'Reportes', route: '/reportes' },
  { label: 'Configuración', route: '/configuracion' },
];
```

---

## 🔑 Autenticación JWT

Todos los roles (excepto endpoints públicos) necesitan:

**Header:**
```
Authorization: Bearer <token-jwt>
```

**El token contiene:**
- `sub`: ID del usuario
- `rol`: Rol del usuario (CLIENTE, RECEPCIONISTA, ADMINISTRADOR)
- `email`: Email del usuario
- `exp`: Fecha de expiración

---

## ⚠️ Validaciones Adicionales en el Backend

Aunque un usuario esté autenticado, el backend valida:

1. **Usuarios solo pueden ver SUS datos:**
   - Un CLIENTE con ID=5 no puede ver datos del CLIENTE con ID=7
   - Esto se valida en los servicios, no solo en los endpoints

2. **Estados de membresía:**
   - Cliente solo puede acceder si tiene membresía ACTIVA
   - Recepcionista valida esto antes de permitir entrada

3. **Códigos de pago:**
   - Deben estar ACTIVOS
   - No pueden estar EXPIRADOS o USADOS

---

## 📝 Resumen Rápido

| Rol | Puede Crear Usuarios | Puede Gestionar Planes | Registro Asistencias | Ver Reportes | Gestión Completa |
|-----|---------------------|------------------------|---------------------|--------------|------------------|
| **CLIENTE** | ❌ | ❌ | ❌ | ❌ | ❌ |
| **RECEPCIONISTA** | ❌ | ❌ | ✅ | ✅ | ❌ |
| **ADMINISTRADOR** | ✅ | ✅ | ✅ | ✅ | ✅ |

---

**¿Necesitas correcciones o aclaraciones? ¡Dímelo!** 🎯
