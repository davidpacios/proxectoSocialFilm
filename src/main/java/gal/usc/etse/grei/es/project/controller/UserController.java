package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Friendship;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
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
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        Link all = linkTo(UserController.class).withRel(relationProvider.getCollectionResourceRelFor(User.class));

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LINK, all.toString())
                .build();
    }

    @PatchMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id==principal")
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
    public ResponseEntity<Void> deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId){
        User user = userService.getUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));
        User friend = userService.getUserById(friendId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + friendId));

        userService.deleteUserFriendship(user, friend);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
