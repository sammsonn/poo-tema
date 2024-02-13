package org.example;

public class Rating {
    String username;
    int rating;
    String comment;
    String productionTitle;
    String actorName;

    public User getUser() {
        User user1 = null;
        for (User user : IMDb.getInstance().users) {
            if (user.username.equals(username)) {
                user1 = user;
            }
        }
        return user1;
    }
}
