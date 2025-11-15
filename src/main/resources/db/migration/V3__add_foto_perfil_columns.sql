-- Agregar columnas para foto de perfil
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_perfil_path VARCHAR(255);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS foto_perfil_nombre VARCHAR(255);

