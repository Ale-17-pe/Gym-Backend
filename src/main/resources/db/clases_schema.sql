-- =========================================
-- MÓDULO DE CLASES Y HORARIOS
-- Script de creación de tablas
-- =========================================
-- Tabla: tipos_clase
-- Catalogo de tipos de clases (Zumba, Spinning, Yoga, etc.)
CREATE TABLE tipos_clase (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    duracion_minutos INTEGER NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    -- PRINCIPIANTE, INTERMEDIO, AVANZADO
    icono VARCHAR(50),
    -- Nombre del icono Material
    color VARCHAR(20),
    -- Color hex para UI
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);
-- Tabla: instructores
-- Instructores que imparten clases
CREATE TABLE instructores (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    especialidades TEXT,
    -- JSON array de especialidades
    biografia TEXT,
    foto_perfil VARCHAR(255),
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    UNIQUE(usuario_id)
);
-- Tabla: horarios_clase
-- Horarios recurrentes de clases (ej: Lunes 18:00)
CREATE TABLE horarios_clase (
    id BIGSERIAL PRIMARY KEY,
    tipo_clase_id BIGINT NOT NULL REFERENCES tipos_clase(id),
    instructor_id BIGINT NOT NULL REFERENCES instructores(id),
    dia_semana INTEGER NOT NULL CHECK (
        dia_semana BETWEEN 1 AND 7
    ),
    -- 1=Lunes, 7=Domingo
    hora_inicio TIME NOT NULL,
    aforo_maximo INTEGER NOT NULL CHECK (aforo_maximo > 0),
    sala VARCHAR(100),
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW()
);
-- Tabla: sesiones_clase
-- Instancias específicas de clases (ej: Zumba del 27-Nov-2025 18:00)
CREATE TABLE sesiones_clase (
    id BIGSERIAL PRIMARY KEY,
    horario_clase_id BIGINT NOT NULL REFERENCES horarios_clase(id),
    fecha DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    -- PROGRAMADA, CANCELADA, COMPLETADA
    asistentes_actuales INTEGER DEFAULT 0,
    notas_instructor TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    UNIQUE(horario_clase_id, fecha)
);
-- Tabla: reservas_clase
-- Reservas de usuarios a clases específicas
CREATE TABLE reservas_clase (
    id BIGSERIAL PRIMARY KEY,
    sesion_clase_id BIGINT NOT NULL REFERENCES sesiones_clase(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    -- CONFIRMADA, CANCELADA, LISTA_ESPERA, ASISTIO, NO_ASISTIO
    fecha_reserva TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_cancelacion TIMESTAMP,
    asistio BOOLEAN,
    posicion_lista_espera INTEGER,
    -- NULL si no está en lista de espera
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP DEFAULT NOW(),
    UNIQUE(sesion_clase_id, usuario_id)
);
-- Tabla: penalizaciones_clase
-- Registro de penalizaciones por no asistir
CREATE TABLE penalizaciones_clase (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    reserva_clase_id BIGINT NOT NULL REFERENCES reservas_clase(id),
    tipo VARCHAR(50) NOT NULL,
    -- NO_ASISTENCIA, CANCELACION_TARDIA
    puntos INTEGER NOT NULL DEFAULT 1,
    fecha_penalizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    activo BOOLEAN DEFAULT true -- Se puede levantar manualmente
);
-- =========================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =========================================
-- Índices para búsquedas frecuentes
CREATE INDEX idx_horarios_dia_hora ON horarios_clase(dia_semana, hora_inicio);
CREATE INDEX idx_horarios_tipo_clase ON horarios_clase(tipo_clase_id);
CREATE INDEX idx_horarios_instructor ON horarios_clase(instructor_id);
CREATE INDEX idx_sesiones_fecha ON sesiones_clase(fecha);
CREATE INDEX idx_sesiones_estado ON sesiones_clase(estado);
CREATE INDEX idx_sesiones_horario ON sesiones_clase(horario_clase_id);
CREATE INDEX idx_reservas_usuario ON reservas_clase(usuario_id);
CREATE INDEX idx_reservas_sesion ON reservas_clase(sesion_clase_id);
CREATE INDEX idx_reservas_estado ON reservas_clase(estado);
CREATE INDEX idx_penalizaciones_usuario ON penalizaciones_clase(usuario_id);
-- =========================================
-- DATOS DE EJEMPLO (SEEDS)
-- =========================================
-- Tipos de clase populares
INSERT INTO tipos_clase (
        nombre,
        descripcion,
        duracion_minutos,
        nivel,
        icono,
        color
    )
VALUES (
        'Zumba',
        'Baile aeróbico con ritmos latinos',
        60,
        'PRINCIPIANTE',
        'music_note',
        '#FF6B6B'
    ),
    (
        'Spinning',
        'Ciclismo indoor de alta intensidad',
        45,
        'INTERMEDIO',
        'directions_bike',
        '#4ECDC4'
    ),
    (
        'Yoga',
        'Posturas y meditación para cuerpo y mente',
        60,
        'PRINCIPIANTE',
        'self_improvement',
        '#95E1D3'
    ),
    (
        'CrossFit',
        'Entrenamiento funcional de alta intensidad',
        60,
        'AVANZADO',
        'fitness_center',
        '#F38181'
    ),
    (
        'Pilates',
        'Fortalecimiento del core y flexibilidad',
        50,
        'INTERMEDIO',
        'accessibility_new',
        '#AA96DA'
    ),
    (
        'Boxing',
        'Entrenamiento de boxeo fitness',
        45,
        'INTERMEDIO',
        'sports_martial_arts',
        '#FCBAD3'
    ),
    (
        'HIIT',
        'Intervalos de alta intensidad',
        30,
        'AVANZADO',
        'local_fire_department',
        '#FFA07A'
    ),
    (
        'Aeróbicos',
        'Ejercicio cardiovascular con música',
        45,
        'PRINCIPIANTE',
        'favorite',
        '#DDA15E'
    );
-- Nota: Los instructores se deben crear manualmente después de crear usuarios con rol INSTRUCTOR
COMMIT;