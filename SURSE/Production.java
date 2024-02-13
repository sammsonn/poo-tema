package org.example;

import java.util.ArrayList;
import java.util.List;

public abstract class Production implements Comparable, Subject {
    private List<Observer> observers = new ArrayList<>();
    String title;
    List<String> directors = new ArrayList<String>();
    List<String> actors = new ArrayList<String>();
    List<Genre> genres = new ArrayList<Genre>();
    List<Rating> ratings = new ArrayList<Rating>();
    String description;
    String trailerLink;
    int year;
    double ranking;

    public String getTitle() {
        return title;
    }

    public double getRanking() {
        return ranking;
    }

    public abstract String displayInfo();

    @Override
    public int compareTo(Object o) {
        if (o instanceof Actor) {
            Actor other = (Actor) o;
            return this.title.compareTo(other.name);
        }
        Production other = (Production) o;
        return this.title.compareTo(other.title);
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
