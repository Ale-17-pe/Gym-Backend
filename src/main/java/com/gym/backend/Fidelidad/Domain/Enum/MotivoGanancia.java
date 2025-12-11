package com.gym.backend.Fidelidad.Domain.Enum;

/**
 * Motivos por los cuales se ganan puntos
 */
public enum MotivoGanancia {
    REGISTRO("Bono de bienvenida", 100),
    VERIFICAR_EMAIL("Verificación de email", 50),
    COMPRA("Puntos por compra", 0), // 10 pts por S/1
    RENOVACION_ANTICIPADA("Renovación anticipada", 50),
    ASISTENCIA("Asistencia al gimnasio", 10),
    RACHA_7_DIAS("Racha de 7 días consecutivos", 100),
    REFERIDO("Referir a un amigo", 200),
    RESERVA_CLASE("Reservar clase grupal", 15),
    COMPLETAR_PERFIL("Perfil completado", 30),
    CUMPLEANOS("Bono de cumpleaños", 50),
    AJUSTE_ADMIN("Ajuste administrativo", 0);

    private final String descripcion;
    private final int puntosBase;

    MotivoGanancia(String descripcion, int puntosBase) {
        this.descripcion = descripcion;
        this.puntosBase = puntosBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPuntosBase() {
        return puntosBase;
    }
}
