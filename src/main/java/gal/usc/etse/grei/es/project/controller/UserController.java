package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> get(@PathVariable("id") String id) {
        return ResponseEntity.of(userService.getUserById(id));
    }

    //Get all users
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "sort", defaultValue = "id") String sort,
                                                  @RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "email", required = false) String email){

        return ResponseEntity.ok(userService.getAllUsers(page, size, sort, name, email));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> patchUser(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        User updatedUser = userService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{id}/friend")
    public ResponseEntity<User> addFriend(@PathVariable("id") String id, @RequestBody User friend) throws JsonPatchException {
        if (friend.getEmail() == null || friend.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email y el nombre son campos obligatorios.");

        User user = userService.getUserById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id));
        User amigo = userService.getUserByEmail(friend.getEmail());
        if (amigo == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amigo no dado de alta en la base de datos.");

        //comprobar que el nombre e email del amigo coinciden
        if (!amigo.getName().equals(friend.getName()) || !amigo.getEmail().equals(friend.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre y el email del amigo no coinciden");

        if (user.getFriends() == null) user.setFriends(new ArrayList<>());

        if (amigo.getFriends() == null) amigo.setFriends(new ArrayList<>());

        if (user.getFriends().stream().anyMatch(f -> f.getEmail().equals(friend.getEmail()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El amigo ya está en la lista de amigos.");

        //mirar si como amigo se está intentando añadir a uno mismo
        if (user.getEmail().equals(friend.getEmail())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede añadir a uno mismo como amigo.");

        friend.setId(amigo.getId());
        user.getFriends().add(friend);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("op", "add");
        updateMap.put("path", "/friends");
        updateMap.put("value", user.getFriends());

        List<Map<String, Object>> updates = Collections.singletonList(updateMap);
        userService.updateUser(id, updates);

        User useraux = new User();
        useraux.setId(user.getId());
        useraux.setName(user.getName());
        useraux.setEmail(user.getEmail());
        amigo.getFriends().add(useraux);
        updateMap = new HashMap<>();
        updateMap.put("op", "add");
        updateMap.put("path", "/friends");
        updateMap.put("value", amigo.getFriends());

        updates = Collections.singletonList(updateMap);
        userService.updateUser(amigo.getId(), updates);

        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{userId}/friend/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) throws JsonPatchException {
        User user = userService.getUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));
        User friend = userService.getUserById(friendId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + friendId));

        //Verificar que el user y amigo no son el mismo
        if (userId.equals(friendId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar a uno mismo como amigo.");

        // Verificar si el amigo existe en la lista de amigos
        boolean friendExists = user.getFriends() != null && user.getFriends().removeIf(friends -> friend.getId().equals(friendId));
        if (!friendExists) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El amigo no se encontró en la lista de amigos.");

        if (friend.getFriends() != null) {
            for (User u : friend.getFriends()) {
                if (u.getId().equals(userId)) {
                    friend.getFriends().remove(u);
                    break;
                }
            }
        }

        // Actualizar la lista de amigos en la base de datos
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("op", "replace");
        updateMap.put("path", "/friends");
        updateMap.put("value", user.getFriends());

        List<Map<String, Object>> updates = Collections.singletonList(updateMap);
        userService.updateUser(userId, updates);

        updateMap = new HashMap<>();
        updateMap.put("op", "replace");
        updateMap.put("path", "/friends");
        updateMap.put("value", friend.getFriends());

        updates = Collections.singletonList(updateMap);
        userService.updateUser(friendId, updates);

        return ResponseEntity.ok(user);
    }


}
