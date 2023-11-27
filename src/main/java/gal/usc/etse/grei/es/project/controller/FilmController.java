package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.model.User;
import gal.usc.etse.grei.es.project.service.FilmService;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("movies")
@Tag(name = "Movies API", description = "Movies related operations")
public class FilmController {
    private final FilmService filmService;
    private final LinkRelationProvider relationProvider;

    @Autowired
    public FilmController(FilmService films, LinkRelationProvider relationProvider) {
        this.filmService = films;
        this.relationProvider = relationProvider;
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "getOneFilm",
            summary = "Get a single film",
            description = "Get the details of a single film. " +
                    "Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Film details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Film.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Film not found",
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
    ResponseEntity<Film> get(@PathVariable("id") String id) {
        //hateoas
        Optional<Film> result = filmService.get(id);
        if(result.isPresent()) {
            Link self = linkTo(methodOn(FilmController.class).get(id)).withSelfRel();
            //Link all al recurso /movies
            Link all = linkTo(FilmController.class).withRel(relationProvider.getCollectionResourceRelFor(Film.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    //get all films
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "getAllFilms",
            summary = "Get all films",
            description = "Get the details of all films. " +
                    "Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The films details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Film.class)
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
    public ResponseEntity<Page<Film>> getAllFilms(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "sort", defaultValue = "title") String sort,
                                                  @RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "genre", required = false) String genre,
                                                  @RequestParam(value = "credit", required = false) String credit,
                                                  @RequestParam(value = "releaseDate", required = false) String releaseDate) {

        //hateoas
        Optional<Page<Film>> result = Optional.ofNullable(filmService.getAllFilms(page, size, sort, keyword, genre, credit, releaseDate));

        if(result.isPresent()) {
            Page<Film> data = result.get();
            Pageable metadata = data.getPageable();

            Link self = linkTo(
                    methodOn(FilmController.class).getAllFilms(metadata.getPageNumber(), size, sort, keyword, genre, credit, releaseDate)
            ).withSelfRel();
            Link first = linkTo(
                    methodOn(FilmController.class).getAllFilms(0, size, sort, keyword, genre, credit, releaseDate)
            ).withRel(IanaLinkRelations.FIRST);
            Link last = linkTo(
                    methodOn(FilmController.class).getAllFilms(data.getTotalPages() - 1, size, sort, keyword, genre, credit, releaseDate)
            ).withRel(IanaLinkRelations.LAST);
            Link next = linkTo(
                    methodOn(FilmController.class).getAllFilms(metadata.next().getPageNumber(), size, sort, keyword, genre, credit, releaseDate)
            ).withRel(IanaLinkRelations.NEXT);
            Link previous = linkTo(
                    methodOn(FilmController.class).getAllFilms(metadata.previousOrFirst().getPageNumber(), size, sort, keyword, genre, credit, releaseDate)
            ).withRel(IanaLinkRelations.PREVIOUS);

            Link one = linkTo(
                    methodOn(FilmController.class).get(null)
            ).withRel(relationProvider.getItemResourceRelFor(Film.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, first.toString())
                    .header(HttpHeaders.LINK, last.toString())
                    .header(HttpHeaders.LINK, next.toString())
                    .header(HttpHeaders.LINK, previous.toString())
                    .header(HttpHeaders.LINK, one.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            operationId = "addFilm",
            summary = "Add a new film",
            description = "Add a new film to the database. " +
                    "Requires authentication and admin privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The film details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Film.class)
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
    public ResponseEntity<Film> addFilm(@RequestBody @Valid Film film) {
        //hateoas
        Optional<Film> result = Optional.ofNullable(filmService.addFilm(film));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link self = linkTo(methodOn(FilmController.class).get(result.get().getId())).withSelfRel();
            Link all = linkTo(FilmController.class).withRel(relationProvider.getCollectionResourceRelFor(Film.class));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            operationId = "removeFilm",
            summary = "Remove a film",
            description = "Remove a film from the database. " +
                    "Requires authentication and admin privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The film was removed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Film not found",
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
    public ResponseEntity<Void> deleteFilm(@PathVariable("id") String id) {
        //hateoas
        filmService.deleteFilm(id);
        Link all = linkTo(FilmController.class).withRel(relationProvider.getCollectionResourceRelFor(Film.class));

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.LINK, all.toString())
                .build();
    }

    @PatchMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            operationId = "updateFilm",
            summary = "Update a film",
            description = "Update a film from the database. " +
                    "Requires authentication and admin privileges."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The film was updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Film.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Film not found",
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
    public ResponseEntity<Film> patchFilm(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        //hateoas
        Optional<Film> result = Optional.ofNullable(filmService.updateFilm(id, updates));
        //link a la pelicula creada y a la lista de peliculas
        if(result.isPresent()) {
            Link self = linkTo(methodOn(FilmController.class).get(result.get().getId())).withSelfRel();
            Link all = linkTo(FilmController.class).withRel(relationProvider.getCollectionResourceRelFor(Film.class));

            return ResponseEntity.ok()
                    .header(HttpHeaders.LINK, self.toString())
                    .header(HttpHeaders.LINK, all.toString())
                    .body(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}
