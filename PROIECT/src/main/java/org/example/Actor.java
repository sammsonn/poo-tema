package org.example;

import java.util.*;

public class Actor implements Comparable, Subject {
    private List<Observer> observers = new ArrayList<>();
    String name;
    Map<String, ProductionType> roles = new LinkedHashMap<String, ProductionType>();
    String bio = new String();
    List<Rating> ratings = new ArrayList<Rating>();
    double ranking;

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Production) {
            Production other = (Production) o;
            return this.name.compareTo(other.title);
        }
        Actor other = (Actor) o;
        return this.name.compareTo(other.name);
    }

    public String displayInfo() {
        StringBuilder result = new StringBuilder();
        if (name != null) {
            result.append("Name: ").append(name).append("\n");
        }
        if (roles != null) {
            for (Map.Entry<String, ProductionType> entry : roles.entrySet()) {
                result.append("Role: ").append(entry.getKey()).append("\n");
                result.append("Production type: ").append(entry.getValue().toString()).append("\n");
            }
        }
        if (bio != null) {
            result.append("Bio: ").append(bio).append("\n");
        }
        if (ratings != null) {
            ratings.sort((Rating r1, Rating r2) -> r2.getUser().experience - r1.getUser().experience);
            for (Rating rating : ratings) {
                result.append("Rated by: ").append(rating.username).append("\n");
                result.append("Rating: ").append(rating.rating).append("\n");
                result.append("Comment: ").append(rating.comment).append("\n");
            }
        }
        return result.toString();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
