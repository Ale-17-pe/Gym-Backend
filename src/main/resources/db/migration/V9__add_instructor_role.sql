-- Migration: Add INSTRUCTOR role
-- This migration adds the INSTRUCTOR role to support the Classes module
-- Since PostgreSQL uses CHECK constraints for enums in some setups,
-- we need to update the constraint if it exists
-- If your setup uses native PostgreSQL ENUMs, this might differ
-- Option 1: If using CHECK constraint (most common in Spring Boot/JPA)
DO $$ BEGIN -- Check if the constraint exists and update it
IF EXISTS (
    SELECT 1
    FROM information_schema.table_constraints
    WHERE constraint_name LIKE '%rol%'
        AND table_name = 'usuarios'
) THEN -- Drop old constraint
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;
-- Add new constraint with INSTRUCTOR
ALTER TABLE usuarios
ADD CONSTRAINT usuarios_rol_check CHECK (
        rol IN (
            'CLIENTE',
            'RECEPCIONISTA',
            'ADMINISTRADOR',
            'INSTRUCTOR'
        )
    );
END IF;
END $$;
-- Option 2: If using PostgreSQL ENUM type (uncomment if needed)
-- ALTER TYPE rol ADD VALUE IF NOT EXISTS 'INSTRUCTOR';
-- Note: The Java enum already has INSTRUCTOR added in Rol.java
-- This migration ensures the database constraint matches