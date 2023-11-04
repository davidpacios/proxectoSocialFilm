package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.*;
import gal.usc.etse.grei.es.project.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    public Page<Film> getAllFilms(int page, int size, String sort, String keyword, String genre, String cast, String releaseDate) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        List<Cast> castList = null;
        List<String> genres = null;
        List<String> keywords = null;
        Date date = null;

        if (genre != null) genres = List.of(genre.split(","));
        if (keyword != null) keywords = List.of(keyword.split(","));
        if (cast != null) castList = List.of((Cast) new Cast().setName(cast));
        if (releaseDate != null) {
            date = new Date();
            String[] parts = releaseDate.split("/");
            date.setDay(Integer.parseInt(parts[0]));
            date.setMonth(Integer.parseInt(parts[1]));
            date.setYear(Integer.parseInt(parts[2]));
        }

        if (genre != null && keyword != null && cast != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setKeywords(keywords).setCast(castList).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && keyword != null && cast != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setKeywords(keywords).setCast(castList), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && keyword != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setKeywords(keywords).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && cast != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setCast(castList).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (keyword != null && cast != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setKeywords(keywords).setCast(castList).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && keyword != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setKeywords(keywords), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && cast != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setCast(castList), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setGenres(genres).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (keyword != null && cast != null)
            return films.findAll(Example.of(new Film().setKeywords(keywords).setCast(castList), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (keyword != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setKeywords(keywords).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (cast != null && releaseDate != null)
            return films.findAll(Example.of(new Film().setCast(castList).setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (genre != null)
            return films.findAll(Example.of(new Film().setGenres(genres), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (keyword != null)
            return films.findAll(Example.of(new Film().setKeywords(keywords), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (cast != null)
            return films.findAll(Example.of(new Film().setCast(castList), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        if (releaseDate != null)
            return films.findAll(Example.of(new Film().setReleaseDate(date), matcher), request)
                    .map(f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()));

        return films.findAll(request);
    }
}

