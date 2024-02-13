package org.example;

public interface StaffInterface {
    public void addProductionSystem(Production p);

    public void addActorSystem(Actor a);

    public void removeProductionSystem(String name);

    public void removeActorSystem(String name);

    public void updateMovie() throws InvalidCommandException;

    public void updateSeries() throws InvalidCommandException;

    public void updateActor() throws InvalidCommandException;

    public void solveRequest(Request r) throws InvalidCommandException;
}
