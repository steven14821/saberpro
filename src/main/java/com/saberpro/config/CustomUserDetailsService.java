package com.saberpro.config;

import com.saberpro.model.Usuario;
import com.saberpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Buscar primero por correo; si no, intentar por codigo (compatibilidad)
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .or(() -> usuarioRepository.findByCodigo(correo))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return new CustomUserDetails(
                usuario.getCodigo(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}
