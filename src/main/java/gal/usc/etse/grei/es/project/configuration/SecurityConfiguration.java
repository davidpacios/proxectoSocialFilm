package gal.usc.etse.grei.es.project.configuration;

import gal.usc.etse.grei.es.project.filter.AuthenticationFilter;
import gal.usc.etse.grei.es.project.filter.AuthorizationFilter;
import gal.usc.etse.grei.es.project.service.AuthenticationService;
import gal.usc.etse.grei.es.project.service.UserService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.security.Key;
import java.util.*;

@Configuration
// Activamos a seguridade na nosa aplicación
@EnableWebSecurity
// Activamos o procesamento de etiquetas @Preauthorize e @Postauthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final AuthenticationService auth;
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Autowired
    public SecurityConfiguration(AuthenticationService auth) {
        this.auth = auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Establecemos o servizo que se empregará para obter os detalles do usuario
        // e cal é o PasswordEncoder que empregaremos
        auth.userDetailsService(this.auth).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        // Establecemos a xerarquia de roles
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        web.expressionHandler(handler);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Deshabilitamos a protección contra ataques CSRF
        http.csrf().disable()
                // Indicamos que por defecto permitimos o acceso de calquera a todos os servizos
                .authorizeRequests().anyRequest().permitAll()
                .and()
                // Engadimos os nosos filtros á cadea de filtros das chamadas
                .addFilter(new AuthenticationFilter(authenticationManager(), tokenSignKey()))
                .addFilter(new AuthorizationFilter(authenticationManager(), tokenSignKey()))
                // Especificamos que queremos sesións sen estado (REST é, por definición, sen estado)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // Creamos unha instancia do algoritmo BCrypt para empregar como encoder
        // de contrasinais
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        Map<String, List<String>> roles = new HashMap<>();
        // Definimos a nosa xerarquia de roles nun map
        // Os valores representan os roles incluidos no rol especificado como clave
        roles.put("ROLE_ADMIN", Collections.singletonList("ROLE_USER"));

        // Creamos a nosa xerarquia de roles a partir do map definido previamente
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(RoleHierarchyUtils.roleHierarchyFromMap(roles));

        return hierarchy;
    }

    @Bean
    public Key tokenSignKey() {
        // Xeramos unha clave de firmado aleatoria para o algoritmo SHA512
        return SecurityConfiguration.key;
    }
}
