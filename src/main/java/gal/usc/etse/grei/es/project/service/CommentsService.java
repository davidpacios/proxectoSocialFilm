package gal.usc.etse.grei.es.project.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import gal.usc.etse.grei.es.project.model.Assessment;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.repository.CommentsRepository;
import gal.usc.etse.grei.es.project.repository.UserRepository;
import gal.usc.etse.grei.es.project.repository.FilmRepository;


@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final PatchUtils patchUtils;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository, UserRepository userRepository, FilmRepository filmRepository, PatchUtils patchUtils) {
        this.commentsRepository = commentsRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.patchUtils = patchUtils;
    }

    public Optional<Assessment> get(String id) {
        return commentsRepository.findById(id);
    }

    public Assessment addComment(Assessment comentario) {
        Optional<User> userOptional = userRepository.findById(comentario.getUser().getId());
        if (!userOptional.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no existe.");

        //comprobar que el nombre e email del usuario coinciden
        if (!userOptional.get().getName().equals(comentario.getUser().getName()) || !userOptional.get().getEmail().equals(comentario.getUser().getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El nombre y el email del usuario no coinciden con el id");
        
        Optional<Film> filmOptional = filmRepository.findById(comentario.getMovie().getId());
        if (!filmOptional.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La película no existe.");

        //comprobar que el titulo de la pelicula coinciden
        if (!filmOptional.get().getTitle().equals(comentario.getMovie().getTitle()))  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El título de la película no coincide con el id");

        if (comentario.getRating() < 0 || comentario.getRating() > 5)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La valoración debe estar entre 0 y 5.");

        //hacer que una misma persona no pueda comentar dos veces la misma película
        for (Assessment assessment : commentsRepository.findAll()) {
            if (assessment.getUser().getEmail().equals(comentario.getUser().getEmail()) && assessment.getMovie().getId().equals(comentario.getMovie().getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El usuario ya ha comentado esta película.");
        }

        return commentsRepository.save(comentario);
    }


    public void deleteComment(String id) {
        commentsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentario no encontrado"));
        commentsRepository.deleteById(id);
    }


    public Page<Assessment> getCommentsByUserId(String userId, int page, int size, String sort) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado");
        User user = new User().setId(userId).setEmail(userOptional.get().getEmail()).setName(userOptional.get().getName());

        //List<Assessment> comments = commentsRepository.findByUserId(userId);
        Page<Assessment> comments = commentsRepository.findAll(Example.of(new Assessment().setUser(user), matcher), request);
        if (comments.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron comentarios para el usuario con ID " + userId);

        return comments;
    }

    public Page<Assessment> getCommentsByMovieId(String movieId, int page, int size, String sort) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Optional<Film> filmOptional = filmRepository.findById(movieId);
        if (!filmOptional.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pelicula no encontrada");
        Film film = new Film().setId(movieId).setTitle(filmOptional.get().getTitle());

        // Obtener los comentarios de la pelicula por su ID
        //List<Assessment> comments = commentsRepository.findByMovieId(movieId);
        Page<Assessment> comments = commentsRepository.findAll(Example.of(new Assessment().setMovie(film), matcher), request);
        if (comments.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron comentarios para la pelicula con ID " + movieId);

        return comments;
    }

    public Assessment updateComment(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        Assessment comment = commentsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentario no encontrado"));
        comment = patchUtils.patch(comment, updates);
        return commentsRepository.save(comment);
    }

    public boolean isCommentFromUser(String id, String userId) {
        Optional<Assessment> comment = commentsRepository.findById(id);
        if (!comment.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentario no encontrado");
        return comment.get().getUser().getId().equals(userId);
    }
}
