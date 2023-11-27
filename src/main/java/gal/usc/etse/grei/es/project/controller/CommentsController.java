package gal.usc.etse.grei.es.project.controller;

import javax.validation.Valid;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.http.HttpHeaders;
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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("comments")
@Tag(name = "Comments API", description = "Comments related operations")
public class CommentsController {

    private final CommentsService commentsService;
    private final UserService userService;
    private final LinkRelationProvider relationProvider;

    @Autowired
    public CommentsController(CommentsService commentsService, UserService userService, LinkRelationProvider relationProvider) {
        this.commentsService = commentsService;
        this.userService = userService;
        this.relationProvider = relationProvider;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "getOneComment",
            summary = "Get a single comment details",
            description = "Get the details for a given comment. To see the comment details " +
                    "you must be authenticated."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The comment details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Assessment.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    ResponseEntity<Assessment> get(@PathVariable("id") String id) {

        Optional<Assessment> result = commentsService.get(id);
        if(result.isPresent()) {
            Link self = linkTo(methodOn(CommentsController.class).get(id)).withSelfRel();
            //Link all al recurso /movies
            Link all = linkTo(CommentsController.class).withRel(relationProvider.getCollectionResourceRelFor(Assessment.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "addComment",
            summary = "Add a new comment",
            description = "Add a new comment to the database. " +
                    "Requires authentication"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The comment details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Assessment.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Assessment> addComment(@RequestBody @Valid Assessment comentario) {
        //hateoas
        Optional<Assessment> result = Optional.ofNullable(commentsService.addComment(comentario));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link film = linkTo(methodOn(FilmController.class).get(comentario.getMovie().getId())).withRel("film");
            Link filmComments = linkTo(methodOn(CommentsController.class).getCommentsByUserIdORMovieId("", comentario.getMovie().getId(),0,10,"id")).withRel("filmComments");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.LINK, film.toString())
                    .header(HttpHeaders.LINK, filmComments.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @commentsService.isCommentFromUser(#id, principal)")
    @Operation(
            operationId = "removeComment",
            summary = "Remove a comment",
            description = "Remove a comment from the database. " +
                    "you must be the user owner of the comment or an admin."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The comment was removed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        //hateoas
        Optional<Assessment> result = commentsService.get(id);
        commentsService.deleteComment(id);

        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link userComments = linkTo(methodOn(CommentsController.class).getCommentsByUserIdORMovieId(result.get().getUser().getId(), "",0,10,"id")).withRel("userComments");
            Link filmComments = linkTo(methodOn(CommentsController.class).getCommentsByUserIdORMovieId("", result.get().getMovie().getId(),0,10,"id")).withRel("filmComments");

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, userComments.toString())
                    .header(HttpHeaders.LINK, filmComments.toString())
                    .build();
        }
        return ResponseEntity.notFound().build();
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
    @PreAuthorize("((#movieId != null) and isAuthenticated()) or ((#userId != null) and (hasRole('ADMIN') or #userId == principal or @userService.areFriends(#userId,principal)))")
    @Operation(
            operationId = "getAllCommentsByUserIdORMovieId",
            summary = "Get all comments by userId or movieId",
            description = "Get the details of all comments by userId or movieId." +
                    "you must be authenticated  (if movieId is specified) or be friends with the user, be the user or be an admin (if userId is specified)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The comments details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Assessment.class)
                    )
            ),
            @ApiResponse(
            responseCode = "404",
            description = "Comment not found",
            content = @Content
             ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Page<Assessment>> getCommentsByUserIdORMovieId(
                                                         @RequestParam(value = "userId", required = false) String userId,
                                                         @RequestParam(value = "movieId", required = false) String movieId,
                                                         @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                         @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                         @RequestParam(value = "sort", defaultValue = "id", required = false) String sort) {
        if (userId != null) {
            //hateoas
            Optional<Page<Assessment>> result = Optional.ofNullable(commentsService.getCommentsByUserId(userId,page, size, sort));

            if(result.isPresent()) {
                Page<Assessment> data = result.get();
                Pageable metadata = data.getPageable();

                Link user = linkTo(
                        methodOn(UserController.class).get(userId)
                ).withSelfRel();
                Link first = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId(userId, "", 0, size, sort)
                ).withRel(IanaLinkRelations.FIRST);
                Link last = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId(userId, "", data.getTotalPages() - 1, size, sort)
                ).withRel(IanaLinkRelations.LAST);
                Link next = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId(userId, "", metadata.next().getPageNumber(), size, sort)
                ).withRel(IanaLinkRelations.NEXT);
                Link previous = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId(userId, "", metadata.previousOrFirst().getPageNumber(), size, sort)
                ).withRel(IanaLinkRelations.PREVIOUS);

                return ResponseEntity.ok()
                        .header(HttpHeaders.LINK, user.toString())
                        .header(HttpHeaders.LINK, first.toString())
                        .header(HttpHeaders.LINK, last.toString())
                        .header(HttpHeaders.LINK, next.toString())
                        .header(HttpHeaders.LINK, previous.toString())
                        .body(result.get());
            }
            return ResponseEntity.notFound().build();
        } else if (movieId != null) {
            Optional<Page<Assessment>> result = Optional.ofNullable(commentsService.getCommentsByMovieId(movieId,page, size, sort));

            if(result.isPresent()) {
                Page<Assessment> data = result.get();
                Pageable metadata = data.getPageable();

                Link user = linkTo(
                        methodOn(FilmController.class).get(movieId)
                ).withSelfRel();
                Link first = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId("",movieId, 0, size, sort)
                ).withRel(IanaLinkRelations.FIRST);
                Link last = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId("",movieId, data.getTotalPages() - 1, size, sort)
                ).withRel(IanaLinkRelations.LAST);
                Link next = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId("",movieId, metadata.next().getPageNumber(), size, sort)
                ).withRel(IanaLinkRelations.NEXT);
                Link previous = linkTo(
                        methodOn(CommentsController.class).getCommentsByUserIdORMovieId("",movieId, metadata.previousOrFirst().getPageNumber(), size, sort)
                ).withRel(IanaLinkRelations.PREVIOUS);

                return ResponseEntity.ok()
                        .header(HttpHeaders.LINK, user.toString())
                        .header(HttpHeaders.LINK, first.toString())
                        .header(HttpHeaders.LINK, last.toString())
                        .header(HttpHeaders.LINK, next.toString())
                        .header(HttpHeaders.LINK, previous.toString())
                        .body(result.get());
            }
            return ResponseEntity.notFound().build();
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
    @Operation(
            operationId = "updateComment",
            summary = "Update a comment",
            description = "Update a comment from the database. " +
                    "you must be the user owner of the comment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The comment was updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Assessment.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not enough privileges",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad token",
                    content = @Content
            ),
    })
    public ResponseEntity<Assessment> patchComment(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        //hateoas
        Optional<Assessment> result = Optional.ofNullable(commentsService.updateComment(id, updates));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link self = linkTo(methodOn(CommentsController.class).get(id)).withSelfRel();
            Link filmComments = linkTo(methodOn(CommentsController.class).getCommentsByUserIdORMovieId("", result.get().getMovie().getId(),0,10,"id")).withRel("filmComments");
            Link userComments = linkTo(methodOn(CommentsController.class).getCommentsByUserIdORMovieId(result.get().getUser().getId(), "",0,10,"id")).withRel("userComments");

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, filmComments.toString())
                    .header(HttpHeaders.LINK, userComments.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }


}
