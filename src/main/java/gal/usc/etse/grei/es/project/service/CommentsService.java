package gal.usc.etse.grei.es.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private PatchUtils patchUtils;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository, UserRepository userRepository, FilmRepository filmRepository) {
        this.commentsRepository = commentsRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    public Optional<Assessment> get(String id) {
        return commentsRepository.findById(id);
    }

    //TODO mirar el friends de la persona que se añade al comentario
    //TODO como nosotros implementamos id en el usuario, lo metemos como obligario aqui tb ademas del nombre e email
    //TODO comprobaciones que pone los campos son obligatorios
    public Assessment addComment(Assessment comentario) {
        Optional<User> userOptional = userRepository.findById(comentario.getUser().getId());
        if (!userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no existe.");
        }

        //comprobar que el nombre e email del usuario coinciden
        if (!userOptional.get().getName().equals(comentario.getUser().getName()) || !userOptional.get().getEmail().equals(comentario.getUser().getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El nombre y el email del usuario no coinciden con el id");
        
        Optional<Film> filmOptional = filmRepository.findById(comentario.getMovie().getId());
        if (!filmOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La película no existe.");
        }

        //comprobar que el titulo de la pelicula coinciden
        if (!filmOptional.get().getTitle().equals(comentario.getMovie().getTitle()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El título de la película no coincide con el id");


        if (comentario.getRating() < 0 || comentario.getRating() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La valoración debe estar entre 0 y 5.");

        //hacer que una misma persona no pueda comentar dos veces la misma película
        for (Assessment assessment : commentsRepository.findAll()) {
            if (assessment.getUser().getEmail().equals(comentario.getUser().getEmail()) && assessment.getMovie().getId().equals(comentario.getMovie().getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El usuario ya ha comentado esta película.");
        }

        return commentsRepository.save(comentario);

    }


    public void deleteComment(String id) {
        commentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        commentsRepository.deleteById(id);
    }


    public List<Assessment> getCommentsByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado");
        }

        // Obtener los comentarios del usuario por su ID
        List<Assessment> comments = commentsRepository.findByUserId(userId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron comentarios para el usuario con ID " + userId);
        }

        return comments;

    }

}
