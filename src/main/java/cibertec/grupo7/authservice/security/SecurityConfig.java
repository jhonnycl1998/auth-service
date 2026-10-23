package cibertec.grupo7.authservice.security;

import cibertec.grupo7.authservice.service.DetalleUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    public final DetalleUsuarioService detalleUsuarioService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwt) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login", "/api/v1/auth/registro")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new FiltroJwtAuth(jwt),
                        UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(daoAuth());
        return http.build();
    }


    @Bean
    AuthenticationProvider daoAuth(){
        DaoAuthenticationProvider daoAuth = new DaoAuthenticationProvider(detalleUsuarioService);
        daoAuth.setPasswordEncoder(passwordEncoder());
        return daoAuth;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder();}

    @Bean
    AuthenticationManager authManager(
            AuthenticationConfiguration configuration)
        throws Exception{
        return configuration.getAuthenticationManager();
    }

}
