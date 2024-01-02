package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Friendship;
import gal.usc.etse.grei.es.project.model.FriendshipWithUser;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("users")
@Tag(name = "User API", description = "User related operations")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserService userService;
    private final LinkRelationProvider relationProvider;


    @Autowired
    public UserController(UserService userService, LinkRelationProvider relationProvider) {
        this.userService = userService;
        this.relationProvider = relationProvider;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id==principal or (@userService.areFriends( principal,#id))")
    @Operation(
            operationId = "getOneUser",
            summary = "Get a single user details",
            description = "Get the details for a given user. To see the user details " +
                    "you must be the requested user, his friend, or have admin permissions."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The user details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    ResponseEntity<User> get(@PathVariable("id") String id) {

        Optional<User> result = userService.getUserById(id);
        if(result.isPresent()) {
            Link self = linkTo(methodOn(UserController.class).get(id)).withSelfRel();
            //Link all al recurso /movies
            Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    //Get all users
    @Operation(
            operationId = "getAllUser",
            summary = "Get all users details",
            description = "Get the details for all users. To see the user details " +
                    "you must be authenticated."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All users details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                  @RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "email", required = false) String email){

        //hateoas
        Optional<Page<User>> result = Optional.ofNullable(userService.getAllUsers(page, size, sort, name, email));

        if(result.isPresent()) {
            Page<User> data = result.get();
            Pageable metadata = data.getPageable();

            Link self = linkTo(
                    methodOn(UserController.class).getAllUsers(page, size, sort, name, email)
            ).withSelfRel();
            Link first = linkTo(
                    methodOn(UserController.class).getAllUsers(0, size, sort, name, email)
            ).withRel(IanaLinkRelations.FIRST);
            Link last = linkTo(
                    methodOn(UserController.class).getAllUsers(data.getTotalPages() - 1, size, sort, name, email)
            ).withRel(IanaLinkRelations.LAST);
            Link next = linkTo(
                    methodOn(UserController.class).getAllUsers(metadata.next().getPageNumber(), size, sort, name, email)
            ).withRel(IanaLinkRelations.NEXT);
            Link previous = linkTo(
                    methodOn(UserController.class).getAllUsers(metadata.previousOrFirst().getPageNumber(), size, sort, name, email)
            ).withRel(IanaLinkRelations.PREVIOUS);

            Link one = linkTo(
                    methodOn(UserController.class).get(null)
            ).withRel(relationProvider.getItemResourceRelFor(User.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, first.toString())
                    .header(HttpHeaders.LINK, last.toString())
                    .header(HttpHeaders.LINK, next.toString())
                    .header(HttpHeaders.LINK, previous.toString())
                    .header(HttpHeaders.LINK, one.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(
            operationId = "postOneUser",
            summary = "Post a single user details",
            description = "Post the details for a user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The user details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed for argument[i]",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        user.setRoles(Collections.singletonList("ROLE_USER"));
        //hateoas
        Optional<User> result = Optional.ofNullable(userService.addUser(user));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link self = linkTo(methodOn(UserController.class).get(result.get().getId())).withSelfRel();
            Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("#id==principal")
    @Operation(
            operationId = "deleteOneUser",
            summary = "Delete a single user details",
            description = "Delete the details for a user."  +
                    "you must be the requested user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted user details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LINK, all.toString())
                .build();
    }

    @PatchMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id==principal")
    @Operation(
            operationId = "updateOneUserDetails",
            summary = "Update a single user details",
            description = "Update the details for a user."  +
                    "you must be the requested user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated user details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<User> patchUser(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        //hateoas
        Optional<User> result = Optional.ofNullable(userService.updateUser(id, updates));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link self = linkTo(methodOn(UserController.class).get(result.get().getId())).withSelfRel();
            Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/friend")
    @PreAuthorize("#id==principal")
    @Operation(
            operationId = "addFriend",
            summary = "Add a friend to a user",
            description = "Add a friend to a user."  +
                    "you must be the requested user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Friendship details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request: email and name are required, trying to add yourself as a friend , the friend is not registered in the database or the name and email of the friend do not match",
                    content = @Content
            ),
    })
    public ResponseEntity<Friendship> addFriend(@PathVariable("id") String id, @RequestBody User friend){
        if (friend.getEmail() == null || friend.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email y el nombre son campos obligatorios.");

        User user = userService.getUserById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado: " + id));
        User amigo = userService.getUserByEmail(friend.getEmail());
        if (amigo == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amigo no dado de alta en la base de datos.");

        //comprobar que el nombre e email del amigo coinciden
        if (!amigo.getName().equals(friend.getName()) || !amigo.getEmail().equals(friend.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre y el email del amigo no coinciden");

        //mirar si como amigo se está intentando añadir a uno mismo
        if (user.getEmail().equals(friend.getEmail())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede añadir a uno mismo como amigo.");

        return ResponseEntity.ok(userService.addUserFrienship(user, amigo));
    }


    @DeleteMapping("/{userId}/friend/{friendId}")
    @PreAuthorize("#userId==principal")
    @Operation(
            operationId = "deleteFriend",
            summary = "Delete a friend from a user",
            description = "Delete a friend from a user."  +
                    "you must be the requested user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Removed friendship details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Void> deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId){
        User user = userService.getUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));
        User friend = userService.getUserById(friendId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + friendId));

        userService.deleteUserFriendship(user, friend);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Get all friends of a user
    @GetMapping("/{userId}/friends/")
    public ResponseEntity<List<FriendshipWithUser>> getAllFriends(@PathVariable("userId") String id) {
        List<FriendshipWithUser> friends = userService.getFriends(id);
        return ResponseEntity.ok(friends);
    }


}
