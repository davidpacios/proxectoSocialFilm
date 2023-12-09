package gal.usc.etse.grei.es.project.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager manager;
    private final Key key;

    // Establecemos unha duración para os tokens
    private static long TOKEN_DURATION = Duration.ofMinutes(60).toMillis();

    public AuthenticationFilter(AuthenticationManager manager, Key key){
        this.manager = manager;
        this.key = key;
    }

    // Método que tenta autenticar ao usuario a partir da chamada HTTP
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // Obtemos o obxecto JSON do body da request HTTP
            JsonNode credentials = new ObjectMapper().readValue(request.getInputStream(), JsonNode.class);

            // Tentamos autenticarnos coas credenciais proporcionadas
            return manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.get("email").textValue(),
                            credentials.get("password").textValue()
                    )
            );
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    // Método que se chama cando a autenticación do metodo anterior é satisfactoria
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        // Almacenamos o momento actual
        long now = System.currentTimeMillis();

        // Obtemos a lista de roles asignados ao usuario e concatenamolso nun string separado por comas
        List<String> authorities = authResult.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Creamos o token JWT empregando o builder
        JwtBuilder tokenBuilder = Jwts.builder()
                // Establecemos como "propietario" do token ao usuario que fixo login
                .setSubject(((User)authResult.getPrincipal()).getUsername())
                // Establecemos a data de emisión do token
                .setIssuedAt(new Date(now))
                // Establecemos a data máxima de validez do token
                .setExpiration(new Date(now + TOKEN_DURATION))
                // Engadimos un atributo máis ao corpo do token cos roles do usuario
                .claim("roles", authorities)
                // Asinamos o token coa nosa clave secreta
                .signWith(key);

        // Engadimos o token á resposta na cabeceira "Authentication"
        response.addHeader("Authentication", String.format("Bearer %s", tokenBuilder.compact()));
        // Guardamos el ID del usuario en la respuesta
        response.addHeader("UserID", ((User)authResult.getPrincipal()).getUsername());
    }

}
