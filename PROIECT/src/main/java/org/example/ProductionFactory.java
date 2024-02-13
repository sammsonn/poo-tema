package org.example;

public class ProductionFactory {
    public static Production factory(ProductionType productionType) {
        switch (productionType) {
            case Movie:
                return new Movie();
            case Series:
                return new Series();
            default:
                return null;
        }
    }
}
