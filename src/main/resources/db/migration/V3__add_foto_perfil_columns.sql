-- Agregar columnas para foto de perfil en base de datos
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_perfil BYTEA;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_perfil_nombre VARCHAR(255);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_perfil_tipo VARCHAR(100);

