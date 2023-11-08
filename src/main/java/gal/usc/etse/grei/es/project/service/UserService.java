package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Date;
import gal.usc.etse.grei.es.project.model.Friendship;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.repository.FriendsRepository;
import gal.usc.etse.grei.es.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service

public class UserService {

    private final UserRepository userRepository;
    private final PatchUtils patchUtils;
    private final PasswordEncoder encoder;
    private final FriendsRepository friendsRepository;

    @Autowired
    public UserService(UserRepository userRepository, PatchUtils patchUtils, PasswordEncoder encoder, FriendsRepository friendsRepository) {
        this.userRepository = userRepository;
        this.patchUtils = patchUtils;
        this.encoder = encoder;
        this.friendsRepository = friendsRepository;
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
        // Modificamos o contrasinal para gardalo codificado na base de datos
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe el usuario con el id proporcionado."));
        userRepository.deleteById(id);
    }

    public User updateUser(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe el usuario con el id proporcionado."));
        for (Map<String, Object> update : updates) {
            if (update.containsKey("path")) { //Si el update contiene el campo path
                String path = (String) update.get("path"); //Obtenemos el path
                if (path.equals("/email") || path.equals("/birthday")) //Si el path es email o birthday
                    throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,"No se puede realizar la modificación.");
            }
        }
        User user1 = patchUtils.patch(user, updates);
        return userRepository.save(user1);
    }

    public Page<User> getAllUsers(int page, int size, String sort, String name, String email) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        if(name == null && email == null)
            return userRepository.findAll(request).map(u -> new User().setName(u.getName()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        if(name != null && email != null)
            return userRepository.findAll(Example.of(new User().setEmail(email).setName(name), matcher), request).map(u -> new User().setName(u.getName()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        if(name != null)
            return userRepository.findAll(Example.of(new User().setName(name), matcher), request).map(u -> new User().setName(u.getName()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));

        return userRepository.findAll(Example.of(new User().setEmail(email), matcher), request).map(u -> new User().setName(u.getName()).setBirthday(u.getBirthday()).setCountry(u.getCountry()));
    }


    public Friendship addUserFrienship(User user, User friend) {
        Date fechaActual = new Date();
        Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        fechaActual.setDay(dia);
        fechaActual.setMonth(mes);
        fechaActual.setYear(anio);

        //mirar que no existe la relacion de amistad
        if(friendsRepository.findByUserAndFriend(user.getId(), friend.getId()) != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe la relación de amistad.");


        Friendship friendship = new Friendship().setUser(user.getId()).setFriend(friend.getId()).setConfirmed(true).setSince(fechaActual);

        return friendsRepository.save(friendship);

    }

    public void deleteUserFriendship(User user, User friend) {
        Friendship friendship = friendsRepository.findByUserAndFriend(user.getId(), friend.getId());
        if(friendship == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe la relación de amistad.");
        friendsRepository.delete(friendship);

    }
}
