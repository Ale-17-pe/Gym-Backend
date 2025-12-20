-- =====================================================
-- DATOS INICIALES PARA GYM_V2
-- Ejecutar después de que Hibernate cree las tablas
-- =====================================================
-- 1. INSERTAR ROLES (obligatorio)
INSERT INTO rol (id, nombre)
VALUES (1, 'ROLE_CLIENTE'),
    (2, 'ROLE_RECEPCIONISTA'),
    (3, 'ROLE_INSTRUCTOR'),
    (4, 'ROLE_ADMINISTRADOR'),
    (5, 'ROLE_CONTADOR');
-- 2. TIPOS DE CLASE
INSERT INTO tipo_clase (
        id,
        nombre,
        descripcion,
        duracion_minutos,
        nivel,
        color,
        icono,
        activo,
        fecha_creacion
    )
VALUES (
        1,
        'CrossFit',
        'Entrenamiento funcional de alta intensidad',
        60,
        'Intermedio',
        '#ef4444',
        'fitness_center',
        true,
        NOW()
    ),
    (
        2,
        'Yoga',
        'Ejercicios de relajación y flexibilidad',
        60,
        'Principiante',
        '#22c55e',
        'self_improvement',
        true,
        NOW()
    ),
    (
        3,
        'Spinning',
        'Ciclismo indoor de alta intensidad',
        45,
        'Intermedio',
        '#3b82f6',
        'directions_bike',
        true,
        NOW()
    ),
    (
        4,
        'Zumba',
        'Baile fitness con ritmos latinos',
        50,
        'Principiante',
        '#f59e0b',
        'music_note',
        true,
        NOW()
    ),
    (
        5,
        'Pilates',
        'Fortalecimiento del core y flexibilidad',
        55,
        'Principiante',
        '#8b5cf6',
        'accessibility_new',
        true,
        NOW()
    );
-- 3. PLANES DE MEMBRESÍA
INSERT INTO plan (
        id,
        nombre,
        descripcion,
        duracion_meses,
        precio,
        descuento,
        activo,
        destacado,
        orden,
        fecha_creacion
    )
VALUES (
        1,
        'Plan Básico',
        'Acceso a equipos de gimnasio',
        1,
        50.00,
        0,
        true,
        false,
        1,
        NOW()
    ),
    (
        2,
        'Plan Premium',
        'Acceso completo + clases grupales',
        3,
        120.00,
        10,
        true,
        true,
        2,
        NOW()
    ),
    (
        3,
        'Plan VIP',
        'Acceso ilimitado + entrenador personal',
        12,
        400.00,
        20,
        true,
        false,
        3,
        NOW()
    );
-- 4. ACTUALIZAR SECUENCIAS
SELECT setval(
        'rol_id_seq',
        (
            SELECT MAX(id)
            FROM rol
        )
    );
SELECT setval(
        'tipo_clase_id_seq',
        (
            SELECT MAX(id)
            FROM tipo_clase
        )
    );
SELECT setval(
        'plan_id_seq',
        (
            SELECT MAX(id)
            FROM plan
        )
    );
-- 5. EJERCICIOS
INSERT INTO ejercicio (
        nombre,
        descripcion,
        grupo_muscular,
        dificultad,
        equipamiento,
        instrucciones,
        activo,
        fecha_creacion
    )
VALUES -- PECHO
    (
        'Press de Banca',
        'Ejercicio fundamental para pecho',
        'PECHO',
        'INTERMEDIO',
        'Barra y banco',
        'Acuéstate en el banco, baja la barra al pecho y empuja hacia arriba',
        true,
        NOW()
    ),
    (
        'Press Inclinado con Mancuernas',
        'Trabaja pecho superior',
        'PECHO',
        'INTERMEDIO',
        'Mancuernas y banco inclinado',
        'Banco a 30-45 grados, empuja las mancuernas hacia arriba',
        true,
        NOW()
    ),
    (
        'Aperturas con Mancuernas',
        'Aislamiento de pectorales',
        'PECHO',
        'PRINCIPIANTE',
        'Mancuernas y banco',
        'Brazos abiertos, baja las mancuernas en arco hasta sentir estiramiento',
        true,
        NOW()
    ),
    (
        'Fondos en Paralelas',
        'Trabaja pecho inferior y tríceps',
        'PECHO',
        'AVANZADO',
        'Barras paralelas',
        'Inclínate hacia adelante, baja lentamente y empuja',
        true,
        NOW()
    ),
    (
        'Flexiones de Brazos',
        'Ejercicio básico de empuje',
        'PECHO',
        'PRINCIPIANTE',
        'Peso corporal',
        'Mantén el cuerpo recto, baja hasta casi tocar el suelo',
        true,
        NOW()
    ),
    -- ESPALDA
    (
        'Dominadas',
        'Rey de los ejercicios de espalda',
        'ESPALDA',
        'AVANZADO',
        'Barra de dominadas',
        'Agarre pronado, jala hasta pasar la barbilla de la barra',
        true,
        NOW()
    ),
    (
        'Remo con Barra',
        'Desarrollo de espalda media',
        'ESPALDA',
        'INTERMEDIO',
        'Barra',
        'Inclínate 45 grados, jala la barra hacia el abdomen',
        true,
        NOW()
    ),
    (
        'Jalón al Pecho',
        'Trabaja dorsales',
        'ESPALDA',
        'PRINCIPIANTE',
        'Máquina de poleas',
        'Agarra la barra ancha, jala hacia el pecho sacando pecho',
        true,
        NOW()
    ),
    (
        'Remo con Mancuerna',
        'Trabajo unilateral de espalda',
        'ESPALDA',
        'PRINCIPIANTE',
        'Mancuerna y banco',
        'Apoya una mano en el banco, rema con la otra hacia la cadera',
        true,
        NOW()
    ),
    (
        'Peso Muerto',
        'Ejercicio compuesto fundamental',
        'ESPALDA',
        'AVANZADO',
        'Barra',
        'Espalda recta, levanta la barra extendiendo caderas y rodillas',
        true,
        NOW()
    ),
    -- HOMBROS
    (
        'Press Militar',
        'Desarrollo de hombros frontales',
        'HOMBROS',
        'INTERMEDIO',
        'Barra',
        'De pie, empuja la barra desde los hombros hacia arriba',
        true,
        NOW()
    ),
    (
        'Elevaciones Laterales',
        'Aislamiento de deltoides lateral',
        'HOMBROS',
        'PRINCIPIANTE',
        'Mancuernas',
        'Brazos a los lados, eleva las mancuernas hasta la altura de los hombros',
        true,
        NOW()
    ),
    (
        'Elevaciones Frontales',
        'Trabaja deltoides anterior',
        'HOMBROS',
        'PRINCIPIANTE',
        'Mancuernas',
        'Eleva las mancuernas al frente hasta la altura de los ojos',
        true,
        NOW()
    ),
    (
        'Pájaros',
        'Deltoides posterior',
        'HOMBROS',
        'PRINCIPIANTE',
        'Mancuernas',
        'Inclinado hacia adelante, eleva las mancuernas a los lados',
        true,
        NOW()
    ),
    -- BICEPS
    (
        'Curl con Barra',
        'Ejercicio básico de bíceps',
        'BICEPS',
        'PRINCIPIANTE',
        'Barra',
        'Codos fijos, curl la barra hacia los hombros',
        true,
        NOW()
    ),
    (
        'Curl con Mancuernas',
        'Trabajo alternado de bíceps',
        'BICEPS',
        'PRINCIPIANTE',
        'Mancuernas',
        'Alterna los curls, rotando la muñeca al subir',
        true,
        NOW()
    ),
    (
        'Curl Martillo',
        'Trabaja braquial y braquiorradial',
        'BICEPS',
        'PRINCIPIANTE',
        'Mancuernas',
        'Agarre neutro, curl sin rotar las muñecas',
        true,
        NOW()
    ),
    (
        'Curl Concentrado',
        'Máximo aislamiento de bíceps',
        'BICEPS',
        'INTERMEDIO',
        'Mancuerna',
        'Sentado, codo apoyado en la rodilla, curl lento',
        true,
        NOW()
    ),
    -- TRICEPS
    (
        'Press Francés',
        'Aislamiento de tríceps',
        'TRICEPS',
        'INTERMEDIO',
        'Barra EZ',
        'Acostado, baja la barra detrás de la cabeza',
        true,
        NOW()
    ),
    (
        'Extensiones con Polea',
        'Trabajo de tríceps en polea',
        'TRICEPS',
        'PRINCIPIANTE',
        'Polea alta',
        'Codos fijos, extiende hacia abajo',
        true,
        NOW()
    ),
    (
        'Fondos en Banco',
        'Tríceps con peso corporal',
        'TRICEPS',
        'PRINCIPIANTE',
        'Banco',
        'Manos en el banco, baja el cuerpo flexionando codos',
        true,
        NOW()
    ),
    (
        'Extensión Tras Nuca',
        'Estiramiento completo del tríceps',
        'TRICEPS',
        'INTERMEDIO',
        'Mancuerna',
        'Mancuerna detrás de la cabeza, extiende hacia arriba',
        true,
        NOW()
    ),
    -- PIERNAS
    (
        'Sentadilla con Barra',
        'Rey de los ejercicios de piernas',
        'CUADRICEPS',
        'INTERMEDIO',
        'Barra y rack',
        'Barra en la espalda, baja hasta que los muslos estén paralelos',
        true,
        NOW()
    ),
    (
        'Prensa de Piernas',
        'Trabajo de cuádriceps en máquina',
        'CUADRICEPS',
        'PRINCIPIANTE',
        'Prensa',
        'Empuja la plataforma, no bloquees las rodillas',
        true,
        NOW()
    ),
    (
        'Extensión de Cuádriceps',
        'Aislamiento de cuádriceps',
        'CUADRICEPS',
        'PRINCIPIANTE',
        'Máquina',
        'Extiende las piernas completamente',
        true,
        NOW()
    ),
    (
        'Zancadas',
        'Trabajo unilateral de piernas',
        'CUADRICEPS',
        'INTERMEDIO',
        'Mancuernas',
        'Da un paso adelante, baja la rodilla trasera',
        true,
        NOW()
    ),
    (
        'Curl Femoral',
        'Aislamiento de isquiotibiales',
        'ISQUIOTIBIALES',
        'PRINCIPIANTE',
        'Máquina',
        'Acostado, flexiona las piernas hacia los glúteos',
        true,
        NOW()
    ),
    (
        'Peso Muerto Rumano',
        'Isquiotibiales y glúteos',
        'ISQUIOTIBIALES',
        'INTERMEDIO',
        'Barra',
        'Piernas semi-rectas, baja la barra manteniendo espalda recta',
        true,
        NOW()
    ),
    -- GLUTEOS
    (
        'Hip Thrust',
        'Ejercicio principal de glúteos',
        'GLUTEOS',
        'INTERMEDIO',
        'Barra y banco',
        'Espalda en el banco, empuja caderas hacia arriba',
        true,
        NOW()
    ),
    (
        'Patada de Glúteo',
        'Aislamiento de glúteos',
        'GLUTEOS',
        'PRINCIPIANTE',
        'Peso corporal o tobilleras',
        'A cuatro patas, extiende una pierna hacia atrás',
        true,
        NOW()
    ),
    -- PANTORRILLAS
    (
        'Elevación de Talones de Pie',
        'Desarrollo de gemelos',
        'PANTORRILLAS',
        'PRINCIPIANTE',
        'Máquina o mancuernas',
        'De pie, eleva los talones lo más alto posible',
        true,
        NOW()
    ),
    (
        'Elevación de Talones Sentado',
        'Trabaja el sóleo',
        'PANTORRILLAS',
        'PRINCIPIANTE',
        'Máquina',
        'Sentado, eleva los talones con peso en las rodillas',
        true,
        NOW()
    ),
    -- ABDOMINALES
    (
        'Crunch',
        'Ejercicio básico abdominal',
        'ABDOMINALES',
        'PRINCIPIANTE',
        'Peso corporal',
        'Acostado, eleva los hombros del suelo',
        true,
        NOW()
    ),
    (
        'Plancha',
        'Trabajo isométrico de core',
        'ABDOMINALES',
        'PRINCIPIANTE',
        'Peso corporal',
        'Mantén la posición con el cuerpo recto',
        true,
        NOW()
    ),
    (
        'Elevación de Piernas',
        'Abdominales inferiores',
        'ABDOMINALES',
        'INTERMEDIO',
        'Barra de dominadas',
        'Colgado, eleva las piernas rectas',
        true,
        NOW()
    ),
    (
        'Russian Twist',
        'Trabajo de oblicuos',
        'OBLICUOS',
        'INTERMEDIO',
        'Peso corporal o balón medicinal',
        'Sentado, rota el torso de lado a lado',
        true,
        NOW()
    ),
    -- CARDIO
    (
        'Burpees',
        'Ejercicio de cuerpo completo',
        'CARDIO',
        'AVANZADO',
        'Peso corporal',
        'Sentadilla, flexión, salto, repetir',
        true,
        NOW()
    ),
    (
        'Mountain Climbers',
        'Cardio de alta intensidad',
        'CARDIO',
        'INTERMEDIO',
        'Peso corporal',
        'Posición de plancha, alterna llevando rodillas al pecho',
        true,
        NOW()
    ),
    (
        'Jumping Jacks',
        'Calentamiento y cardio',
        'CARDIO',
        'PRINCIPIANTE',
        'Peso corporal',
        'Salta abriendo piernas y brazos',
        true,
        NOW()
    );
-- Actualizar secuencia de ejercicios
SELECT setval(
        'ejercicio_id_seq',
        (
            SELECT MAX(id)
            FROM ejercicio
        )
    );
-- =====================================================
-- DESPUÉS DE EJECUTAR ESTE SCRIPT:
-- =====================================================
-- 1. Regístrate en el frontend normalmente
-- 
-- 2. Para hacerte ADMIN, ejecuta:
--    UPDATE usuario_roles SET rol_id = 4 WHERE usuario_id = (SELECT id FROM usuario WHERE email = 'TU_EMAIL');
--    
-- 3. Para crear un ENTRENADOR:
--    a) Registra al usuario normalmente
--    b) Desde el panel admin o ejecuta:
--       INSERT INTO empleado (usuario_id, tipo_contrato, activo, fecha_creacion) 
--       VALUES ((SELECT id FROM usuario WHERE email = 'EMAIL_ENTRENADOR'), 'TIEMPO_COMPLETO', true, NOW());
--       
--       INSERT INTO entrenador (empleado_id, usuario_id, especialidad, activo, fecha_creacion)
--       VALUES ((SELECT id FROM empleado ORDER BY id DESC LIMIT 1), (SELECT id FROM usuario WHERE email = 'EMAIL_ENTRENADOR'), 'CROSSFIT', true, NOW());
-- =====================================================