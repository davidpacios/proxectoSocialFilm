package gal.usc.etse.grei.es.project.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
@Tag(name = "Authentication API", description = "Authentication operations")
public class AuthController {
    @PostMapping()
    @Operation(
            operationId = "login",
            summary = "Login",
            description = "Login with username and password to obtain a JWT token."
    )
    public void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User and password for authentication",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                value = "{\"email\": \"test@test.com\", \"password\": \"test\"}"
                            )
                    )
            )
            @RequestBody Map<String, String> userpass
    ) {}
}
