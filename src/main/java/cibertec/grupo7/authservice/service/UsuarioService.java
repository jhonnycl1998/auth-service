package cibertec.grupo7.authservice.service;

import cibertec.grupo7.authservice.model.dto.UsuarioRegistroDto;
import cibertec.grupo7.authservice.model.entity.Rol;
import cibertec.grupo7.authservice.model.entity.Usuario;
import cibertec.grupo7.authservice.repository.RolRepository;
import cibertec.grupo7.authservice.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    public Usuario getUsuarioByNomusuario(String nomusuario) {
        return usuarioRepository.findByNomusuario(nomusuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Registro público: asigna rol CLIENTE automáticamente
    @Transactional
    public Usuario registrarCliente(UsuarioRegistroDto dto) {
        String username = (dto.getNomusuario() != null && !dto.getNomusuario().isBlank())
                ? dto.getNomusuario() : dto.getEmail();

        if (usuarioRepository.existsByNomusuario(username)) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }

        Rol rolCliente = rolRepository.findByRol("CLIENTE");
        if (rolCliente == null) {
            rolCliente = rolRepository.save(Rol.builder().rol("CLIENTE").build());
        }

        Set<Rol> rolesAsignados = new HashSet<>();
        rolesAsignados.add(rolCliente);

        Usuario nuevoUsuario = Usuario.builder()
                .nomusuario(username)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .activo(true)
                .roles(rolesAsignados)
                .build();

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional
    public Usuario crearUsuarioConRoles(UsuarioRegistroDto dto) {
        if (usuarioRepository.existsByNomusuario(dto.getNomusuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }
        Set<Rol> rolesAsignados = new HashSet<>();
        if (dto.getRolesIds() != null && !dto.getRolesIds().isEmpty()) {
            List<Rol> rolesEncontrados = rolRepository.findAllById(dto.getRolesIds());
            if (rolesEncontrados.size() != dto.getRolesIds().size()) {
                throw new RuntimeException("Uno o más roles proporcionados no existen en la base de datos.");
            }
            rolesAsignados.addAll(rolesEncontrados);
        } else {
            Rol rolCliente = rolRepository.findByRol("CLIENTE");
            if (rolCliente != null) rolesAsignados.add(rolCliente);
        }
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNomusuario(dto.getNomusuario());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setNombres(dto.getNombres());
        nuevoUsuario.setApellidos(dto.getApellidos());
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRoles(rolesAsignados);
        return usuarioRepository.save(nuevoUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario cambiarRolUsuario(Integer idusuario, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(idusuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol rol = rolRepository.findByRol(nombreRol.replace("ROLE_", ""));
        if (rol == null) {
            throw new RuntimeException("El rol especificado no existe");
        }
        Set<Rol> nuevosRoles = new HashSet<>();
        nuevosRoles.add(rol);
        usuario.setRoles(nuevosRoles);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarEstadoActivo(Integer idusuario, boolean activo) {
        Usuario usuario = usuarioRepository.findById(idusuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }
}