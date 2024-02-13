package org.example;

public interface ExperienceStrategy {
    public int calculateExperience();
}

class AddRatingExperienceStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        return 10;
    }
}

class AcceptedRequestExperienceStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        return 15;
    }
}

class AddContributionExperienceStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        return 20;
    }
}

class Context {
    private ExperienceStrategy strategy;

    public Context(ExperienceStrategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy() {
        return strategy.calculateExperience();
    }
}
