# 🔔 PLAN DE NOTIFICACIONES PARA ADMIN

## 📊 CATEGORÍAS DE NOTIFICACIONES

### 1. 💰 **NOTIFICACIONES FINANCIERAS** (Críticas)
- ✅ **Pago Confirmado** - Cuando se confirma un pago de membresía
- ⚠️ **Pago Pendiente > 3 días** - Pagos sin confirmar
- 📉 **Meta Mensual en Riesgo** - Si los ingresos están por debajo del objetivo
- 💳 **Pago Rechazado** - Cuando falla un cargo automático
- 🎯 **Meta Mensual Alcanzada** - Cuando se supera el objetivo de ingresos

### 2. 👥 **NOTIFICACIONES DE MEMBRESÍAS** (Alta Prioridad)
- ⏰ **Membresía por Vencer (7 días)** - Lista de usuarios con membresía próxima a vencer
- ⏰ **Membresía por Vencer (24 horas)** - Alerta urgente día antes
- ❌ **Membresía Vencida** - Cuando una membresía expira
- 🆕 **Nueva Membresía** - Cuando se registra un nuevo miembro
- 🔄 **Renovación Exitosa** - Cuando un usuario renueva
- 📊 **Reporte Semanal de Vencimientos** - Resumen cada lunes

### 3. 🆕 **NOTIFICACIONES DE USUARIOS** (Media Prioridad)
- 👤 **Nuevo Usuario Registrado** - Alerta de registro nuevo
- 👋 **Usuario Inactivo (30 días)** - No ha asistido en un mes
- 🎂 **Cumpleaños de Miembro** - Felicitación automática
- 📧 **Solicitud de Cambio de Plan** - Usuario quiere cambiar plan
- ⭐ **Usuario VIP Nuevo** - Cuando alguien compra plan premium

### 4. 📅 **NOTIFICACIONES DE ASISTENCIAS** (Media Prioridad)
- 📊 **Reporte Diario de Asistencias** - Resumen de asistencias del día
- 📉 **Baja Asistencia Detectada** - Cuando cae % de asistencias
- 🔥 **Peak de Asistencias** - Cuando el gym está lleno (> 80% capacidad)
- 📈 **Nuevo Récord de Asistencias** - Mayor asistencia histórica

### 5. 🏋️ **NOTIFICACIONES DE CLASES** (Media Prioridad)
- 🗓️ **Nueva Clase Programada** - Cuando staff programa clase
- ❌ **Clase Cancelada** - Alerta de cancelación
- ⚠️ **Clase con Pocas Reservas** - < 30% capacidad a 24h
- 🎉 **Clase Llena** - 100% capacidad alcanzada
- 👨‍🏫 **Instructor Ausente** - Cuando falta un instructor

### 6. 💪 **NOTIFICACIONES DE RUTINAS** (Baja Prioridad)
- 📝 **Nueva Rutina Asignada por Coach** - Coach asignó rutina a usuario
- ✅ **Rutina Completada** - Usuario terminó su rutina del día
- 🎯 **Meta de Rutina Alcanzada** - Usuario completa objetivo mensual

### 7. 👨‍💼 **NOTIFICACIONES DE PERSONAL** (Alta Prioridad)
- 🆕 **Nuevo Coach/Staff Registrado** - Alta de personal
- ❌ **Staff Inactivo** - No ha marcado asistencia
- 📊 **Evaluación de Personal Pendiente** - Reviews mensuales
- 🎓 **Certificación por Vencer** - Certificados de coaches

### 8. 🎁 **NOTIFICACIONES DE RECOMPENSAS** (Baja Prioridad)
- 🏆 **Logro Desbloqueado** - Usuario alcanza logro
- 💎 **Recompensa Canjeada** - Usuario canjea puntos
- ⭐ **Nuevo Nivel de Usuario** - Usuario sube de nivel

### 9. ⚙️ **NOTIFICACIONES DEL SISTEMA** (Críticas)
- 🔴 **Error Crítico del Sistema** - Fallo importante
- 🔄 **Backup Completado** - Respaldo exitoso
- 📊 **Reporte Automático Generado** - Reportes programados
- 🔧 **Mantenimiento Programado** - Alertas de mantenimiento
- 🔒 **Cambio de Contraseña (Admin)** - Seguridad

### 10. 📣 **NOTIFICACIONES DE MARKETING** (Baja Prioridad)
- 📧 **Campaña de Email Enviada** - Confirmación de envío masivo
- 📊 **Reporte de Campaña** - Resultados de marketing
- 🎯 **Lead Nuevo** - Prospecto interesado

---

## 🎨 DISEÑO DE NOTIFICACIONES

### **Tipos Visuales:**
1. **🔴 CRÍTICO** - Rojo, requiere acción inmediata
2. **🟡 IMPORTANTE** - Amarillo, requiere atención pronto
3. **🔵 INFO** - Azul, informativo
4. **🟢 ÉXITO** - Verde, confirmación positiva

### **Acciones:**
- Ver Detalle
- Marcar como leída
- Ir a la sección relacionada
- Descartar
- Recordar más tarde

---

## 📱 CANALES DE NOTIFICACIÓN

1. **En la App (Badge + Panel)** ✅ Prioritario
2. **Email** (Resúmenes diarios/semanales)
3. **WhatsApp** (Solo críticas - opcional)
4. **Push Notifications** (Móvil - futuro)

---

## ⚙️ CONFIGURACIÓN

El admin debe poder:
- ✅ Activar/Desactivar cada tipo
- ✅ Elegir frecuencia (Inmediato, Diario, Semanal)
- ✅ Elegir canales por tipo
- ✅ Horario de no molestar
- ✅ Prioridad de notificaciones

---

## 📊 IMPLEMENTACIÓN TÉCNICA

### Backend:
```java
@Entity
public class Notificacion {
    private Long id;
    private TipoNotificacion tipo;
    private Prioridad prioridad;
    private String titulo;
    private String mensaje;
    private String accionUrl;
    private Long usuarioId;
    private Boolean leida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLeida;
}

enum TipoNotificacion {
    PAGO, MEMBRESIA, USUARIO, ASISTENCIA, CLASE, 
    RUTINA, PERSONAL, RECOMPENSA, SISTEMA, MARKETING
}

enum Prioridad {
    CRITICA, ALTA, MEDIA, BAJA
}
```

### Frontend:
- Badge en navbar (contador)
- Panel deslizante de notificaciones
- Filtros por tipo/prioridad
- Marcar todas como leídas
- Perfil de usuario con configuración

---

## 🎯 PRIORIDAD DE IMPLEMENTACIÓN

### **FASE 1 - MVP** (Implementar YA)
1. ✅ Panel de notificaciones en navbar
2. ✅ Notificaciones de membresías por vencer
3. ✅ Notificaciones de pagos
4. ✅ Perfil de usuario con configuración básica

### **FASE 2 - Expansión**
5. Notificaciones de asistencias
6. Notificaciones de clases
7. Email automático
8. Configuración avanzada

### **FASE 3 - Avanzado**
9. Analytics de notificaciones
10. WhatsApp integration
11. Push notifications
12. IA para priorización
