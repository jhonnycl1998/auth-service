package cibertec.grupo7.authservice.security;

import cibertec.grupo7.authservice.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String CLAVE_SECRETA_UNIFICADA = "MiClaveSecretaSuperSeguraParaProyectoDAW2Grupo7Cibertec2026";
    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${jwt.expiration:3600000}") long expiration) {
        System.out.println("--> [AUTH] CLAVE FORZADA: [" + CLAVE_SECRETA_UNIFICADA + "]");
        this.key = Keys.hmacShaKeyFor(CLAVE_SECRETA_UNIFICADA.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }
    // ... el resto de métodos queda igual

    public String extraerToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            return header.substring(7);
        }
        return null;
    }

    public Claims obtenerClaims(String token){
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public boolean tokenValido(String token){
        try {
            obtenerClaims(token);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    public String generarToken(Usuario usuario, List<GrantedAuthority> authorities){
        return Jwts.builder()
                .id(usuario.getIdusuario().toString())
                .subject(usuario.getNomusuario())
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .issuedAt(Date.from(Instant.now())).expiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(key).compact();
    }

    public void generarAuthSecurity(Claims claims){
        List<String> authorities = claims.get("authorities", List.class);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                claims.getId(), // Guardamos el idusuario en credentials
                authorities.stream().map(SimpleGrantedAuthority::new).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


}
