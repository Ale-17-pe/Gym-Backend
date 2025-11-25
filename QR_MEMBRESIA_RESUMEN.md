# Sistema de QR de Membresía - Resumen de Implementación

## ✅ Completado
1. Migración SQL (V8__add_codigo_acceso_to_membresias.sql)
2. MembresiaEntity actualizada con `codigoAcceso` y `codigoExpiracion`
3. MembresiaDTO actualizado con `codigoAcceso`, `codigoExpiracion` y `qrBase64`

## 📋 Pendiente

### Backend
1. **MembresiaService** - Agregar métodos:
   - `generarCodigoAcceso(Long membresiaId)` - Genera código único tipo "MEM-123456-ABC" válido por 5 minutos
   - `obtenerMembresiaConQR(Long usuarioId)` - Devuelve membresía activa con QR generado
   
2. **MembresiaController** - Agregar endpoint:
   - `POST /api/membresias/{id}/generar-qr` - Genera nuevo código de acceso temporal

3. **AsistenciaService** - Actualizar método:
   - `validarCodigoAcceso(String codigo)` - Valida código de membresía y registra asistencia

4. **QrService** - Ya existe, solo usar `generarQRBytes(String codigo)`

### Frontend

1. **perfil.html** - Agregar sección de QR:
   ```html
   <div *ngIf="membresia && membresia.qrBase64" class="qr-section">
     <h4>Tu código de acceso al gimnasio</h4>
     <img [src]="'data:image/png;base64,' + membresia.qrBase64'" alt="QR de acceso">
     <div class="codigo-texto">{{ membresia.codigoAcceso }}</div>
     <p>Válido hasta: {{ membresia.codigoExpiracion | date:'short' }}</p>
     <button (click)="generarNuevoQR()">Generar Nuevo Código</button>
   </div>
   ```

2. **perfil.ts** - Agregar método:
   - `generarNuevoQR()` - Llama al backend para generar nuevo código

3. **membresias.service.ts** - Agregar método:
   - `generarQR(membresiaId: number)` - Llama a endpoint de backend

4. **validar-qr.html/ts** (recepcionista) - Ya funciona, solo necesita validar códigos de membresía además de los de pago

## 🔄 Flujo de Usuario

1. Cliente entra a su perfil
2. Si tiene membresía activa, ve botón "Generar código de acceso"
3. Al hacer clic, se genera un código temporal (5 minutos de validez)
4. Se muestra:
   - QR visual
   - Código alfanumérico (ej: "MEM-123456-ABC")
   - Tiempo de expiración
5. Cliente va al gimnasio y muestra:
   - Opción A: Escanean el QR
   - Opción B: Dicta el código al recepcionista
6. Recepcionista valida el código y registra la asistencia

## 🔒 Seguridad
- Códigos válidos solo por 5 minutos
- Un código no puede usarse múltiples veces (se invalida al primer uso)
- Membresía debe estar ACTIVA
- Membresía no debe estar vencida

## 📝 Próximo Paso
¿Quieres que continúe con la implementación del backend (MembresiaService y Controller)?
