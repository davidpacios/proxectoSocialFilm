package gal.usc.etse.grei.es.project.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("movies")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService films) {
        this.filmService = films;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Page<Film>> get(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "") List<String> sort
    ) {
        List<Sort.Order> criteria = sort.stream().map(string -> {
                    if (string.startsWith("+")) {
                        return Sort.Order.asc(string.substring(1));
                    } else if (string.startsWith("-")) {
                        return Sort.Order.desc(string.substring(1));
                    } else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.of(filmService.get(page, size, Sort.by(criteria)));
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Film> get(@PathVariable("id") String id) {
        return ResponseEntity.of(filmService.get(id));
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody @Valid Film film) {
        return new ResponseEntity<Film>(filmService.addFilm(film), HttpStatus.CREATED);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable("id") String id) {
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    
    @PatchMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Film> patchFilm(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> updates) throws JsonPatchException {
        Film updatedFilm = filmService.updateFilm(id, updates);
        return ResponseEntity.ok(updatedFilm);
    }



    

}
