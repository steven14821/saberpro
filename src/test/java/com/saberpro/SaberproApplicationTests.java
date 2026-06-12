package com.saberpro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.saberpro.repository.AlumnoRepository;
import com.saberpro.model.Alumno;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class SaberproApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testRenderResultadosWeb() throws Exception {
        List<Alumno> alumnos = alumnoRepository.findAll();
        System.out.println("TESTING ALL ALUMNOS: " + alumnos.size());
        for (Alumno a : alumnos) {
            String username = a.getCodigo();
            System.out.println("TESTING GET /estudiante/resultados FOR: " + a.getNombre() + " (" + username + ") State: " + a.getEstado());
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            try {
                mockMvc.perform(get("/estudiante/resultados").principal(authToken))
                    .andExpect(status().isOk());
                System.out.println("SUCCESS for " + a.getNombre());
            } catch (Exception | AssertionError e) {
                System.err.println("FAILED for " + a.getNombre() + ": " + e.getMessage());
                throw e;
            }
        }
    }

    @Test
    void testRenderResultadosUserEstudiante() throws Exception {
        String username = "estudiante";
        System.out.println("TESTING GET /estudiante/resultados AS SEED USER: " + username);
        
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        mockMvc.perform(get("/estudiante/resultados").principal(authToken))
            .andExpect(status().isOk());
        
        System.out.println("SUCCESSFULLY PERFORMED GET /estudiante/resultados FOR SEED USER");
    }
}
