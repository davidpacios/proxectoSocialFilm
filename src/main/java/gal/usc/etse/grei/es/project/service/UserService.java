package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User addUser(User user) {
        User u = getUserByEmail(user.getEmail());
        if(u == null)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El usuario ya existe con el correo electrónico proporcionado.");
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.deleteById(id);
    }

    public User updateUser(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        for (Map<String, Object> update : updates) {
            if (update.containsKey("path")) { //Si el update contiene el campo path
                String path = (String) update.get("path"); //Obtenemos el path
                if (path.equals("/email") || path.equals("/birthday")) //Si el path es email o birthday
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede realizar la modificación.");
            }
        }
        User user1 = patchUtils.patch(user, updates);
        return userRepository.save(user1);
    }

    public Page<User> getAllUsers(int page, int size, String sort, String name, String email) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        if(name == null && email == null)
            return userRepository.findAll(request).map(u -> new User().setName(u.getName()).setEmail(u.getEmail()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        if(name != null && email != null)
            return userRepository.findAll(Example.of(new User().setEmail(email).setName(name), matcher), request).map(u -> new User().setName(u.getName()).setEmail(u.getEmail()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        if(name != null)
            return userRepository.findAll(Example.of(new User().setName(name), matcher), request).map(u -> new User().setName(u.getName()).setEmail(u.getEmail()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        return userRepository.findAll(Example.of(new User().setEmail(email), matcher), request).map(u -> new User().setName(u.getName()).setEmail(u.getEmail()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));
    }
}
