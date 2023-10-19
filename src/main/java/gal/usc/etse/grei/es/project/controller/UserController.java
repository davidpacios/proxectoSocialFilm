package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<User> get(@PathVariable("id") String id) {
        return ResponseEntity.of(userService.get(id));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {

        return new ResponseEntity<User>(userService.addUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> patchUser(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        User updatedUser = userService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    //TODO preguntar si hay que dar de alta al amigo en la base de datos si no existe
    //TODO preguntar si se puede hacer como un patch dentro del post
    //TODO preguntar si se hace con un post esto y si las rutas están bien
    @PostMapping("/{id}/friend")
    public ResponseEntity<User> addFriend(@PathVariable("id") String id, @RequestBody User friend) throws JsonPatchException {

        if (friend.getEmail() == null || friend.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email y el nombre son campos obligatorios.");
        }

        User user = userService.get(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id));
        User amigo = userService.getUserByEmail(friend.getEmail());
        if (amigo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amigo no dado de alta en la base de datos.");
        }

        if (user.getFriends() != null && user.getFriends().stream().anyMatch(f -> f.getEmail().equals(friend.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El amigo ya está en la lista de amigos.");
        }


        user.getFriends().add(amigo);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("op", "add");
        updateMap.put("path", "/friends");
        updateMap.put("value", user.getFriends());

        List<Map<String, Object>> updates = Collections.singletonList(updateMap);
        userService.updateUser(id, updates);

        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{userId}/friend/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) throws JsonPatchException {
        User user = userService.get(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));

        // Verificar si el amigo existe en la lista de amigos
        boolean friendExists = user.getFriends() != null && user.getFriends().removeIf(friend -> friend.getId().equals(friendId));

        if (!friendExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El amigo no se encontró en la lista de amigos.");
        }

        // Actualizar la lista de amigos en la base de datos
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("op", "replace");
        updateMap.put("path", "/friends");
        updateMap.put("value", user.getFriends());

        List<Map<String, Object>> updates = Collections.singletonList(updateMap);
        userService.updateUser(userId, updates);

        return ResponseEntity.ok(user);
    }


}
