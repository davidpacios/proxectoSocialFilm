package gal.usc.etse.grei.es.project.repository;

import gal.usc.etse.grei.es.project.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface FriendsRepository  extends MongoRepository<Friendship, String> {
    Friendship findByUserAndFriend(String user, String friend);
    List<Friendship> findByUser(String user);
    List<Friendship> findByFriend(String user);
}

