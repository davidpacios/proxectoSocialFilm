package gal.usc.etse.grei.es.project.service;

import com.github.fge.jsonpatch.JsonPatchException;
import gal.usc.etse.grei.es.project.model.*;
import gal.usc.etse.grei.es.project.model.Date;
import gal.usc.etse.grei.es.project.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.domain.Page.empty;

@Service
public class FilmService {
    private final FilmRepository films;
    private final MongoTemplate mongoTemplate;

    @Autowired
    private PatchUtils patchUtils;


    @Autowired
    public FilmService(FilmRepository films, MongoTemplate mongoTemplate){
        this.films = films;
        this.mongoTemplate = mongoTemplate;

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

        List<String> genres = null;
        List<String> keywords = null;
        Date date = null;

        if (genre != null) {
            genres = Arrays.asList(genre.split(","));
        }

        if (keyword != null) {
            keywords = Arrays.asList(keyword.split(","));
        }


        if (releaseDate != null) {
            date = new Date();
            String[] parts = releaseDate.split("/");
            date.setDay(Integer.parseInt(parts[0]));
            date.setMonth(Integer.parseInt(parts[1]));
            date.setYear(Integer.parseInt(parts[2]));
        }

        Query query = new Query().with(request);

        if (genre != null) {
            query.addCriteria(Criteria.where("genres").all(genres));
        }

        if (keyword != null) {
            query.addCriteria(Criteria.where("keywords").all(keywords));
        }

        if (cast != null) {
            Criteria castCriteria = new Criteria().orOperator(
                    Criteria.where("cast.name").regex(cast),
                    Criteria.where("producers.name").regex(cast),
                    Criteria.where("crew.name").regex(cast)
            );
            query.addCriteria(castCriteria);

        }

        if (releaseDate != null) {
            query.addCriteria(Criteria.where("releaseDate").all(date));
        }


        query.fields().exclude("tagline").exclude("producers").exclude("crew").exclude("cast").exclude("producers").exclude("budget").exclude("status");


        List <Film> filmss = mongoTemplate.find(query,Film.class);
        //https://stackoverflow.com/questions/29030542/pagination-with-mongotemplate
        /*long count = mongoTemplate.count(query, Film.class);
        System.out.println(count);
        Page<Film> result = new PageImpl<Film>(filmss , request, count);

*/


        return PageableExecutionUtils.getPage(
                filmss,
                request,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Film.class));
       /* if (result.isEmpty())
            return empty();

        else return result;*/
    }



}

