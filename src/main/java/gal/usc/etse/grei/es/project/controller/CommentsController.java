package gal.usc.etse.grei.es.project.controller;

import javax.validation.Valid;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import gal.usc.etse.grei.es.project.model.Assessment;
import gal.usc.etse.grei.es.project.service.CommentsService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("comments")
public class CommentsController {

    private final CommentsService commentsService;
    private final UserService userService;

    @Autowired
    public CommentsController(CommentsService commentsService, UserService userService) {
        this.commentsService = commentsService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Assessment> addComment(@RequestBody @Valid Assessment comentario) {
        return new ResponseEntity<>(commentsService.addComment(comentario), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @commentsService.isCommentFromUser(#id, principal)")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        commentsService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId==principal")
    public List<Assessment> getCommentsByUserId(@PathVariable String userId) {
        return commentsService.getCommentsByUserId(userId);
    }


    @GetMapping("/movie/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public List<Assessment> getCommentsByMovieId(@PathVariable String movieId) {
        return commentsService.getCommentsByMovieId(movieId);
    }*/

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("(!#movieId.equals('') and isAuthenticated()) or " +
            "(!#userId.equals('') and (hasRole('ADMIN') or #userId == principal or @userService.areFriends(#userId,principal)))")
    public ResponseEntity<List<Assessment>> getCommentsByUserIdORMovieId(@RequestParam(value = "userId", required = false) String userId,
                                                         @RequestParam(value = "movieId", required = false) String movieId) {
        if (userId != null) {
            return  ResponseEntity.ok(commentsService.getCommentsByUserId(userId));
        } else if (movieId != null) {
            return  ResponseEntity.ok(commentsService.getCommentsByMovieId(movieId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha especificado ni userId ni movieId");
        }
    }

    @PatchMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("@commentsService.isCommentFromUser(#id, principal)")
    public ResponseEntity<Assessment> patchComment(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        Assessment updatedComment = commentsService.updateComment(id, updates);
        return ResponseEntity.ok(updatedComment);
    }


}
