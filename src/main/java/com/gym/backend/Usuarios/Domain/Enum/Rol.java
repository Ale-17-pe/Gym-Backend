package com.gym.backend.Usuarios.Domain.Enum;

/**
 * Catálogo de roles del sistema
 * Ahora es solo un enum pero hay una tabla ROL para flexibilidad
 */
public enum Rol {
    ADMINISTRADOR("Administrador", "Acceso total al sistema"),
    CLIENTE("Cliente", "Usuario que usa el gimnasio"),
    RECEPCIONISTA("Recepcionista", "Atiende a clientes y gestiona pagos"),
    ENTRENADOR("Entrenador", "Entrena a clientes"),
    CONTADOR("Contador", "Acceso a reportes financieros");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
