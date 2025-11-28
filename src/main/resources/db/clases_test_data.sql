-- =========================================
-- DATOS DE PRUEBA - MÓDULO DE CLASES
-- Ejecutar DESPUÉS de clases_schema.sql
-- =========================================
-- Los tipos de clase ya están creados en clases_schema.sql
-- Ahora creamos datos de prueba completos
-- =========================================
-- 1. CREAR USUARIOS INSTRUCTORES
-- =========================================
-- IMPORTANTE: Cambia estos passwords por hashes BCrypt reales
-- Puedes generarlos en: https://bcrypt-generator.com/
-- Password de ejemplo: "instructor123"
INSERT INTO usuarios (
        nombre,
        apellido,
        email,
        dni,
        password,
        rol,
        genero,
        telefono,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        'María',
        'González',
        'maria.instructor@gym.com',
        '12345678',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9J1SBgYs3IjYl9B0jK7Kw0rZlQqfC',
        'INSTRUCTOR',
        'FEMENINO',
        '555-0001',
        true,
        NOW(),
        NOW()
    ),
    (
        'Carlos',
        'Ramírez',
        'carlos.instructor@gym.com',
        '87654321',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9J1SBgYs3IjYl9B0jK7Kw0rZlQqfC',
        'INSTRUCTOR',
        'MASCULINO',
        '555-0002',
        true,
        NOW(),
        NOW()
    ),
    (
        'Ana',
        'López',
        'ana.instructor@gym.com',
        '11223344',
        '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9J1SBgYs3IjYl9B0jK7Kw0rZlQqfC',
        'INSTRUCTOR',
        'FEMENINO',
        '555-0003',
        true,
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO NOTHING;
-- =========================================
-- 2. CREAR INSTRUCTORES
-- =========================================
-- Nota: Cambia los usuario_id según los IDs reales de los usuarios creados arriba
-- Para obtener los IDs: SELECT id, nombre, email FROM usuarios WHERE rol = 'INSTRUCTOR';
INSERT INTO instructores (
        usuario_id,
        especialidades,
        biografia,
        foto_perfil,
        activo,
        fecha_creacion
    )
VALUES (
        (
            SELECT id
            FROM usuarios
            WHERE email = 'maria.instructor@gym.com'
            LIMIT 1
        ), '["Zumba", "Aeróbicos", "Baile Latino"]', 'Instructora certificada con 8 años de experiencia en clases de baile fitness. Especializada en ritmos latinos.', NULL,
        true,
        NOW()
    ),
    (
        (
            SELECT id
            FROM usuarios
            WHERE email = 'carlos.instructor@gym.com'
            LIMIT 1
        ), '["Spinning", "HIIT", "CrossFit"]', 'Ex ciclista profesional. Especializado en entrenamientos de alta intensidad y resistencia.', NULL,
        true,
        NOW()
    ),
    (
        (
            SELECT id
            FROM usuarios
            WHERE email = 'ana.instructor@gym.com'
            LIMIT 1
        ), '["Yoga", "Pilates", "Meditación"]', 'Maestra certificada de yoga con enfoque en bienestar integral. 10 años de experiencia.', NULL,
        true,
        NOW()
    ) ON CONFLICT (usuario_id) DO NOTHING;
-- =========================================
-- 3. CREAR HORARIOS DE CLASES
-- =========================================
-- Día de semana: 1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado, 7=Domingo
-- LUNES
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        1,
        1,
        1,
        '06:00',
        25,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    ),
    -- Zumba matutina con María
    (
        2,
        2,
        1,
        '07:00',
        20,
        'Sala de Spinning',
        true,
        NOW(),
        NOW()
    ),
    -- Spinning con Carlos
    (
        3,
        3,
        1,
        '18:00',
        20,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    ),
    -- Yoga con Ana
    (
        1,
        1,
        1,
        '19:00',
        30,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    );
-- Zumba noche con María
-- MARTES
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        5,
        3,
        2,
        '06:30',
        15,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    ),
    -- Pilates con Ana
    (
        4,
        2,
        2,
        '18:00',
        15,
        'Box Fitness',
        true,
        NOW(),
        NOW()
    ),
    -- CrossFit con Carlos
    (
        2,
        2,
        2,
        '19:30',
        20,
        'Sala de Spinning',
        true,
        NOW(),
        NOW()
    );
-- Spinning con Carlos
-- MIÉRCOLES
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        1,
        1,
        3,
        '06:00',
        25,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    ),
    -- Zumba con María
    (
        7,
        2,
        3,
        '07:00',
        15,
        'Box Fitness',
        true,
        NOW(),
        NOW()
    ),
    -- HIIT con Carlos
    (
        3,
        3,
        3,
        '18:30',
        20,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    );
-- Yoga con Ana
-- JUEVES
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        8,
        1,
        4,
        '06:30',
        25,
        'Sala Aeróbicos',
        true,
        NOW(),
        NOW()
    ),
    -- Aeróbicos con María
    (
        2,
        2,
        4,
        '07:00',
        20,
        'Sala de Spinning',
        true,
        NOW(),
        NOW()
    ),
    -- Spinning con Carlos
    (
        5,
        3,
        4,
        '18:00',
        15,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    ),
    -- Pilates con Ana
    (
        1,
        1,
        4,
        '19:00',
        30,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    );
-- Zumba con María
-- VIERNES
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        1,
        1,
        5,
        '06:00',
        25,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    ),
    -- Zumba con María
    (
        4,
        2,
        5,
        '18:00',
        15,
        'Box Fitness',
        true,
        NOW(),
        NOW()
    ),
    -- CrossFit con Carlos
    (
        3,
        3,
        5,
        '19:00',
        20,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    );
-- Yoga con Ana
-- SÁBADO (horarios especiales)
INSERT INTO horarios_clase (
        tipo_clase_id,
        instructor_id,
        dia_semana,
        hora_inicio,
        aforo_maximo,
        sala,
        activo,
        fecha_creacion,
        fecha_actualizacion
    )
VALUES (
        1,
        1,
        6,
        '09:00',
        35,
        'Sala de Baile 1',
        true,
        NOW(),
        NOW()
    ),
    -- Zumba sábado con María
    (
        2,
        2,
        6,
        '10:00',
        20,
        'Sala de Spinning',
        true,
        NOW(),
        NOW()
    ),
    -- Spinning con Carlos
    (
        3,
        3,
        6,
        '11:00',
        25,
        'Sala Yoga',
        true,
        NOW(),
        NOW()
    );
-- Yoga con Ana
-- =========================================
-- VERIFICACIÓN
-- =========================================
-- Ver tipos de clase creados
SELECT id,
    nombre,
    nivel,
    duracion_minutos
FROM tipos_clase
WHERE activo = true;
-- Ver instructores creados
SELECT i.id,
    u.nombre,
    u.apellido,
    u.email,
    i.especialidades
FROM instructores i
    JOIN usuarios u ON i.usuario_id = u.id
WHERE i.activo = true;
-- Ver horarios creados por día
SELECT h.id,
    h.dia_semana,
    CASE
        h.dia_semana
        WHEN 1 THEN 'Lunes'
        WHEN 2 THEN 'Martes'
        WHEN 3 THEN 'Miércoles'
        WHEN 4 THEN 'Jueves'
        WHEN 5 THEN 'Viernes'
        WHEN 6 THEN 'Sábado'
        WHEN 7 THEN 'Domingo'
    END as dia_nombre,
    h.hora_inicio,
    t.nombre as clase,
    u.nombre || ' ' || u.apellido as instructor,
    h.aforo_maximo,
    h.sala
FROM horarios_clase h
    JOIN tipos_clase t ON h.tipo_clase_id = t.id
    JOIN instructores i ON h.instructor_id = i.id
    JOIN usuarios u ON i.usuario_id = u.id
WHERE h.activo = true
ORDER BY h.dia_semana,
    h.hora_inicio;
COMMIT;
-- =========================================
-- NOTAS IMPORTANTES
-- =========================================
-- 1. Después de ejecutar este script, usa Swagger para generar sesiones:
--    POST /api/clases/sesiones/generar?dias=7
--
-- 2. Esto creará sesiones automáticas para los próximos 7 días
--    basándose en los horarios recurrentes
--
-- 3. Luego podrás reservar clases con:
--    POST /api/clases/reservas/reservar
--    {
--      "sesionId": 1,
--      "usuarioId": <id_de_usuario_con_membresia>
--    }
-- =========================================