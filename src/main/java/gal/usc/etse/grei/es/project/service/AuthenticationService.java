package gal.usc.etse.grei.es.project.service;

import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    private final UserRepository users;

    @Autowired
    public AuthenticationService(UserRepository users) {
        this.users = users;
    }

    //O método lanzará unha excepción se o usuario non se atopa na base de datos
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos o usuario correspondente ao id proporcionado na base de datos,
        // e lanzamos a excepción no caso de que non exista
        User user = users.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario non encontrado " + username);
        }

        // Creamos o usuario de spring empregando o builder
        return org.springframework.security.core.userdetails.User.builder()
                //id
                .username(user.getId())
                // Establecemos o nome do usuario
                //.username(user.getEmail())
                // Establecemos o contrasinal do usuario
                .password(user.getPassword())
                // Establecemos a lista de roles que ten o usuario.
                // Por convenio, os roles sempre teñen o prefixo "ROLE_"
                .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(
                        String.join(",", user.getRoles())
                ))
                // Xeneramos o obxecto do usuario a partir dos datos introducidos no builder
                .build();
    }
}
