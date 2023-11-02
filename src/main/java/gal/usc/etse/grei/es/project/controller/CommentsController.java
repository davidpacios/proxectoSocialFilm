package gal.usc.etse.grei.es.project.controller;

import javax.validation.Valid;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Assessment> get(@PathVariable("id") String id) {
        return ResponseEntity.of(commentsService.get(id));
    }

    @PostMapping
    public ResponseEntity<Assessment> addComment(@RequestBody @Valid Assessment comentario) {
        return new ResponseEntity<Assessment>(commentsService.addComment(comentario), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        commentsService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public List<Assessment> getCommentsByUserId(@PathVariable String userId) {
        return commentsService.getCommentsByUserId(userId);
    }


    @GetMapping("/movie/{movieId}")
    public List<Assessment> getCommentsByMovieId(@PathVariable String movieId) {
        return commentsService.getCommentsByMovieId(movieId);
    }


    //TODO EN TODOS LOS PATCH NOS FALTA COMRPOBACION DE QUE EL ID REALMENTE ES VALIDO
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
