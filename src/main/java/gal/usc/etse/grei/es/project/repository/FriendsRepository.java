package gal.usc.etse.grei.es.project.repository;

import gal.usc.etse.grei.es.project.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface FriendsRepository  extends MongoRepository<Friendship, String> {
    Friendship findByUserAndFriend(String user, String friend);
}

