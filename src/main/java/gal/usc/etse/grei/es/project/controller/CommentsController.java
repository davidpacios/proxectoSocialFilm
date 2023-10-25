package gal.usc.etse.grei.es.project.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gal.usc.etse.grei.es.project.model.Assessment;
import gal.usc.etse.grei.es.project.service.CommentsService;

import java.util.List;

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



}
