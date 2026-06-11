package com.saberpro.config;

import com.saberpro.model.Usuario;
import com.saberpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ejecuta migraciones de BD necesarias al arrancar.
 * Soluciona columnas que quedaron sin default tras cambios en la entidad.
 */
@Component
@Order(1) // Se ejecuta ANTES que DataInitializer
public class DbMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        try {
            // Arreglar formulario_especifico que no tiene default
            jdbcTemplate.execute(
                "ALTER TABLE notas MODIFY COLUMN formulario_especifico INT NOT NULL DEFAULT 0"
            );
            System.out.println("=== DB MIGRATION: formulario_especifico default aplicado ===");
        } catch (Exception e) {
            // Si ya tiene default o la columna no existe, ignorar el error
            System.out.println("=== DB MIGRATION: formulario_especifico ya estaba OK: " + e.getMessage() + " ===");
        }

        try {
            // Asegurar que tipo_examen tenga default si ya existe en la tabla
            jdbcTemplate.execute(
                "ALTER TABLE notas MODIFY COLUMN tipo_examen VARCHAR(20) NOT NULL DEFAULT 'SABER_PRO'"
            );
            System.out.println("=== DB MIGRATION: tipo_examen default aplicado ===");
        } catch (Exception e) {
            System.out.println("=== DB MIGRATION: tipo_examen ya estaba OK: " + e.getMessage() + " ===");
        }

        // Migrar contrasenas BCrypt a texto plano
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            int actualizados = 0;
            for (Usuario u : usuarios) {
                if (u.getPassword() != null && u.getPassword().startsWith("$2a$")) {
                    String plainPwd;
                    if ("admin".equals(u.getCodigo())) plainPwd = "admin123";
                    else if ("coord".equals(u.getCodigo())) plainPwd = "coord123";
                    else if ("docente".equals(u.getCodigo())) plainPwd = "docente123";
                    else if ("estudiante".equals(u.getCodigo())) plainPwd = "estudiante123";
                    else plainPwd = u.getNombre().trim().split("\\s+")[0].toLowerCase() + u.getCedula();
                    
                    u.setPassword(plainPwd);
                    usuarioRepository.save(u);
                    actualizados++;
                }
            }
            if (actualizados > 0) {
                System.out.println("=== DB MIGRATION: Se convirtieron " + actualizados + " contrasenas de BCrypt a texto plano ===");
            }
        } catch (Exception e) {
            System.err.println("=== DB MIGRATION ERROR: Error migrando contrasenas: " + e.getMessage() + " ===");
        }
    }
}
