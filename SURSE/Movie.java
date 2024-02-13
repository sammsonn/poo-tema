package org.example;

public class Movie extends Production {
    int duration;
    int year;

    public String displayInfo() {
        StringBuilder result = new StringBuilder();
        if (title != null) {
            result.append("Movie: ").append(title).append("\n");
        }
        if (directors != null) {
            result.append("Directed by: ").append(directors).append("\n");
        }
        if (actors != null) {
            result.append("Actors: ").append(actors).append("\n");
        }
        if (genres != null) {
            result.append("Genres: ").append(genres).append("\n");
        }
        if (ratings != null) {
            ratings.sort((Rating r1, Rating r2) -> r2.getUser().experience - r1.getUser().experience);
            for (Rating rating : ratings) {
                result.append("Rated by: ").append(rating.username).append("\n");
                result.append("Rating: ").append(rating.rating).append("\n");
                result.append("Comment: ").append(rating.comment).append("\n");
            }
        }
        if (description != null) {
            result.append("Description: ").append(description).append("\n");
        }
        if (ranking != 0) {
            result.append("Ranking: ").append(ranking).append("\n");
        }
        if (duration != 0) {
            result.append("Duration: ").append(duration).append(" minutes").append("\n");
        }
        if (year != 0) {
            result.append("Year: ").append(year).append("\n");
        }
        if (trailerLink != null) {
            result.append("Trailer link: ").append(trailerLink).append("\n");
        }
        return result.toString();
    }
}
