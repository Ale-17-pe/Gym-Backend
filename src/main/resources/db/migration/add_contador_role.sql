-- Script para agregar el rol CONTADOR al constraint de la tabla usuarios
-- Primero, eliminar el constraint existente
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;
-- Crear el nuevo constraint que incluye CONTADOR
ALTER TABLE usuarios
ADD CONSTRAINT usuarios_rol_check CHECK (
        rol IN (
            'CLIENTE',
            'RECEPCIONISTA',
            'ADMINISTRADOR',
            'INSTRUCTOR',
            'CONTADOR'
        )
    );
-- Verificar que el constraint se creó correctamente
SELECT conname,
    pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'usuarios'::regclass
    AND conname = 'usuarios_rol_check';