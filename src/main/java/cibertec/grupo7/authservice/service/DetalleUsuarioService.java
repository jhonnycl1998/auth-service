package cibertec.grupo7.authservice.service;

import cibertec.grupo7.authservice.model.entity.Rol;
import cibertec.grupo7.authservice.model.entity.Usuario;
import cibertec.grupo7.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String nomusuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNomusuario(nomusuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nomusuario: " + nomusuario));

        return getUserDetails(usuario, getAuthorities(usuario.getRoles()));
    }

    public List<GrantedAuthority> getAuthorities(
            Set<Rol> roles){

        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Rol role : roles){
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getRol()));
        }
        return authorities;

    }


    public UserDetails getUserDetails(Usuario usuario, List<GrantedAuthority> authorities){
        return new User(usuario.getNomusuario(), usuario.getPassword(), usuario.isActivo(), true, true, true,
                authorities);
    }
}
