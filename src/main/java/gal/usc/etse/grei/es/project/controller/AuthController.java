package gal.usc.etse.grei.es.project.controller;


import gal.usc.etse.grei.es.project.model.Assessment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"email\": \"test@test.com\", \"password\": \"test\"}"
                            )
                    )
            ),
           @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
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
