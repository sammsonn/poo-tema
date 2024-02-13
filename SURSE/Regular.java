package org.example;

import java.util.*;

public class Regular extends User implements RequestsManager {
    List<Request> requestsSent = new ArrayList<Request>();

    public void createRequest(Request r) {
        if (r.recipientName.equals("ADMIN")) {
            Admin.RequestsHolder.addRequest(r);
            for (User user : IMDb.getInstance().users) {
                if (user.accountType == AccountType.Admin) {
                    r.registerObserver(user);
                }
            }
        } else {
            for (User u : IMDb.getInstance().users) {
                if (u.username.equals(r.recipientName)) {
                    if (u instanceof Staff) {
                        Staff s = (Staff) u;
                        s.requests.add(r);
                    }
                }
            }
        }

        for (User user : IMDb.getInstance().users) {
            if (user.username.equals(r.senderName) || user.username.equals(r.recipientName)) {
                r.registerObserver(user);
            }
        }

        requestsSent.add(r);
        IMDb.getInstance().requests.add(r);

        r.notifyObservers();
    }

    public void removeRequest(Request r) {
        if (r.recipientName.equals("ADMIN")) {
            Admin.RequestsHolder.removeRequest(r);
        } else {
            for (User u : IMDb.getInstance().users) {
                if (u.username.equals(r.recipientName)) {
                    if (u instanceof Staff) {
                        Staff s = (Staff) u;
                        s.requests.remove(r);
                    }
                }
            }
        }
        requestsSent.remove(r);
        IMDb.getInstance().requests.remove(r);
    }

    void addRating() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the production/actor: ");
        String name = scanner.nextLine();
        boolean exists = false;
        boolean rated = false;

        for (Production production : IMDb.getInstance().productions) {
            if (production.title.equalsIgnoreCase(name)) {
                exists = true;
                for (Rating rating : production.ratings) {
                    if (rating.username.equals(this.username)) {
                        rated = true;
                        break;
                    }
                }
                if (rated) {
                    System.out.println("You have already rated this production!");
                    return;
                }
                System.out.print("Enter the grade: ");
                int grade;
                if (scanner.hasNextInt()) {
                    grade = scanner.nextInt();
                    if (grade < 1 || grade > 10) {
                        throw new InvalidCommandException("The grade must be between 1 and 10!");
                    }
                } else {
                    throw new InvalidCommandException("The grade must be an integer!");
                }
                scanner.nextLine();
                System.out.print("Enter the comment: ");
                String comment = scanner.nextLine();

                Rating rating = new Rating();
                rating.username = this.username;
                rating.rating = grade;
                rating.comment = comment;
                rating.productionTitle = production.title;

                production.ratings.add(rating);
                this.ratingsGiven.add(rating);

                int sum = 0, count = 0;
                for (Rating r : production.ratings) {
                    sum += r.rating;
                    count++;
                }
                if (count != 0) {
                    production.ranking = (double) sum / count;
                } else {
                    production.ranking = 0;
                }

                boolean alreadyRated = false;
                for (Object r : this.ratingsHistory) {
                    Rating r1 = (Rating) r;
                    if (r1.productionTitle != null && r1.productionTitle.equalsIgnoreCase(name)) {
                        alreadyRated = true;
                        break;
                    }
                }
                this.ratingsHistory.add(rating);

                if (!alreadyRated) {
                    Context context = new Context(new AddRatingExperienceStrategy());
                    this.updateExperience(context.executeStrategy());
                }

                production.notifyObservers();
                production.registerObserver(this);
            }
        }

        for (Actor actor : IMDb.getInstance().actors) {
            if (actor.name.equalsIgnoreCase(name)) {
                exists = true;
                for (Rating rating : actor.ratings) {
                    if (rating.username.equals(this.username)) {
                        rated = true;
                        break;
                    }
                }
                if (rated) {
                    System.out.println("You have already rated this actor!");
                    return;
                }
                System.out.print("Enter the grade: ");
                int grade;
                if (scanner.hasNextInt()) {
                    grade = scanner.nextInt();
                    if (grade < 1 || grade > 10) {
                        throw new InvalidCommandException("The grade must be between 1 and 10!");
                    }
                } else {
                    throw new InvalidCommandException("The grade must be an integer!");
                }
                scanner.nextLine();
                System.out.print("Enter the comment: ");
                String comment = scanner.nextLine();

                Rating rating = new Rating();
                rating.username = this.username;
                rating.rating = grade;
                rating.comment = comment;
                rating.actorName = actor.name;

                actor.ratings.add(rating);
                this.ratingsGiven.add(rating);

                int sum = 0, count = 0;
                for (Rating r : actor.ratings) {
                    sum += r.rating;
                    count++;
                }
                if (count != 0) {
                    actor.ranking = (double) sum / count;
                } else {
                    actor.ranking = 0;
                }

                boolean alreadyRated = false;
                for (Object r : this.ratingsHistory) {
                    Rating r1 = (Rating) r;
                    if (r1.actorName != null && r1.actorName.equalsIgnoreCase(name)) {
                        alreadyRated = true;
                        break;
                    }
                }
                this.ratingsHistory.add(rating);

                if (!alreadyRated) {
                    Context context = new Context(new AddRatingExperienceStrategy());
                    this.updateExperience(context.executeStrategy());
                }

                actor.notifyObservers();
                actor.registerObserver(this);
            }
        }

        if (!exists) {
            System.out.println("The name you entered is not in the system!");
        }
    }
}
