package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.Movie;
import gal.usc.etse.grei.es.project.repository.MovieRepository;
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
public class MovieService {
    private final MovieRepository movies;
    @Autowired
    private PatchUtils patchUtils;


    @Autowired
    public MovieService(MovieRepository movies) {
        this.movies = movies;
    }

    
    public Optional<Page<Movie>> get(int page, int size, Sort sort) {
        Pageable request = PageRequest.of(page, size, sort);
        Page<Movie> result = movies.findAll(request);

        if (result.isEmpty())
            return Optional.empty();

        else return Optional.of(result);
    }

    public Optional<Movie> get(String id) {
        return movies.findById(id);
    }


	public Movie addMovie(Movie movie) {
        return movies.save(movie);
    }


    public void deleteMovie(String id) {
        movies.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        movies.deleteById(id);
    }

    public Movie updateMovie(String id, List<Map<String, Object>> updates) throws JsonPatchException {
        Movie movie = movies.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        return patchUtils.patch(movie, updates);
    }


}
