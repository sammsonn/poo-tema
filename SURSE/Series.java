package org.example;

import java.util.*;

public class Series extends Production {
    int year;
    int noOfSeasons;
    private Map<String, List<Episode>> seasons = new LinkedHashMap<String, List<Episode>>();

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
        if (year != 0) {
            result.append("Year: ").append(year).append("\n");
        }
        if (noOfSeasons != 0) {
            result.append("Number of seasons: ").append(noOfSeasons).append("\n");
        }
        if (seasons != null) {
            for (Map.Entry<String, List<Episode>> entry : seasons.entrySet()) {
                result.append("Season: ").append(entry.getKey()).append("\n");
                for (Episode episode : entry.getValue()) {
                    result.append("Episode: ").append(episode.name).append("\n");
                    result.append("Duration: ").append(episode.duration).append(" minutes").append("\n");
                }
            }
        }
        if (trailerLink != null) {
            result.append("Trailer link: ").append(trailerLink).append("\n");
        }
        return result.toString();
    }

    public void addSeason(String seasonName, List<Episode> episodes) {
        seasons.put(seasonName, episodes);
    }

    public Map<String, List<Episode>> getSeasons() {
        return seasons;
    }
}
