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

    public Page<Film> getAllFilms(int page, int size, String sort, String keyword, String genre, String cast, String releaseDate) {
        Pageable request = PageRequest.of(page, size, Sort.by(sort).ascending());
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        if(genre != null){
            //Añadir genre a una lista que solo tenga ese elemento
            List<String> genres = List.of(genre);
            return films.findAll(Example.of(new Film().setGenres(genres), matcher), request);
        }

        if(releaseDate != null){
            //transformar releaseDate a Date
            Date date = new Date();
            String[] parts = releaseDate.split("/");
            date.setDay(Integer.parseInt(parts[0]));
            date.setMonth(Integer.parseInt(parts[1]));
            date.setYear(Integer.parseInt(parts[2]));
            return films.findAll(Example.of(new Film().setReleaseDate(date), matcher), request);
        }

        if(keyword != null){
            //Solo devolver los atributos id, title, overview, genres, releaseDate e resources
            List<String> keywords = List.of(keyword.split(","));
            return films.findAll(Example.of(new Film().setKeywords(keywords), matcher), request);
        }

        //No se puede buscar por cast no funciona
        if(cast != null){
            //Solo devolver los atributos id, title, overview, genres, releaseDate e resources
            List<Cast> castList = List.of((Cast) new Cast().setName(cast));
            return films.findAll(Example.of(new Film().setCast(castList), matcher), request);
        }

        //Solo devolver los atributos id, title, overview, genres, releaseDate e resources
        return films.findAll(request).map( f -> new Film().setId(f.getId()).setTitle(f.getTitle()).setOverview(f.getOverview()).setGenres(f.getGenres()).setReleaseDate(f.getReleaseDate()).setResources(f.getResources()));
    }
}
