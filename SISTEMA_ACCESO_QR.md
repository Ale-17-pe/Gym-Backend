# Sistema de Acceso QR para Membresías

## Descripción General
El sistema permite a los clientes generar códigos QR temporales para acceder al gimnasio. Los recepcionistas escanean o ingresan manualmente estos códigos para validar el acceso.

## Componentes del Sistema

### 1. Generación de Código de Acceso

**Endpoint**: `POST /api/membresias/{id}/generar-qr`

**Flujo**:
1. Cliente solicita generación desde su perfil
2. Sistema genera código único: `MEM-{ID}-{RANDOM}`
   - Ejemplo: `MEM-123-A1B2C3`
3. Código expira en 5 minutos
4. Sistema genera imagen QR en formato Base64
5. Retorna DTO con código, expiración y QR

**Request**:
```http
POST /api/membresias/123/generar-qr
Authorization: Bearer {token}
```

**Response**:
```json
{
  "id": 123,
  "codigoAcceso": "MEM-123-A1B2C3",
  "codigoExpiracion": "2025-11-24T22:25:00",
  "qrBase64": "iVBORw0KGgoAAAANS...",
  "estado": "ACTIVA",
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-12-01"
}
```

### 2. Validación de Acceso

**Endpoint**: `POST /api/membresias/validar-acceso`

**Flujo**:
1. Recepcionista escanea QR o ingresa código manual
2. Sistema busca código en base de datos
3. Valida que:
   - Código existe
   - No ha expirado (< 5 minutos)
   - Membresía está activa
4. Invalida código (uso único)
5. Retorna información de membresía

**Request**:
```http
POST /api/membresias/validar-acceso
Content-Type: application/json

{
  "codigo": "MEM-123-A1B2C3"
}
```

**Response (éxito)**:
```json
{
  "id": 123,
  "usuarioId": 456,
  "nombreUsuario": "Juan Pérez",
  "planId": 1,
  "nombrePlan": "Mensual",
  "estado": "ACTIVA",
  "fechaInicio": "2025-11-01",
  "fechaFin": "2025-12-01",
  "diasRestantes": 7,
  "activa": true
}
```

**Errores Comunes**:
- `404`: Código no encontrado
- `400`: Código expirado (mensaje: "El código de acceso ha expirado. Genera uno nuevo.")
- `400`: Membresía no activa

## Arquitectura Backend

### Capa de Dominio

**MembresiaUseCase.java**:
```java
// Genera código de acceso temporal
public Membresia generarCodigoAcceso(Long membresiaId)

// Valida y consume código (one-time use)
@Transactional
public Membresia validarCodigoAcceso(String codigo)
```

**QrUseCase.java**:
```java
// Genera QR como array de bytes
public byte[] generarQRBytes(String codigoPago)
```

### Capa de Infraestructura

**MembresiaController.java**:
```java
@PostMapping("/{id}/generar-qr")
public ResponseEntity<MembresiaDTO> generarQR(@PathVariable Long id) {
    var membresia = useCase.generarCodigoAcceso(id);
    byte[] qrBytes = qrUseCase.generarQRBytes(membresia.getCodigoAcceso());
    String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);
    
    var dto = mapper.toDTO(membresia);
    dto.setQrBase64(qrBase64);
    return ResponseEntity.ok(dto);
}
```

### Modelo de Datos

**Tabla `membresias`**:
```sql
ALTER TABLE membresias ADD COLUMN codigo_acceso VARCHAR(50);
ALTER TABLE membresias ADD COLUMN codigo_expiracion TIMESTAMP;
CREATE INDEX idx_codigo_acceso ON membresias(codigo_acceso);
```

## Seguridad

### Características de Seguridad
1. **Expiración Temporal**: Códigos válidos solo 5 minutos
2. **Uso Único**: Código se invalida después del primer uso
3. **Código Aleatorio**: UUID garantiza unicidad
4. **Validación de Estado**: Verifica membresía activa antes de permitir acceso
5. **Autorización**: Solo el propietario puede generar su código

### Formato del Código
```
MEM-{MEMBRESIA_ID}-{RANDOM_6_CHARS}
Ejemplo: MEM-123-A1B2C3
```

## Flujo de Usuario

### Cliente (Generar QR)
1. Navega a su perfil
2. Click en "Generar Código de Acceso"
3. Sistema muestra:
   - Imagen QR
   - Código alfanumérico
   - Tiempo de expiración (cuenta regresiva)

### Recepcionista (Validar Acceso)
1. Cliente llega al gimnasio
2. Recepcionista escanea QR o solicita código manual
3. Sistema valida y muestra:
   - ✅ Nombre del cliente
   - ✅ Plan activo
   - ✅ Días restantes
4. Código se invalida automáticamente

## Casos de Uso

### Caso 1: Acceso Normal
1. Cliente genera QR en su celular
2. Muestra QR al recepcionista
3. Recepcionista escanea
4. Sistema confirma acceso
5. Cliente ingresa al gimnasio

### Caso 2: QR Expirado
1. Cliente generó QR hace 6 minutos
2. Intenta ingresar
3. Recepcionista escanea
4. Sistema rechaza: "Código expirado"
5. Cliente genera nuevo QR

### Caso 3: Entrada Manual
1. Cliente no puede mostrar QR
2. Recepcionista solicita código
3. Cliente lee código: "MEM-123-A1B2C3"
4. Recepcionista ingresa manualmente
5. Sistema valida y permite acceso

## Monitoreo y Logs

```java
// Generación
log.info("Código de acceso generado: {} para membresía ID: {}", codigo, membresiaId);

// Validación exitosa
log.info("Código de acceso validado exitosamente para membresía ID: {}", membresia.getId());

// Validación fallida
log.warn("Intento de validación con código inválido: {}", codigo);
log.warn("Código expirado detectado para membresía ID: {}", membresia.getId());
```

## Consideraciones de Implementación

### Performance
- Índice en `codigo_acceso` para búsqueda rápida
- Generación de QR en memoria (no persistido)
- Base64 encoding para transporte eficiente

### Escalabilidad
- Códigos independientes por membresía
- No hay estado compartido
- Operaciones atómicas con `@Transactional`

### Mantenibilidad
- Arquitectura hexagonal (desacoplamiento)
- Módulo QR reutilizable
- Validaciones centralizadas en UseCase
