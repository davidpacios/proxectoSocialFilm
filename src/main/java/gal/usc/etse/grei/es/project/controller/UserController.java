package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

}
