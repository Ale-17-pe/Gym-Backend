-- Tabla de notificaciones para PostgreSQL
CREATE TABLE IF NOT EXISTS notificacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_leida TIMESTAMP NULL,
    email_enviado BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_notificacion_usuario ON notificacion(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificacion_leida ON notificacion(leida);
CREATE INDEX IF NOT EXISTS idx_notificacion_fecha ON notificacion(fecha_creacion);