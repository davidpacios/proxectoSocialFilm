package gal.usc.etse.grei.es.project.model;

import java.util.Optional;

public class FriendshipWithUser {
    private Friendship friendship;
    private Optional<User> user;

    public FriendshipWithUser(Friendship friendship, Optional<User> user) {
        this.friendship = friendship;
        this.user = user;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public Optional<User> getUser() {
        return user;
    }
}
