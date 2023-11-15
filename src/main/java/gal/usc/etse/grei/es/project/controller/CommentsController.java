package gal.usc.etse.grei.es.project.controller;

import javax.validation.Valid;

import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import gal.usc.etse.grei.es.project.model.Assessment;
import gal.usc.etse.grei.es.project.service.CommentsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("comments")
public class CommentsController {

    private final CommentsService commentsService;

    @Autowired
    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Assessment> addComment(@RequestBody @Valid Assessment comentario) {
        return new ResponseEntity<>(commentsService.addComment(comentario), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    //TODO mirar aqui lo de el solo el propio usuario
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        commentsService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO Falta amigos
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId==principal")
    public List<Assessment> getCommentsByUserId(@PathVariable String userId) {
        return commentsService.getCommentsByUserId(userId);
    }


    @GetMapping("/movie/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public List<Assessment> getCommentsByMovieId(@PathVariable String movieId) {
        return commentsService.getCommentsByMovieId(movieId);
    }

    //TODO mirar lo del propio usuario
    @PatchMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Assessment> patchUser(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        Assessment updatedComment = commentsService.updateComment(id, updates);
        return ResponseEntity.ok(updatedComment);
    }


}
