package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Film;
import gal.usc.etse.grei.es.project.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmRepository films;
    @Autowired
    private PatchUtils patchUtils;


    @Autowired
    public FilmService(FilmRepository films) {
        this.films = films;
    }

    
    public Optional<Page<Film>> get(int page, int size, Sort sort) {
        Pageable request = PageRequest.of(page, size, sort);
        Page<Film> result = films.findAll(request);

        if (result.isEmpty())
            return Optional.empty();

        else return Optional.of(result);
    }

    public Optional<Film> get(String id) {
        return films.findById(id);
    }


	public Film addFilm(Film film) {
        return films.save(film);
    }


    public void deleteFilm(String id) {
        films.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        films.deleteById(id);
    }

    public Film updateFilm(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        Film film = films.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid film Id:" + id));
        film = patchUtils.patch(film, updates);
        return films.save(film);
    }


}
