package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.repository.FilmRepository;
import gal.usc.etse.grei.es.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service

public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PatchUtils patchUtils;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> get(String id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User addUser(User user) {
        User u = getUserByEmail(user.getEmail());
        if(u == null){
            return userRepository.save(user);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El usuario ya existe con el correo electrónico proporcionado.");
    }

    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.deleteById(id);
    }

    public User updateUser(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        for (Map<String, Object> update : updates) {
            if (update.containsKey("path")) {
                String path = (String) update.get("path");
                if (path.equals("/email") || path.equals("/birthday"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede realizar la modificación.");

            }
        }
        User user1 = patchUtils.patch(user, updates);
        return userRepository.save(user1);
    }
}
