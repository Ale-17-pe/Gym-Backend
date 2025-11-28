# Módulo de Clases y Horarios - Guía de Uso

## 📋 Resumen

Este módulo implementa un sistema completo de gestión de clases grupales para el gimnasio.

### Funcionalidades Principales:
- ✅ Gestión de tipos de clases (Zumba, Spinning, Yoga, etc.)
- ✅ Gestión de instructores
- ✅ Horarios recurrentes de clases
- ✅ Generación automática de sesiones
- ✅ Sistema de reservas con control de aforo
- ✅ Lista de espera automática
- ✅ Sistema de penalizaciones por no asistir
- ✅ Cancelación de reservas con política de tiempo

---

## 🗄️ Estructura de Base de Datos

### Tablas Creadas:
1. **tipos_clase** - Catálogo de tipos de clases
2. **instructores** - Instructores del gimnasio
3. **horarios_clase** - Horarios recurrentes (ej: Lunes 18:00)
4. **sesiones_clase** - Sesiones específicas (ej: Zumba del 27-Nov-2025)
5. **reservas_clase** - Reservas de usuarios
6. **penalizaciones_clase** - Registro de penalizaciones

### Ejecutar Schema:
```sql
-- En PostgreSQL
\c gym
\i backend/src/main/resources/db/clases_schema.sql
```

---

## 🚀 Configuración Inicial

### 1. Crear Usuario Instructor

Primero crear un usuario con rol INSTRUCTOR:

```sql
INSERT INTO usuarios (nombre, apellido, email, dni, password, rol, activo, fecha_creacion, fecha_actualizacion)
VALUES (
  'María',
  'López',
  'maria@gym.com',
  '11111111',
  '$2a$10$...', -- Password encriptado
  'INSTRUCTOR',
  true,
  NOW(),
  NOW()
);
```

### 2. Crear Instructor

```bash
POST /api/clases/instructores
{
  "usuarioId": 10,
  "especialidades": "[\"Zumba\", \"Aeróbicos\", \"Baile\"]",
  "biografia": "Instructora certificada con 5 años de experiencia",
  "fotoPerfil": "https://imagen.com/maria.jpg"
}
```

### 3. Crear Horarios de Clases

```bash
POST /api/clases/horarios
{
  "tipoClase": { "id": 1 },  # Zumba
  "instructor": { "id": 1 },
  "diaSemana": 1,  # 1=Lunes
  "horaInicio": "18:00",
  "aforoMaximo": 30,
  "sala": "Sala de Baile 1",
  "activo": true
}
```

### 4. Generar Sesiones Automáticamente

```bash
POST /api/clases/sesiones/generar?dias=7
```

Esto crea sesiones automáticas para los próximos 7 días según los horarios.

---

## 📡 Endpoints Principales

### **Tipos de Clase**

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/clases/tipos/activos` | Listar tipos activos | Público |
| POST | `/api/clases/tipos` | Crear tipo | ADMIN |
| PUT | `/api/clases/tipos/{id}` | Actualizar tipo | ADMIN |

### **Instructores**

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/clases/instructores/activos` | Listar activos | Público |
| POST | `/api/clases/instructores` | Crear instructor | ADMIN |
| PUT | `/api/clases/instructores/{id}` | Actualizar | ADMIN/INSTRUCTOR |

### **Horarios**

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/clases/horarios/activos` | Listar activos | Público |
| GET | `/api/clases/horarios/dia/{dia}` | Por día (1-7) | Público |
| POST | `/api/clases/horarios` | Crear horario | ADMIN |

### **Sesiones**

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/clases/sesiones/semana` | Semana actual | Público |
| GET | `/api/clases/sesiones/fecha/{fecha}` | Por fecha | Público |
| POST | `/api/clases/sesiones/generar` | Generar sesiones | ADMIN |

### **Reservas** ⭐

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| POST | `/api/clases/reservas/reservar` | Reservar clase | Cliente |
| DELETE | `/api/clases/reservas/{id}/cancelar` | Cancelar reserva | Cliente |
| GET | `/api/clases/reservas/usuario/{id}` | Mis reservas | Cliente |
| PUT | `/api/clases/reservas/{id}/asistencia` | Marcar asistencia | RECEP/ADMIN |

---

## 💡 Flujos de Uso

### Flujo 1: Cliente Reserva una Clase

```bash
# 1. Ver calendario de clases de la semana
GET /api/clases/sesiones/semana

# Response:
[
  {
    "id": 100,
    "fecha": "2025-11-28",
    "horarioClase": {
      "tipoClase": { "nombre": "Zumba" },
      "instructor": { "usuario": { "nombre": "María" } },
      "horaInicio": "18:00",
      "aforoMaximo": 30
    },
    "estado": "PROGRAMADA",
    "asistentesActuales": 25
  }
]

# 2. Reservar
POST /api/clases/reservas/reservar
{
  "sesionId": 100,
  "usuarioId": 5
}

# Response:
{
  "id": 200,
  "estado": "CONFIRMADA",  # o "LISTA_ESPERA" si está llena
  "posicionListaEspera": null
}
```

### Flujo 2: Cliente Cancela Reserva

```bash
DELETE /api/clases/reservas/200/cancelar?usuarioId=5
```

### Flujo 3: Recepcionista Marca Asistencia

```bash
PUT /api/clases/reservas/200/asistencia?asistio=true
```

---

## ⚙️ Reglas de Negocio

### Reservas:
- ✅ Solo usuarios con membresía **ACTIVA** pueden reservar
- ✅ Máximo **5 reservas simultáneas** por usuario
- ✅ No se puede reservar la misma clase dos veces
- ✅ Si la clase está llena → Lista de espera automática

### Cancelaciones:
- ✅ Se puede cancelar hasta **2 horas antes** sin penalización
- ⚠️  Cancelación tardía (< 2hrs) → 1 punto de penalización
- ⚠️  No asistir sin cancelar → 1 punto de penalización
- 🚫 **3 puntos** = Suspensión de **7 días**

### Generación de Sesiones:
- 🤖 **Automática**: Todos los días a las 00:00
- 📅 Genera sesiones para los próximos **7 días**
- 🔄 Solo crea si no existe ya
- 📊 Basado en horarios activos

---

## 🔐 Seguridad

### Roles y Permisos:

**Público (sin login):**
- Ver tipos de clase
- Ver calendario de sesiones
- Ver instructores

**CLIENTE:**
- Reservar clases
- Cancelar sus propias reservas
- Ver sus reservas

**INSTRUCTOR:**
- Todo lo del cliente
- Ver reservas de sus clases
- Completar/cancelar sus sesiones
- Editar su perfil

**RECEPCIONISTA:**
- Marcar asistencia
- Ver todas las reservas

**ADMINISTRADOR:**
- Acceso total
- Crear/editar tipos de clase
- Crear/editar horarios
- Gestionar instructores

---

## 📊 Scheduler Automático

El sistema incluye un **scheduler** que se ejecuta automáticamente:

```java
@Scheduled(cron = "0 0 0 * * ?")  // Todos los días a las 00:00
public void generarSesionesDiarias()
```

**¿Qué hace?**
- Revisa todos los horarios activos
- Genera sesiones para los próximos 7 días
- No duplica sesiones ya existentes

---

## 🧪 Datos de Ejemplo

El script SQL ya incluye 8 tipos de clase:
- Zumba 💃
- Spinning 🚴
- Yoga 🧘
- CrossFit 💪
- Pilates
- Boxing 🥊
- HIIT 🔥
- Aeróbicos ❤️

---

## 🛠️ Próximos Pasos

1. **Integración con Notific aciones**:
   - Confirmación de reserva
   - Recordatorio 2 horas antes
   - Aviso de lista de espera

2. **Frontend**:
   - Calendario visual de clases
   - Página de reservas
   - Panel de instructor

3. **Reportes**:
   - Clases más populares
   - Tasa de ocupación
   - Rating de instructores

---

## ❓ Soporte

Para dudas o problemas:
- Revisar logs: `logging.level.com.gym.backend.Clases=DEBUG`
- Verificar Swagger UI: `http://localhost:8080/swagger-ui.html`

---

**¡El módulo de Clases está listo para usar!** 🎉
