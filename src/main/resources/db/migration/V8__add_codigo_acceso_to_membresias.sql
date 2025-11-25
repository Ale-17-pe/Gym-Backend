-- Agregar campos para código de acceso temporal a membresías
ALTER TABLE membresias
ADD COLUMN IF NOT EXISTS codigo_acceso VARCHAR(50),
    ADD COLUMN IF NOT EXISTS codigo_expiracion TIMESTAMP;
-- Crear índice para búsqueda rápida por código
CREATE INDEX IF NOT EXISTS idx_membresias_codigo_acceso ON membresias(codigo_acceso);