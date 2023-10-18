package gal.usc.etse.grei.es.project.repository;

import gal.usc.etse.grei.es.project.model.Film;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilmRepository extends MongoRepository<Film, String> {
}
