# 🎯 Plan de Implementación: Sistema de Puntos de Fidelidad

## 📋 Resumen Ejecutivo

El **Sistema de Puntos de Fidelidad** es un módulo que recompensa a los clientes del gimnasio por sus actividades, incentivando la permanencia, participación y referencias. Los puntos acumulados pueden canjearse por beneficios como descuentos, días gratis, o productos del gimnasio.

---

## 🏆 Objetivos del Sistema

1. **Aumentar la retención de clientes** mediante recompensas atractivas
2. **Incentivar comportamientos deseados**: asistencias frecuentes, renovaciones anticipadas, referencias
3. **Gamificar la experiencia** del cliente en el gimnasio
4. **Generar datos valiosos** sobre el comportamiento de los clientes

---

## 💎 Reglas de Negocio - Ganancia de Puntos

| Acción | Puntos | Condiciones |
|--------|--------|-------------|
| **Registro inicial** | 100 pts | Una sola vez al registrarse |
| **Verificar email** | 50 pts | Una sola vez al verificar |
| **Comprar membresía** | 10 pts por S/1 | Por cada sol gastado |
| **Renovar membresía** (antes de vencer) | +50 pts bonus | Si renueva 7+ días antes del vencimiento |
| **Asistencia al gimnasio** | 10 pts | Máximo 1 vez por día |
| **Racha de asistencia** (7 días consecutivos) | 100 pts bonus | Una vez por racha completada |
| **Referir a un amigo** | 200 pts | Cuando el amigo completa su primer pago |
| **Reservar clase grupal** | 15 pts | Por clase reservada y asistida |
| **Completar perfil** | 30 pts | Una sola vez |
| **Cumpleaños** | 50 pts | Automático en su cumpleaños |

---

## 🎁 Reglas de Negocio - Canje de Puntos

| Recompensa | Costo en Puntos | Descripción |
|------------|-----------------|-------------|
| **Descuento 10%** en membresía | 500 pts | Aplicable en próxima compra |
| **Descuento 20%** en membresía | 900 pts | Aplicable en próxima compra |
| **1 Día gratis** de acceso | 300 pts | Extiende membresía 1 día |
| **1 Semana gratis** de acceso | 800 pts | Extiende membresía 7 días |
| **Clase personal gratis** | 1000 pts | 1 sesión con entrenador |
| **Merchandising** (toalla, botella) | 600 pts | Canjeable en recepción |

---

## 🏛️ Arquitectura del Módulo

Siguiendo la **Arquitectura Hexagonal** del proyecto:

```
Fidelidad/
├── Domain/
│   ├── PuntosFidelidad.java           # Entidad de dominio (balance del usuario)
│   ├── TransaccionPuntos.java         # Historial de movimientos
│   ├── Recompensa.java                # Catálogo de recompensas
│   ├── Canje.java                     # Registro de canjes
│   ├── Enum/
│   │   ├── TipoTransaccion.java       # GANANCIA, CANJE, EXPIRACION, AJUSTE
│   │   ├── MotivoGanancia.java        # REGISTRO, COMPRA, ASISTENCIA, REFERIDO...
│   │   └── EstadoCanje.java           # PENDIENTE, COMPLETADO, CANCELADO
│   ├── PuntosFidelidadRepositoryPort.java
│   ├── TransaccionPuntosRepositoryPort.java
│   ├── RecompensaRepositoryPort.java
│   └── CanjeRepositoryPort.java
│
├── Application/
│   ├── PuntosFidelidadUseCase.java    # Lógica de negocio principal
│   ├── Dto/
│   │   ├── BalancePuntosDTO.java
│   │   ├── TransaccionPuntosDTO.java
│   │   ├── RecompensaDTO.java
│   │   ├── CanjeDTO.java
│   │   └── CanjeRequest.java
│   └── Mapper/
│       ├── PuntosFidelidadMapper.java
│       └── RecompensaMapper.java
│
└── Infrastructure/
    ├── Entity/
    │   ├── PuntosFidelidadEntity.java
    │   ├── TransaccionPuntosEntity.java
    │   ├── RecompensaEntity.java
    │   └── CanjeEntity.java
    ├── Repository/
    │   ├── PuntosFidelidadJpaRepository.java
    │   ├── PuntosFidelidadRepositoryAdapter.java
    │   ├── TransaccionPuntosJpaRepository.java
    │   ├── TransaccionPuntosRepositoryAdapter.java
    │   ├── RecompensaJpaRepository.java
    │   ├── RecompensaRepositoryAdapter.java
    │   ├── CanjeJpaRepository.java
    │   └── CanjeRepositoryAdapter.java
    └── Controller/
        └── FidelidadController.java
```

---

## 📊 Modelo de Datos

### Tabla: `puntos_fidelidad` (Balance actual del usuario)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT PK | ID único |
| usuario_id | BIGINT FK UNIQUE | Referencia a usuarios |
| puntos_totales | INTEGER | Puntos acumulados históricos |
| puntos_disponibles | INTEGER | Puntos disponibles para canjear |
| puntos_canjeados | INTEGER | Total de puntos canjeados |
| nivel | VARCHAR(20) | BRONCE, PLATA, ORO, PLATINO |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| fecha_actualizacion | TIMESTAMP | Última actualización |

### Tabla: `transacciones_puntos` (Historial de movimientos)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT PK | ID único |
| usuario_id | BIGINT FK | Referencia a usuarios |
| tipo | VARCHAR(20) | GANANCIA, CANJE, EXPIRACION, AJUSTE |
| motivo | VARCHAR(50) | REGISTRO, COMPRA, ASISTENCIA, etc. |
| puntos | INTEGER | Cantidad (+ o -) |
| descripcion | VARCHAR(255) | Descripción detallada |
| referencia_id | BIGINT | ID de pago, asistencia, etc. |
| fecha | TIMESTAMP | Fecha de la transacción |

### Tabla: `recompensas` (Catálogo de recompensas)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT PK | ID único |
| nombre | VARCHAR(100) | Nombre de la recompensa |
| descripcion | VARCHAR(500) | Descripción detallada |
| costo_puntos | INTEGER | Puntos necesarios |
| tipo | VARCHAR(30) | DESCUENTO, EXTENSION, SERVICIO, PRODUCTO |
| valor | DECIMAL(10,2) | Valor del beneficio (%, días, etc.) |
| stock | INTEGER | Stock disponible (null = ilimitado) |
| activo | BOOLEAN | Si está disponible |
| imagen_url | VARCHAR(255) | URL de imagen |
| fecha_inicio | DATE | Inicio de vigencia |
| fecha_fin | DATE | Fin de vigencia (null = sin límite) |

### Tabla: `canjes` (Registro de canjes realizados)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT PK | ID único |
| usuario_id | BIGINT FK | Referencia a usuarios |
| recompensa_id | BIGINT FK | Referencia a recompensas |
| puntos_usados | INTEGER | Puntos canjeados |
| estado | VARCHAR(20) | PENDIENTE, COMPLETADO, CANCELADO |
| codigo_canje | VARCHAR(20) | Código único de canje |
| fecha_canje | TIMESTAMP | Fecha del canje |
| fecha_uso | TIMESTAMP | Fecha cuando se usó |
| usado_en_pago_id | BIGINT | Si es descuento, ID del pago donde se aplicó |

---

## 🔌 API Endpoints

### Para Clientes (CLIENTE)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/fidelidad/mi-balance` | Ver balance y nivel actual |
| GET | `/api/fidelidad/mis-transacciones` | Historial de transacciones |
| GET | `/api/fidelidad/recompensas` | Ver catálogo de recompensas |
| POST | `/api/fidelidad/canjear` | Canjear puntos por recompensa |
| GET | `/api/fidelidad/mis-canjes` | Ver canjes realizados |

### Para Administradores (ADMINISTRADOR, RECEPCIONISTA)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/fidelidad/usuarios/{id}/balance` | Ver balance de un usuario |
| POST | `/api/fidelidad/usuarios/{id}/ajustar` | Ajustar puntos manualmente |
| GET | `/api/fidelidad/estadisticas` | Estadísticas del programa |
| POST | `/api/fidelidad/recompensas` | Crear nueva recompensa |
| PUT | `/api/fidelidad/recompensas/{id}` | Actualizar recompensa |
| DELETE | `/api/fidelidad/recompensas/{id}` | Desactivar recompensa |
| GET | `/api/fidelidad/canjes/pendientes` | Ver canjes pendientes |
| PUT | `/api/fidelidad/canjes/{id}/completar` | Marcar canje como completado |

---

## 📝 Fases de Implementación

### **Fase 1: Estructura Base** (Prioridad Alta)
1. ✅ Crear estructura de carpetas del módulo `Fidelidad`
2. ✅ Crear enums: `TipoTransaccion`, `MotivoGanancia`, `EstadoCanje`
3. ✅ Crear entidades de dominio
4. ✅ Crear entidades JPA
5. ✅ Crear repositorios (Port + Adapter)

### **Fase 2: Lógica de Negocio** (Prioridad Alta)
1. ✅ Implementar `PuntosFidelidadUseCase`
   - Método para otorgar puntos
   - Método para canjear puntos
   - Método para calcular nivel
   - Validaciones de negocio
2. ✅ Crear DTOs y Mappers

### **Fase 3: Integración con Módulos Existentes** (Prioridad Alta)
1. ✅ Integrar con `AuthController` → puntos por registro
2. ✅ Integrar con `EmailVerificationService` → puntos por verificar email
3. ✅ Integrar con `PagoOrquestacionService` → puntos por compras
4. ✅ Integrar con `AsistenciaUseCase` → puntos por asistencias

### **Fase 4: API y Controladores** (Prioridad Media)
1. ✅ Crear `FidelidadController`
2. ✅ Implementar endpoints para clientes
3. ✅ Implementar endpoints para administradores
4. ✅ Configurar seguridad en `WebSecurityConfig`

### **Fase 5: Catálogo de Recompensas** (Prioridad Media)
1. ✅ Crear datos iniciales de recompensas
2. ✅ Implementar lógica de canje
3. ✅ Integrar descuentos con sistema de pagos

### **Fase 6: Gamificación y Niveles** (Prioridad Baja)
1. ⬜ Sistema de niveles (Bronce, Plata, Oro, Platino)
2. ⬜ Beneficios por nivel
3. ⬜ Notificaciones de logros
4. ⬜ Expiración de puntos (opcional)

---

## 💰 Análisis de Rentabilidad

### Costos Estimados
- **Desarrollo**: ~40-60 horas de trabajo
- **Recompensas físicas**: Merchandising (~S/15-30 por producto)
- **Descuentos aplicados**: ~5-15% del valor de membresías canjeadas

### Beneficios Esperados
- **Aumento en retención**: 15-25% más renovaciones
- **Mayor frecuencia de asistencia**: Usuarios más comprometidos
- **Efecto viral por referencias**: Nuevos clientes sin costo de adquisición
- **Datos de comportamiento**: Insights valiosos para marketing

### ROI Esperado
- Si se logra un **20% más de renovaciones**, el programa se paga solo
- Los puntos tienen un "costo real" bajo (~S/0.01-0.02 por punto al canjear)
- El programa genera **engagement** que reduce cancelaciones

---

## ✅ Checklist de Implementación

- [ ] **Fase 1**: Estructura base del módulo
- [ ] **Fase 2**: Lógica de negocio (UseCase)
- [ ] **Fase 3**: Integración con módulos existentes
- [ ] **Fase 4**: API REST completa
- [ ] **Fase 5**: Catálogo de recompensas
- [ ] **Fase 6**: Gamificación avanzada (opcional)

---

## 🚀 ¿Comenzamos?

**Tiempo estimado por fase:**
- Fase 1: ~1-2 horas
- Fase 2: ~2-3 horas
- Fase 3: ~1-2 horas
- Fase 4: ~2-3 horas
- Fase 5: ~1-2 horas
- Fase 6: ~3-4 horas (opcional)

**Total estimado**: 8-12 horas para funcionalidad completa

