-- Insertar notificaciones de prueba para el usuario (Asegúrate de cambiar el ID 33 por el ID real de tu usuario si es diferente)
-- Notificación 1: Membresía próxima a vencer
INSERT INTO notificacion (
        usuario_id,
        tipo,
        titulo,
        mensaje,
        leida,
        fecha_creacion,
        email_enviado
    )
VALUES (
        33,
        'MEMBRESIA_PROXIMA_VENCER',
        'Tu membresía vence pronto',
        'Recuerda renovar tu plan antes del 30 de Noviembre.',
        false,
        NOW(),
        false
    );
-- Notificación 2: Pago pendiente
INSERT INTO notificacion (
        usuario_id,
        tipo,
        titulo,
        mensaje,
        leida,
        fecha_creacion,
        email_enviado
    )
VALUES (
        33,
        'PAGO_PENDIENTE',
        'Tienes un pago pendiente',
        'El pago de tu última mensualidad no se ha completado.',
        false,
        NOW() - INTERVAL '1 day',
        true
    );
-- Notificación 3: Bienvenida (Leída)
INSERT INTO notificacion (
        usuario_id,
        tipo,
        titulo,
        mensaje,
        leida,
        fecha_creacion,
        email_enviado
    )
VALUES (
        33,
        'BIENVENIDA',
        '¡Bienvenido a AresFitness!',
        'Gracias por unirte a nuestra comunidad.',
        true,
        NOW() - INTERVAL '5 days',
        true
    );
-- Notificación 4: Pago Confirmado
INSERT INTO notificacion (
        usuario_id,
        tipo,
        titulo,
        mensaje,
        leida,
        fecha_creacion,
        email_enviado
    )
VALUES (
        33,
        'PAGO_CONFIRMADO',
        'Pago recibido',
        'Hemos recibido tu pago correctamente.',
        false,
        NOW() - INTERVAL '2 hours',
        true
    );