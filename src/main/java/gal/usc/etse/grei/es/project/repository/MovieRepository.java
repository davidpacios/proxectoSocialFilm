package gal.usc.etse.grei.es.project.repository;

import gal.usc.etse.grei.es.project.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
}
