package gal.usc.etse.grei.es.project.repository;

import gal.usc.etse.grei.es.project.model.Assessment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository <Assessment, String> {
    List<Assessment> findByUserId(String userId);
    List<Assessment> findByMovieId(String filmId);
}
