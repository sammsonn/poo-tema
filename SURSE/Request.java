package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Request implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private RequestTypes requestType;
    private LocalDateTime date;
    String movieTitle;
    String actorName;
    String description;
    String senderName;
    String recipientName;
    boolean solved = false;
    boolean accepted = false;

    public void setRequestType(RequestTypes requestType) {
        this.requestType = requestType;
    }

    public RequestTypes getRequestType() {
        return requestType;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
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
