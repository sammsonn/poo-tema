package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class IMDb {
    private static IMDb instance = null;
    List<User> users = new ArrayList<User>();
    List<Actor> actors = new ArrayList<Actor>();
    List<Request> requests = new ArrayList<Request>();
    List<Production> productions = new ArrayList<Production>();

    private IMDb() {
    }

    public static IMDb getInstance() {
        if (instance == null) {
            instance = new IMDb();
        }
        return instance;
    }

    private static void parseActorObject(JSONObject actor) {
        String name = (String) actor.get("name");
        String biography = (String) actor.get("biography");

        Actor actorObject = new Actor();
        actorObject.name = name;
        actorObject.bio = biography;

        JSONArray performances = (JSONArray) actor.get("performances");
        if (performances != null) {
            for (Object performance : performances) {
                JSONObject performanceObject = (JSONObject) performance;
                String title = (String) performanceObject.get("title");
                String type = (String) performanceObject.get("type");

                actorObject.roles.put(title, ProductionType.valueOf(type));
            }
        }

        IMDb.getInstance().actors.add(actorObject);
    }

    private static void parseProductionObject(JSONObject production) {
        String title = (String) production.get("title");
        String type = (String) production.get("type");

        JSONArray directors = (JSONArray) production.get("directors");
        JSONArray actors = (JSONArray) production.get("actors");
        JSONArray genres = (JSONArray) production.get("genres");
        JSONArray ratings = (JSONArray) production.get("ratings");

        String plot = (String) production.get("plot");
        double averageRating = (double) production.get("averageRating");
        String duration = (String) production.get("duration");

        long releaseYear;
        if (production.get("releaseYear") != null) {
            releaseYear = (long) production.get("releaseYear");
        } else {
            releaseYear = 0;
        }

        long numberOfSeasons;
        if (production.get("numSeasons") != null) {
            numberOfSeasons = (long) production.get("numSeasons");
        } else {
            numberOfSeasons = 0;
        }

        Production productionObject = ProductionFactory.factory(ProductionType.valueOf(type));

        productionObject.title = title;

        if (directors != null) {
            for (Object director : directors) {
                String directorName = (String) director;
                productionObject.directors.add(directorName);
            }
        }

        if (actors != null) {
            for (Object actor : actors) {
                String actorName = (String) actor;
                productionObject.actors.add(actorName);

                boolean exists = false;
                for (Actor a : IMDb.getInstance().actors) {
                    if (a.name.equals(actorName)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    Actor actorObject = new Actor();
                    actorObject.name = actorName;
                    actorObject.roles.put(title, ProductionType.valueOf(type));
                    IMDb.getInstance().actors.add(actorObject);
                    Admin.ContributionsHolder.addContribution(actorObject);
                }
            }
        }

        if (genres != null) {
            for (Object genre : genres) {
                String genreName = (String) genre;
                productionObject.genres.add(Genre.valueOf(genreName));
            }
        }

        if (ratings != null) {
            for (Object rating : ratings) {
                JSONObject ratingObject = (JSONObject) rating;

                String username = (String) ratingObject.get("username");
                long grade = (long) ratingObject.get("rating");
                String comment = (String) ratingObject.get("comment");

                Rating ratingObject1 = new Rating();
                ratingObject1.username = username;
                ratingObject1.rating = (int) grade;
                ratingObject1.comment = comment;
                ratingObject1.productionTitle = title;

                productionObject.ratings.add(ratingObject1);
            }
        }

        productionObject.description = plot;
        productionObject.ranking = averageRating;

        String[] words = productionObject.title.split(" ");
        String trailerLink = "https://www.youtube.com/results?search_query=";
        for (String word : words) {
            trailerLink += word + "+";
        }
        trailerLink += "trailer";
        productionObject.trailerLink = trailerLink;

        if (duration != null) {
            Movie movie = (Movie) productionObject;
            String[] parts = duration.split(" ");
            movie.duration = Integer.parseInt(parts[0]);
            movie.year = (int) releaseYear;
            productionObject.year = (int) releaseYear;
        } else {
            Series series = (Series) productionObject;
            series.year = (int) releaseYear;
            productionObject.year = (int) releaseYear;
            series.noOfSeasons = (int) numberOfSeasons;
        }

        JSONObject seasons = (JSONObject) production.get("seasons");
        if (seasons != null) {
            for (Object season : seasons.keySet()) {
                String seasonName = (String) season;

                JSONArray episodes = (JSONArray) seasons.get(seasonName);

                List<Episode> episodesList = new ArrayList<Episode>();

                for (Object episode : episodes) {
                    JSONObject episodeObject = (JSONObject) episode;

                    String episodeName = (String) episodeObject.get("episodeName");
                    String episodeDuration = (String) episodeObject.get("duration");

                    Episode episode1 = new Episode();
                    episode1.name = episodeName;
                    String[] parts = episodeDuration.split(" ");
                    episode1.duration = Integer.parseInt(parts[0]);

                    episodesList.add(episode1);
                }

                Series series = (Series) productionObject;
                series.addSeason(seasonName, episodesList);
            }
        }

        IMDb.getInstance().productions.add(productionObject);
    }

    private static void parseUserObject(JSONObject user) {
        String username = (String) user.get("username");
        String experience = (String) user.get("experience");

        JSONObject information = (JSONObject) user.get("information");
        JSONObject credentials = (JSONObject) information.get("credentials");

        String email = (String) credentials.get("email");
        String password = (String) credentials.get("password");
        String name = (String) information.get("name");
        String country = (String) information.get("country");
        long age = (long) information.get("age");
        String gender = (String) information.get("gender");

        String birthDate = (String) information.get("birthDate");
        LocalDate date = LocalDate.parse(birthDate);
        LocalDateTime birthday = date.atStartOfDay();

        String userType = (String) user.get("userType");

        JSONArray productionsContribution = (JSONArray) user.get("productionsContribution");
        JSONArray actorsContribution = (JSONArray) user.get("actorsContribution");
        JSONArray favoriteProductions = (JSONArray) user.get("favoriteProductions");
        JSONArray favoriteActors = (JSONArray) user.get("favoriteActors");
        JSONArray notifications = (JSONArray) user.get("notifications");

        User.Information info = null;
        try {
            info = new User.Information.InformationBuilder(email, password)
                    .name(name)
                    .country(country)
                    .age(age)
                    .gender(gender)
                    .birthday(birthday)
                    .build();
        } catch (InformationIncompleteException e) {
            System.out.println(e.getMessage());
        }

        User userObject = UserFactory.factory(AccountType.valueOf(userType));

        userObject.username = username;
        userObject.info = info;
        userObject.accountType = AccountType.valueOf(userType);

        if (userObject.accountType == AccountType.Admin) {
            userObject.experience = 999;
        } else {
            if (experience != null) {
                userObject.experience = Integer.parseInt(experience);
            } else {
                userObject.experience = 0;
            }
        }

        if (productionsContribution != null) {
            Staff staff = (Staff) userObject;
            for (Object productionContribution : productionsContribution) {
                String productionTitle = (String) productionContribution;
                for (Production production : IMDb.getInstance().productions) {
                    if (production.title.equals(productionTitle)) {
                        staff.contributions.add(production);
                        production.registerObserver(staff);
                    }
                }
            }
        }

        if (actorsContribution != null) {
            Staff staff = (Staff) userObject;
            for (Object actorContribution : actorsContribution) {
                String actorName = (String) actorContribution;
                for (Actor actor : IMDb.getInstance().actors) {
                    if (actor.name.equals(actorName)) {
                        staff.contributions.add(actor);
                        actor.registerObserver(staff);
                    }
                }
            }
        }

        if (userObject instanceof Admin) {
            Admin admin = (Admin) userObject;
            for (Object contribution : Admin.ContributionsHolder.contributions) {
                if (contribution instanceof Actor) {
                    Actor actor = (Actor) contribution;
                    actor.registerObserver(admin);
                }
                else if (contribution instanceof Production) {
                    Production production = (Production) contribution;
                    production.registerObserver(admin);
                }
            }
        }

        if (favoriteProductions != null) {
            for (Object favoriteProduction : favoriteProductions) {
                String productionTitle = (String) favoriteProduction;
                for (Production production : IMDb.getInstance().productions) {
                    if (production.title.equals(productionTitle)) {
                        userObject.favorites.add(production);
                    }
                }
            }
        }

        if (favoriteActors != null) {
            for (Object favoriteActor : favoriteActors) {
                String actorName = (String) favoriteActor;
                for (Actor actor : IMDb.getInstance().actors) {
                    if (actor.name.equals(actorName)) {
                        userObject.favorites.add(actor);
                    }
                }
            }
        }

        if (notifications != null) {
            for (Object notification : notifications) {
                String notif = (String) notification;
                userObject.notifications.add(notif);
            }
        }

        for (Production p : IMDb.getInstance().productions) {
            for (Rating r : p.ratings) {
                if (r.username.equals(userObject.username)) {
                    userObject.ratingsGiven.add(r);
                    userObject.ratingsHistory.add(r);
                    p.registerObserver(userObject);
                }
            }
        }

        IMDb.getInstance().users.add(userObject);
    }

    private static void parseRequestObject(JSONObject request) {
        String type = (String) request.get("type");
        String createdDate = (String) request.get("createdDate");
        LocalDateTime date = LocalDateTime.parse(createdDate);
        String username = (String) request.get("username");
        String actorName = (String) request.get("actorName");
        String to = (String) request.get("to");
        String description = (String) request.get("description");
        String movieTitle = (String) request.get("movieTitle");

        Request requestObject = new Request();

        requestObject.setRequestType(RequestTypes.valueOf(type));
        requestObject.setDate(date);
        requestObject.senderName = username;

        if (actorName != null) {
            requestObject.actorName = actorName;
        } else if (movieTitle != null) {
            requestObject.movieTitle = movieTitle;
        }

        requestObject.recipientName = to;
        requestObject.description = description;

        if (requestObject.recipientName.equals("ADMIN")) {
            Admin.RequestsHolder.addRequest(requestObject);
            for (User user : IMDb.getInstance().users) {
                if (user.accountType == AccountType.Admin) {
                    requestObject.registerObserver(user);
                }
            }
        } else {
            for (User user : IMDb.getInstance().users) {
                if (user.username.equals(requestObject.recipientName)) {
                    Staff staff = (Staff) user;
                    staff.requests.add(requestObject);
                }
            }
        }

        for (User user : IMDb.getInstance().users) {
            if (user.username.equals(requestObject.senderName)) {
                if (user instanceof Contributor) {
                    Contributor c = (Contributor) user;
                    c.requestsSent.add(requestObject);
                } else if (user instanceof Regular) {
                    Regular r = (Regular) user;
                    r.requestsSent.add(requestObject);
                }
                requestObject.registerObserver(user);
            }
            if (user.username.equals(requestObject.recipientName)) {
                requestObject.registerObserver(user);
            }
        }

        IMDb.getInstance().requests.add(requestObject);
        requestObject.notifyObservers();
    }

    void printProductions() throws InvalidCommandException {
        System.out.println("Choose an option: ");
        System.out.println("    1) View unfiltered productions");
        System.out.println("    2) View productions filtered by genre");
        System.out.println("    3) View productions filtered by number of ratings");
        System.out.println("    4) View productions filtered by year");
        System.out.println("    5) View productions filtered by director");
        System.out.println("    6) View productions filtered by actor");
        System.out.println("    7) View productions sorted by ranking");
        System.out.println("    8) View productions sorted by title");

        Scanner scanner = new Scanner(System.in);
        int option;
        if (scanner.hasNextInt()) {
            option = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 8.");
        }
        scanner.nextLine();

        switch (option) {
            case 1:
                for (Production production : productions) {
                    if (production instanceof Movie) {
                        Movie movie = (Movie) production;
                        System.out.println(movie.displayInfo() + "\n");
                    } else if (production instanceof Series) {
                        Series series = (Series) production;
                        System.out.println(series.displayInfo() + "\n");
                    }
                }
                break;
            case 2:
                System.out.println("Choose a genre: ");
                System.out.println("    1) Action");
                System.out.println("    2) Adventure");
                System.out.println("    3) Comedy");
                System.out.println("    4) Drama");
                System.out.println("    5) Horror");
                System.out.println("    6) SF");
                System.out.println("    7) Fantasy");
                System.out.println("    8) Romance");
                System.out.println("    9) Mystery");
                System.out.println("    10) Thriller");
                System.out.println("    11) Crime");
                System.out.println("    12) Biography");
                System.out.println("    13) War");
                System.out.println("    14) Cooking");
                int genre;
                if (scanner.hasNextInt()) {
                    genre = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");
                }
                scanner.nextLine();

                switch (genre) {
                    case 1:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Action)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 2:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Adventure)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 3:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Comedy)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 4:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Drama)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 5:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Horror)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 6:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.SF)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 7:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Fantasy)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 8:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Romance)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 9:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Mystery)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                    case 10:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Thriller)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 11:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Crime)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 12:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Biography)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 13:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.War)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    case 14:
                        for (Production production : productions) {
                            if (production.genres.contains(Genre.Cooking)) {
                                if (production instanceof Movie) {
                                    Movie movie = (Movie) production;
                                    System.out.println(movie.displayInfo() + "\n");
                                } else if (production instanceof Series) {
                                    Series series = (Series) production;
                                    System.out.println(series.displayInfo() + "\n");
                                }
                            }
                        }
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");

                }
                break;
            case 3:
                System.out.print("Enter the minimum number of ratings: ");
                int min;
                if (scanner.hasNextInt()) {
                    min = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");
                }
                scanner.nextLine();
                System.out.print("Enter the maximum number of ratings: ");
                int max;
                if (scanner.hasNextInt()) {
                    max = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");
                }
                scanner.nextLine();

                for (Production production : productions) {
                    if (production.ratings.size() >= min && production.ratings.size() <= max) {
                        if (production instanceof Movie) {
                            Movie movie = (Movie) production;
                            System.out.println(movie.displayInfo() + "\n");
                        } else if (production instanceof Series) {
                            Series series = (Series) production;
                            System.out.println(series.displayInfo() + "\n");
                        }
                    }
                }
                break;
            case 4:
                System.out.print("Enter the minimum year: ");
                int minYear;
                if (scanner.hasNextInt()) {
                    minYear = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");
                }
                scanner.nextLine();
                System.out.print("Enter the maximum year: ");
                int maxYear;
                if (scanner.hasNextInt()) {
                    maxYear = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 14.");
                }
                scanner.nextLine();

                boolean exists = false;
                for (Production production : productions) {
                    if (production instanceof Movie) {
                        Movie movie = (Movie) production;
                        if (movie.year >= minYear && movie.year <= maxYear) {
                            exists = true;
                            System.out.println(movie.displayInfo() + "\n");
                        }
                    } else if (production instanceof Series) {
                        Series series = (Series) production;
                        if (series.year >= minYear && series.year <= maxYear) {
                            exists = true;
                            System.out.println(series.displayInfo() + "\n");
                        }
                    }
                }

                if (!exists) {
                    System.out.println("There are no productions in the given interval!");
                }
                break;
            case 5:
                System.out.print("Enter the director: ");
                String director = scanner.nextLine();
                boolean exists1 = false;

                for (Production production : productions) {
                    if (production.directors.contains(director)) {
                        if (production instanceof Movie) {
                            exists1 = true;
                            Movie movie = (Movie) production;
                            System.out.println(movie.displayInfo() + "\n");
                        } else if (production instanceof Series) {
                            exists1 = true;
                            Series series = (Series) production;
                            System.out.println(series.displayInfo() + "\n");
                        }
                    }
                }

                if (!exists1) {
                    System.out.println("The name you entered is not in the system!");
                }
                break;
            case 6:
                System.out.print("Enter the actor: ");
                String actor = scanner.nextLine();
                boolean exists2 = false;

                for (Production production : productions) {
                    if (production.actors.contains(actor)) {
                        if (production instanceof Movie) {
                            exists2 = true;
                            Movie movie = (Movie) production;
                            System.out.println(movie.displayInfo() + "\n");
                        } else if (production instanceof Series) {
                            exists2 = true;
                            Series series = (Series) production;
                            System.out.println(series.displayInfo() + "\n");
                        }
                    }
                }

                if (!exists2) {
                    System.out.println("The name you entered is not in the system!");
                }
                break;
            case 7:
                Collections.sort(productions, new Comparator<Production>() {
                    @Override
                    public int compare(Production o1, Production o2) {
                        return Double.compare(o2.ranking, o1.ranking);
                    }
                });
                for (Production production : productions) {
                    if (production instanceof Movie) {
                        Movie movie = (Movie) production;
                        System.out.println(movie.displayInfo() + "\n");
                    } else if (production instanceof Series) {
                        Series series = (Series) production;
                        System.out.println(series.displayInfo() + "\n");
                    }
                }
                break;
            case 8:
                Collections.sort(productions, new Comparator<Production>() {
                    @Override
                    public int compare(Production o1, Production o2) {
                        return o1.title.compareTo(o2.title);
                    }
                });
                for (Production production : productions) {
                    if (production instanceof Movie) {
                        Movie movie = (Movie) production;
                        System.out.println(movie.displayInfo() + "\n");
                    } else if (production instanceof Series) {
                        Series series = (Series) production;
                        System.out.println(series.displayInfo() + "\n");
                    }
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 8.");
        }
    }

    void printActors() throws InvalidCommandException {
        System.out.println("Choose an option: ");
        System.out.println("    1) View unsorted actors");
        System.out.println("    2) View actors sorted by name");
        System.out.println("    3) View actors filtered by productions they played in");

        Scanner scanner = new Scanner(System.in);
        int option;
        if (scanner.hasNextInt()) {
            option = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
        }
        scanner.nextLine();

        switch (option) {
            case 1:
                for (Actor actor : actors) {
                    System.out.println(actor.displayInfo() + "\n");
                }
                break;
            case 2:
                Collections.sort(actors, new Comparator<Actor>() {
                    @Override
                    public int compare(Actor o1, Actor o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                for (Actor actor : actors) {
                    System.out.println(actor.displayInfo() + "\n");
                }
                break;
            case 3:
                System.out.print("Enter the name of the production: ");
                String productionName = scanner.nextLine();
                boolean exists = false;

                for (Actor actor : actors) {
                    if (actor.roles.containsKey(productionName)) {
                        exists = true;
                        System.out.println(actor.displayInfo() + "\n");
                    }
                }

                if (!exists) {
                    System.out.println("The name you entered is not in the system!");
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
        }
    }

    void printNotifications(User u) {
        if (!u.notifications.isEmpty()) {
            for (Object notification : u.notifications) {
                String notification1 = (String) notification;
                System.out.println(notification1);
            }
        } else {
            System.out.println("You don't have any notifications!");
        }
        System.out.println();
    }

    void search() {
        System.out.print("Enter the name of the actor/movie/series: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        boolean exists = false;

        for (Production production : productions) {
            if (production.title.equalsIgnoreCase(name)) {
                exists = true;
                if (production instanceof Movie) {
                    Movie movie = (Movie) production;
                    System.out.println(movie.displayInfo() + "\n");
                } else if (production instanceof Series) {
                    Series series = (Series) production;
                    System.out.println(series.displayInfo() + "\n");
                }
            }
        }

        for (Actor actor : actors) {
            if (actor.name.equalsIgnoreCase(name)) {
                exists = true;
                System.out.println(actor.displayInfo() + "\n");
            }
        }

        if (!exists) {
            System.out.println("The name you entered is not in the system!");
        }
    }

    void modifyFavorites(User u) throws InvalidCommandException {
        System.out.println("What do you want to do:");
        System.out.println("    1) Add");
        System.out.println("    2) Delete");

        Scanner scanner = new Scanner(System.in);
        int action;
        if (scanner.hasNextInt()) {
            action = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();


        switch (action) {
            case 1:
                System.out.print("Enter the name of the actor/movie/series: ");
                String name = scanner.nextLine();
                boolean exists = false;
                boolean alreadyFavorite = false;

                for (Object favorite : u.favorites) {
                    if (favorite instanceof Actor && ((Actor) favorite).name.equalsIgnoreCase(name)) {
                        alreadyFavorite = true;
                        break;
                    } else if (favorite instanceof Movie && ((Movie) favorite).title.equalsIgnoreCase(name)) {
                        alreadyFavorite = true;
                        break;
                    } else if (favorite instanceof Series && ((Series) favorite).title.equalsIgnoreCase(name)) {
                        alreadyFavorite = true;
                        break;
                    }
                }

                if (!alreadyFavorite) {
                    for (Production production : productions) {
                        if (production.title.equalsIgnoreCase(name)) {
                            exists = true;
                            u.addFavorite(production);
                        }
                    }

                    for (Actor actor : actors) {
                        if (actor.name.equalsIgnoreCase(name)) {
                            exists = true;
                            u.addFavorite(actor);
                        }
                    }

                    if (!exists) {
                        System.out.println("The name you entered is not in the system!");
                    }
                } else {
                    System.out.println("The given favorite is already in the list.");
                }
                break;
            case 2:
                if (u.favorites.isEmpty()) {
                    System.out.println("You don't have any favorite actor/production!");
                    return;
                } else {
                    Map<Integer, Object> favorites = new HashMap<Integer, Object>();
                    int i = 0;

                    System.out.println("Choose the favorite you want to delete:");
                    for (Object favorite : u.favorites) {
                        if (favorite instanceof Production) {
                            Production p = (Production) favorite;
                            i++;
                            if (p instanceof Movie) {
                                Movie m = (Movie) p;
                                System.out.println("    " + i + ") " + "Movie: " + m.title);
                            } else if (p instanceof Series) {
                                Series s = (Series) p;
                                System.out.println("    " + i + ") " + "Series: " + s.title);
                            }
                            favorites.put(i, p);
                        } else if (favorite instanceof Actor) {
                            Actor a = (Actor) favorite;
                            i++;
                            System.out.println("    " + i + ") " + "Actor: " + a.name);
                            favorites.put(i, a);
                        }
                    }

                    int index;
                    if (scanner.hasNextInt()) {
                        index = scanner.nextInt();
                    } else {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                    }
                    scanner.nextLine();

                    if (index > i || index < 1) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                    } else {
                        if (favorites.get(index) instanceof Production) {
                            Production p = (Production) favorites.get(index);
                            u.removeFavorite(p);
                        } else if (favorites.get(index) instanceof Actor) {
                            Actor a = (Actor) favorites.get(index);
                            u.removeFavorite(a);
                        }
                    }
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    void accountRequest(User u) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the description: ");
        String description = scanner.nextLine();

        Request r = new Request();
        r.setRequestType(RequestTypes.DELETE_ACCOUNT);
        r.setDate(LocalDateTime.now());
        r.senderName = u.username;
        r.recipientName = "ADMIN";
        r.description = description;

        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            c.createRequest(r);
        } else if (u instanceof Regular) {
            Regular r1 = (Regular) u;
            r1.createRequest(r);
        }
    }

    void actorRequest(User u) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an actor: ");

        Map<Integer, Actor> actorsMap = new HashMap<Integer, Actor>();
        int i = 0;
        for (Actor actor : actors) {
            boolean contributedTo = false;
            if (u instanceof Contributor) {
                Contributor c = (Contributor) u;
                for (Object t : c.contributions) {
                    if (t instanceof Actor) {
                        Actor a = (Actor) t;
                        if (a.name.equals(actor.name)) {
                            contributedTo = true;
                        }
                    }
                }
            }
            if (!contributedTo) {
                i++;
                System.out.println("    " + i + ") " + actor.name);
                actorsMap.put(i, actor);
            }
        }

        int index;
        if (scanner.hasNextInt()) {
            index = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
        }
        scanner.nextLine();

        if (index > i || index < 1) {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
        }

        System.out.print("Enter the description: ");
        String description = scanner.nextLine();

        Request r = new Request();
        r.setRequestType(RequestTypes.ACTOR_ISSUE);
        r.setDate(LocalDateTime.now());
        r.senderName = u.username;
        r.actorName = actorsMap.get(index).name;
        r.description = description;

        for (User user : users) {
            if (user instanceof Staff) {
                Staff s = (Staff) user;
                for (Object t : s.contributions) {
                    if (t instanceof Actor) {
                        Actor a = (Actor) t;
                        if (a.name.equals(actorsMap.get(index).name)) {
                            r.recipientName = s.username;
                            break;
                        }
                    }
                }
            }
        }

        for (Object t : Admin.ContributionsHolder.contributions) {
            if (t instanceof Actor) {
                Actor a = (Actor) t;
                if (a.name.equals(actorsMap.get(index).name)) {
                    r.recipientName = "ADMIN";
                    break;
                }
            }
        }

        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            c.createRequest(r);
        } else if (u instanceof Regular) {
            Regular r1 = (Regular) u;
            r1.createRequest(r);
        }
    }

    void movieRequest(User u) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a production: ");

        Map<Integer, Production> productionsMap = new HashMap<Integer, Production>();
        int i = 0;
        for (Production production : productions) {
            boolean contributedTo = false;
            if (u instanceof Contributor) {
                Contributor c = (Contributor) u;
                for (Object t : c.contributions) {
                    if (t instanceof Production) {
                        Production p = (Production) t;
                        if (p.title.equals(production.title)) {
                            contributedTo = true;
                        }
                    }
                }
            }
            if (!contributedTo) {
                i++;
                if (production instanceof Movie) {
                    Movie movie = (Movie) production;
                    System.out.println("    " + i + ") " + movie.title);
                } else if (production instanceof Series) {
                    Series series = (Series) production;
                    System.out.println("    " + i + ") " + series.title);
                }
                productionsMap.put(i, production);
            }
        }

        int index;
        if (scanner.hasNextInt()) {
            index = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
        }
        scanner.nextLine();

        if (index > i || index < 1) {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
        }

        System.out.print("Enter the description: ");
        String description = scanner.nextLine();

        Request r = new Request();
        r.setRequestType(RequestTypes.MOVIE_ISSUE);
        r.setDate(LocalDateTime.now());
        r.senderName = u.username;
        r.movieTitle = productionsMap.get(index).title;
        r.description = description;

        for (User user : users) {
            if (user instanceof Staff) {
                Staff s = (Staff) user;
                for (Object t : s.contributions) {
                    if (t instanceof Production) {
                        Production p = (Production) t;
                        if (p.title.equals(productionsMap.get(index).title)) {
                            r.recipientName = s.username;
                            break;
                        }
                    }
                }
            }
        }

        for (Object t : Admin.ContributionsHolder.contributions) {
            if (t instanceof Production) {
                Production p = (Production) t;
                if (p.title.equals(productionsMap.get(index).title)) {
                    r.recipientName = "ADMIN";
                    break;
                }
            }
        }

        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            c.createRequest(r);
        } else if (u instanceof Regular) {
            Regular r1 = (Regular) u;
            r1.createRequest(r);
        }
    }

    void otherRequest(User u) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the description: ");
        String description = scanner.nextLine();

        Request r = new Request();
        r.setRequestType(RequestTypes.OTHERS);
        r.setDate(LocalDateTime.now());
        r.senderName = u.username;
        r.recipientName = "ADMIN";
        r.description = description;

        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            c.createRequest(r);
        } else if (u instanceof Regular) {
            Regular r1 = (Regular) u;
            r1.createRequest(r);
        }
    }

    void delRequest(User u) throws InvalidCommandException {
        Map<Integer, Request> requests = new HashMap<Integer, Request>();
        int i = 0;

        if (u instanceof Regular) {
            Regular r = (Regular) u;

            if (r.requestsSent.isEmpty()) {
                System.out.println("You don't have any active requests!");
                return;
            } else {
                System.out.println("Choose the request you want to delete:");
                for (Request request : r.requestsSent) {
                    i++;
                    System.out.println("    " + i + ") " + request.getRequestType() + ": " + request.description);
                    requests.put(i, request);
                }

                Scanner scanner = new Scanner(System.in);
                int index;
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    r.removeRequest(requests.get(index));
                }
            }
        } else if (u instanceof Contributor) {
            Contributor c = (Contributor) u;

            if (c.requestsSent.isEmpty()) {
                System.out.println("You don't have any requests!");
                return;
            } else {
                System.out.println("Choose the request you want to delete:");
                for (Request request : c.requestsSent) {
                    i++;
                    System.out.println("    " + i + ") " + request.getRequestType() + ": " + request.description);
                    requests.put(i, request);
                }

                Scanner scanner = new Scanner(System.in);
                int index;
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    c.removeRequest(requests.get(index));
                }
            }
        }
    }

    void modifyRequests(User u) throws InvalidCommandException {
        System.out.println("What do you want to do:");
        System.out.println("    1) Create");
        System.out.println("    2) Withdraw");

        Scanner scanner = new Scanner(System.in);
        int action;
        if (scanner.hasNextInt()) {
            action = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        switch (action) {
            case 1:
                System.out.println("What type of request do you want to create:");
                System.out.println("    1) Delete account");
                System.out.println("    2) Actor issue");
                System.out.println("    3) Movie issue");
                System.out.println("    4) Other");
                int requestType;
                if (scanner.hasNextInt()) {
                    requestType = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 4.");
                }
                scanner.nextLine();

                switch (requestType) {
                    case 1:
                        accountRequest(u);
                        break;
                    case 2:
                        actorRequest(u);
                        break;
                    case 3:
                        movieRequest(u);
                        break;
                    case 4:
                        otherRequest(u);
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 4.");
                }
                break;
            case 2:
                delRequest(u);
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    void modifyRatings(User u) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to do:");
        System.out.println("    1) Add");
        System.out.println("    2) Delete");

        int action;
        if (scanner.hasNextInt()) {
            action = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        switch (action) {
            case 1:
                Regular r1 = (Regular) u;
                r1.addRating();
                break;
            case 2:
                if (u.ratingsGiven.isEmpty()) {
                    System.out.println("You haven't given any ratings!");
                    return;
                } else {
                    Map<Integer, Rating> ratings = new HashMap<Integer, Rating>();
                    int i = 0;

                    System.out.println("Choose the rating you want to delete:");
                    for (Object rating : u.ratingsGiven) {
                        Rating r = (Rating) rating;
                        i++;
                        if (r.productionTitle != null) {
                            System.out.println("    " + i + ") " + r.productionTitle + ": " + r.rating + " (" + r.comment + ")");
                        } else if (r.actorName != null) {
                            System.out.println("    " + i + ") " + r.actorName + ": " + r.rating + " (" + r.comment + ")");
                        }
                        ratings.put(i, r);
                    }

                    int index;
                    if (scanner.hasNextInt()) {
                        index = scanner.nextInt();
                    } else {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                    }
                    scanner.nextLine();

                    if (index > i || index < 1) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                    } else {
                        u.ratingsGiven.remove(ratings.get(index));
                        for (Production p : productions) {
                            if (p.title.equals(ratings.get(index).productionTitle)) {
                                p.ratings.remove(ratings.get(index));
                                p.removeObserver(u);
                                int sum = 0, count = 0;
                                for (Rating r : p.ratings) {
                                    sum += r.rating;
                                    count++;
                                }
                                if (count != 0) {
                                    p.ranking = (double) sum / count;
                                } else {
                                    p.ranking = 0;
                                }
                            }
                        }

                        for (Actor a : actors) {
                            if (a.name.equals(ratings.get(index).actorName)) {
                                a.ratings.remove(ratings.get(index));
                                a.removeObserver(u);
                                int sum = 0, count = 0;
                                for (Rating r : a.ratings) {
                                    sum += r.rating;
                                    count++;
                                }
                                if (count != 0) {
                                    a.ranking = (double) sum / count;
                                } else {
                                    a.ranking = 0;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    void modifySystem(User u) throws InvalidCommandException {
        Staff s = (Staff) u;
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to do:");
        System.out.println("    1) Add");
        System.out.println("    2) Delete");

        int action;
        if (scanner.hasNextInt()) {
            action = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        switch (action) {
            case 1:
                System.out.println("What do you want to add:");
                System.out.println("    1) Actor");
                System.out.println("    2) Movie");
                System.out.println("    3) Series");
                int type;
                if (scanner.hasNextInt()) {
                    type = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                }
                scanner.nextLine();

                switch (type) {
                    case 1:
                        System.out.print("Enter the name of the actor: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter the biography of the actor: ");
                        String bio = scanner.nextLine();
                        int type1 = 0;
                        Map<String, ProductionType> roles = new LinkedHashMap<String, ProductionType>();

                        while (type1 != 3) {
                            System.out.println("Enter the type of production they acted in: ");
                            System.out.println("    1) Movie");
                            System.out.println("    2) Series");
                            System.out.println("    3) Exit");
                            if (scanner.hasNextInt()) {
                                type1 = scanner.nextInt();
                            } else {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                            }
                            scanner.nextLine();

                            switch (type1) {
                                case 1:
                                    System.out.print("Enter the title of the movie: ");
                                    String title = scanner.nextLine();
                                    roles.put(title, ProductionType.Movie);
                                    break;
                                case 2:
                                    System.out.print("Enter the title of the series: ");
                                    String title1 = scanner.nextLine();
                                    roles.put(title1, ProductionType.Series);
                                    break;
                                case 3:
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                        }

                        Actor actor = new Actor();
                        actor.name = name;
                        actor.bio = bio;
                        actor.roles = roles;

                        s.addActorSystem(actor);
                        s.contributions.add(actor);
                        if (!(s instanceof Admin)) {
                            Context context = new Context(new AddContributionExperienceStrategy());
                            s.updateExperience(context.executeStrategy());
                        }
                        actor.registerObserver(s);
                        break;
                    case 2:
                        System.out.print("Enter the title of the movie: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter the description of the movie: ");
                        String description = scanner.nextLine();
                        System.out.print("Enter the duration of the movie in minutes: ");
                        int duration;
                        if (scanner.hasNextInt()) {
                            duration = scanner.nextInt();
                        } else {
                            throw new InvalidCommandException("Invalid command! Please enter a number.");
                        }
                        scanner.nextLine();
                        System.out.print("Enter the year when the movie released: ");
                        int year;
                        if (scanner.hasNextInt()) {
                            year = scanner.nextInt();
                        } else {
                            throw new InvalidCommandException("Invalid command! Please enter a number.");
                        }
                        scanner.nextLine();

                        List<String> directors = new ArrayList<String>();
                        List<String> actors = new ArrayList<String>();
                        List<Genre> genres = new ArrayList<Genre>();

                        String director = new String();
                        while (!director.equals("exit")) {
                            System.out.print("Add a director (type 'exit' when done): ");
                            director = scanner.nextLine();
                            if (!director.equals("exit")) {
                                directors.add(director);
                            }
                        }

                        String actor1 = new String();
                        while (!actor1.equals("exit")) {
                            System.out.print("Add an actor (type 'exit' when done): ");
                            actor1 = scanner.nextLine();
                            if (!actor1.equals("exit")) {
                                actors.add(actor1);
                            }
                        }

                        int genre = 0;
                        while (genre != 15) {
                            System.out.println("Add a genre: ");
                            System.out.println("    1) Action");
                            System.out.println("    2) Adventure");
                            System.out.println("    3) Comedy");
                            System.out.println("    4) Drama");
                            System.out.println("    5) Horror");
                            System.out.println("    6) SF");
                            System.out.println("    7) Fantasy");
                            System.out.println("    8) Romance");
                            System.out.println("    9) Mystery");
                            System.out.println("    10) Thriller");
                            System.out.println("    11) Crime");
                            System.out.println("    12) Biography");
                            System.out.println("    13) War");
                            System.out.println("    14) Cooking");
                            System.out.println("    15) Exit");
                            if (scanner.hasNextInt()) {
                                genre = scanner.nextInt();
                            } else {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                            }
                            scanner.nextLine();

                            switch (genre) {
                                case 1:
                                    if (!genres.contains(Genre.Action)) {
                                        genres.add(Genre.Action);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 2:
                                    if (!genres.contains(Genre.Adventure)) {
                                        genres.add(Genre.Adventure);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 3:
                                    if (!genres.contains(Genre.Comedy)) {
                                        genres.add(Genre.Comedy);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 4:
                                    if (!genres.contains(Genre.Drama)) {
                                        genres.add(Genre.Drama);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 5:
                                    if (!genres.contains(Genre.Horror)) {
                                        genres.add(Genre.Horror);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 6:
                                    if (!genres.contains(Genre.SF)) {
                                        genres.add(Genre.SF);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 7:
                                    if (!genres.contains(Genre.Fantasy)) {
                                        genres.add(Genre.Fantasy);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 8:
                                    if (!genres.contains(Genre.Romance)) {
                                        genres.add(Genre.Romance);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 9:
                                    if (!genres.contains(Genre.Mystery)) {
                                        genres.add(Genre.Mystery);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 10:
                                    if (!genres.contains(Genre.Thriller)) {
                                        genres.add(Genre.Thriller);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 11:
                                    if (!genres.contains(Genre.Crime)) {
                                        genres.add(Genre.Crime);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 12:
                                    if (!genres.contains(Genre.Biography)) {
                                        genres.add(Genre.Biography);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 13:
                                    if (!genres.contains(Genre.War)) {
                                        genres.add(Genre.War);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 14:
                                    if (!genres.contains(Genre.Cooking)) {
                                        genres.add(Genre.Cooking);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 15:
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                            }
                        }

                        Movie movie = new Movie();
                        movie.title = title;
                        movie.description = description;
                        movie.duration = duration;
                        movie.year = year;
                        movie.directors = directors;
                        movie.actors = actors;
                        movie.genres = genres;

                        String[] words = movie.title.split(" ");
                        String trailerLink = "https://www.youtube.com/results?search_query=";
                        for (String word : words) {
                            trailerLink += word + "+";
                        }
                        trailerLink += "trailer";
                        movie.trailerLink = trailerLink;

                        s.addProductionSystem(movie);
                        s.contributions.add(movie);
                        if (!(s instanceof Admin)) {
                            Context context = new Context(new AddContributionExperienceStrategy());
                            s.updateExperience(context.executeStrategy());
                        }
                        movie.registerObserver(s);
                        break;
                    case 3:
                        System.out.print("Enter the title of the series: ");
                        String title1 = scanner.nextLine();
                        System.out.print("Enter the description of the series: ");
                        String description1 = scanner.nextLine();
                        System.out.print("Enter the number of seasons of the series: ");
                        int noOfSeasons;
                        if (scanner.hasNextInt()) {
                            noOfSeasons = scanner.nextInt();
                        } else {
                            throw new InvalidCommandException("Invalid command! Please enter a number.");
                        }
                        scanner.nextLine();
                        System.out.print("Enter the year when the series released: ");
                        int year1;
                        if (scanner.hasNextInt()) {
                            year1 = scanner.nextInt();
                        } else {
                            throw new InvalidCommandException("Invalid command! Please enter a number.");
                        }
                        scanner.nextLine();

                        List<String> directors1 = new ArrayList<String>();
                        List<String> actors1 = new ArrayList<String>();
                        List<Genre> genres1 = new ArrayList<Genre>();

                        String director1 = new String();
                        while (!director1.equals("exit")) {
                            System.out.print("Add a director (type 'exit' when done): ");
                            director1 = scanner.nextLine();
                            if (!director1.equals("exit")) {
                                directors1.add(director1);
                            }
                        }

                        String actor2 = new String();
                        while (!actor2.equals("exit")) {
                            System.out.print("Add an actor (type 'exit' when done): ");
                            actor2 = scanner.nextLine();
                            if (!actor2.equals("exit")) {
                                actors1.add(actor2);
                            }
                        }

                        int genre1 = 0;
                        while (genre1 != 15) {
                            System.out.println("Add a genre: ");
                            System.out.println("    1) Action");
                            System.out.println("    2) Adventure");
                            System.out.println("    3) Comedy");
                            System.out.println("    4) Drama");
                            System.out.println("    5) Horror");
                            System.out.println("    6) SF");
                            System.out.println("    7) Fantasy");
                            System.out.println("    8) Romance");
                            System.out.println("    9) Mystery");
                            System.out.println("    10) Thriller");
                            System.out.println("    11) Crime");
                            System.out.println("    12) Biography");
                            System.out.println("    13) War");
                            System.out.println("    14) Cooking");
                            System.out.println("    15) Exit");
                            if (scanner.hasNextInt()) {
                                genre1 = scanner.nextInt();
                            } else {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                            }
                            scanner.nextLine();

                            switch (genre1) {
                                case 1:
                                    if (!genres1.contains(Genre.Action)) {
                                        genres1.add(Genre.Action);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 2:
                                    if (!genres1.contains(Genre.Adventure)) {
                                        genres1.add(Genre.Adventure);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 3:
                                    if (!genres1.contains(Genre.Comedy)) {
                                        genres1.add(Genre.Comedy);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 4:
                                    if (!genres1.contains(Genre.Drama)) {
                                        genres1.add(Genre.Drama);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 5:
                                    if (!genres1.contains(Genre.Horror)) {
                                        genres1.add(Genre.Horror);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 6:
                                    if (!genres1.contains(Genre.SF)) {
                                        genres1.add(Genre.SF);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 7:
                                    if (!genres1.contains(Genre.Fantasy)) {
                                        genres1.add(Genre.Fantasy);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 8:
                                    if (!genres1.contains(Genre.Romance)) {
                                        genres1.add(Genre.Romance);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 9:
                                    if (!genres1.contains(Genre.Mystery)) {
                                        genres1.add(Genre.Mystery);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 10:
                                    if (!genres1.contains(Genre.Thriller)) {
                                        genres1.add(Genre.Thriller);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 11:
                                    if (!genres1.contains(Genre.Crime)) {
                                        genres1.add(Genre.Crime);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 12:
                                    if (!genres1.contains(Genre.Biography)) {
                                        genres1.add(Genre.Biography);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 13:
                                    if (!genres1.contains(Genre.War)) {
                                        genres1.add(Genre.War);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 14:
                                    if (!genres1.contains(Genre.Cooking)) {
                                        genres1.add(Genre.Cooking);
                                    } else {
                                        System.out.println("The genre is already added!");
                                    }
                                    break;
                                case 15:
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                            }
                        }

                        Map<String, List<Episode>> seasons = new LinkedHashMap<String, List<Episode>>();
                        for (int j = 1; j <= noOfSeasons; j++) {
                            System.out.print("Enter the number of episodes in season " + j + ": ");
                            int noOfEpisodes;
                            if (scanner.hasNextInt()) {
                                noOfEpisodes = scanner.nextInt();
                            } else {
                                throw new InvalidCommandException("Invalid command! Please enter a number.");
                            }
                            scanner.nextLine();

                            List<Episode> episodes = new ArrayList<Episode>();
                            for (int k = 1; k <= noOfEpisodes; k++) {
                                System.out.print("Enter the title of episode " + k + ": ");
                                String episodeTitle = scanner.nextLine();
                                System.out.print("Enter the duration of episode " + k + " in minutes: ");
                                int episodeDuration;
                                if (scanner.hasNextInt()) {
                                    episodeDuration = scanner.nextInt();
                                } else {
                                    throw new InvalidCommandException("Invalid command! Please enter a number.");
                                }
                                scanner.nextLine();

                                Episode episode = new Episode();
                                episode.name = episodeTitle;
                                episode.duration = episodeDuration;

                                episodes.add(episode);
                            }

                            seasons.put("Season" + j, episodes);
                        }

                        Series series = new Series();
                        series.title = title1;
                        series.description = description1;
                        series.noOfSeasons = noOfSeasons;
                        series.year = year1;
                        series.directors = directors1;
                        series.actors = actors1;
                        series.genres = genres1;
                        for (Map.Entry<String, List<Episode>> entry : seasons.entrySet()) {
                            series.addSeason(entry.getKey(), entry.getValue());
                        }

                        String[] words1 = series.title.split(" ");
                        String trailerLink1 = "https://www.youtube.com/results?search_query=";
                        for (String word : words1) {
                            trailerLink1 += word + "+";
                        }
                        trailerLink1 += "trailer";
                        series.trailerLink = trailerLink1;

                        s.addProductionSystem(series);
                        s.contributions.add(series);
                        if (!(s instanceof Admin)) {
                            Context context = new Context(new AddContributionExperienceStrategy());
                            s.updateExperience(context.executeStrategy());
                        }
                        series.registerObserver(s);
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                }
                break;
            case 2:
                System.out.println("What do you want to delete:");
                System.out.println("    1) Actor");
                System.out.println("    2) Production");
                int type1;
                if (scanner.hasNextInt()) {
                    type1 = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                }
                scanner.nextLine();
                Staff staff = (Staff) u;

                switch (type1) {
                    case 1:
                        if (staff instanceof Admin) {
                            int k1 = 0;
                            for (Object obj : staff.contributions) {
                                if (obj instanceof Actor) {
                                    k1++;
                                }
                            }
                            for (Object obj : Admin.ContributionsHolder.contributions) {
                                if (obj instanceof Actor) {
                                    k1++;
                                }
                            }
                            if (k1 == 0) {
                                System.out.println("There are no actors you contributed to!");
                                return;
                            } else {
                                System.out.println("Choose the actor you want to delete:");
                                Map<Integer, Actor> actors1 = new HashMap<Integer, Actor>();
                                int i = 0;
                                for (Object obj : s.contributions) {
                                    if (obj instanceof Actor) {
                                        Actor actor = (Actor) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + actor.name);
                                        actors1.put(i, actor);
                                    }
                                }
                                int middle = i;
                                for (Object obj : Admin.ContributionsHolder.contributions) {
                                    if (obj instanceof Actor) {
                                        Actor actor = (Actor) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + actor.name);
                                        actors1.put(i, actor);
                                    }
                                }

                                int index;
                                if (scanner.hasNextInt()) {
                                    index = scanner.nextInt();
                                } else {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                }
                                scanner.nextLine();

                                if (index > i || index < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                } else {
                                    if (index <= middle) {
                                        for (Object obj : staff.contributions) {
                                            if (obj instanceof Actor) {
                                                Actor actor = (Actor) obj;
                                                if (actor.name.equals(actors1.get(index).name)) {
                                                    staff.contributions.remove(actor);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        Admin.ContributionsHolder.removeContribution(actors1.get(index));
                                    }
                                    for (Actor actor : actors) {
                                        if (actor.name.equals(actors1.get(index).name)) {
                                            staff.removeActorSystem(actors1.get(index).name);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (staff instanceof Contributor) {
                            int k1 = 0;
                            for (Object obj : staff.contributions) {
                                if (obj instanceof Actor) {
                                    k1++;
                                }
                            }
                            if (k1 == 0) {
                                System.out.println("There are no actors you contributed to!");
                                return;
                            } else {
                                System.out.println("Choose the actor you want to delete:");
                                Map<Integer, Actor> actors1 = new HashMap<Integer, Actor>();
                                int i = 0;
                                for (Object obj : s.contributions) {
                                    if (obj instanceof Actor) {
                                        Actor actor = (Actor) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + actor.name);
                                        actors1.put(i, actor);
                                    }
                                }

                                int index;
                                if (scanner.hasNextInt()) {
                                    index = scanner.nextInt();
                                } else {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                }
                                scanner.nextLine();

                                if (index > i || index < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                } else {
                                    for (Object obj : staff.contributions) {
                                        if (obj instanceof Actor) {
                                            Actor actor = (Actor) obj;
                                            if (actor.name.equals(actors1.get(index).name)) {
                                                staff.contributions.remove(actor);
                                                break;
                                            }
                                        }
                                    }
                                    for (Actor actor : actors) {
                                        if (actor.name.equals(actors1.get(index).name)) {
                                            staff.removeActorSystem(actors1.get(index).name);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 2:
                        if (staff instanceof Admin) {
                            int k = 0;
                            for (Object obj : staff.contributions) {
                                if (obj instanceof Production) {
                                    k++;
                                }
                            }
                            for (Object obj : Admin.ContributionsHolder.contributions) {
                                if (obj instanceof Production) {
                                    k++;
                                }
                            }
                            if (k == 0) {
                                System.out.println("There are no productions you contributed to!");
                                return;
                            } else {
                                System.out.println("Choose the production you want to delete:");
                                Map<Integer, Production> productions1 = new HashMap<Integer, Production>();
                                int i = 0;

                                for (Object obj : staff.contributions) {
                                    if (obj instanceof Production) {
                                        Production production = (Production) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + production.title);
                                        productions1.put(i, production);
                                    }
                                }
                                int middle = i;
                                for (Object obj : Admin.ContributionsHolder.contributions) {
                                    if (obj instanceof Production) {
                                        Production production = (Production) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + production.title);
                                        productions1.put(i, production);
                                    }
                                }

                                int index;
                                if (scanner.hasNextInt()) {
                                    index = scanner.nextInt();
                                } else {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                }
                                scanner.nextLine();

                                if (index > i || index < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                } else {
                                    if (index <= middle) {
                                        for (Object obj : staff.contributions) {
                                            if (obj instanceof Production) {
                                                Production production = (Production) obj;
                                                if (production.title.equals(productions1.get(index).title)) {
                                                    staff.contributions.remove(production);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        Admin.ContributionsHolder.removeContribution(productions1.get(index));
                                    }
                                    for (Production p : productions) {
                                        if (p.title.equals(productions1.get(index).title)) {
                                            staff.removeProductionSystem(productions1.get(index).title);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (staff instanceof Contributor) {
                            int k = 0;
                            for (Object obj : staff.contributions) {
                                if (obj instanceof Production) {
                                    k++;
                                }
                            }
                            if (k == 0) {
                                System.out.println("There are no productions you contributed to!");
                                return;
                            } else {
                                System.out.println("Choose the production you want to delete:");
                                Map<Integer, Production> productions1 = new HashMap<Integer, Production>();
                                int i = 0;

                                for (Object obj : staff.contributions) {
                                    if (obj instanceof Production) {
                                        Production production = (Production) obj;
                                        i++;
                                        System.out.println("    " + i + ") " + production.title);
                                        productions1.put(i, production);
                                    }
                                }

                                int index;
                                if (scanner.hasNextInt()) {
                                    index = scanner.nextInt();
                                } else {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                }
                                scanner.nextLine();

                                if (index > i || index < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                                } else {
                                    for (Object obj : staff.contributions) {
                                        if (obj instanceof Production) {
                                            Production production = (Production) obj;
                                            if (production.title.equals(productions1.get(index).title)) {
                                                staff.contributions.remove(production);
                                                break;
                                            }
                                        }
                                    }
                                    for (Production p : productions) {
                                        if (p.title.equals(productions1.get(index).title)) {
                                            staff.removeProductionSystem(productions1.get(index).title);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    void solveRequest(User u) throws InvalidCommandException {
        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            if (c.requests.isEmpty()) {
                System.out.println("You don't have any requests!");
                return;
            } else {
                Map<Integer, Request> requests = new HashMap<Integer, Request>();
                int i = 0;

                System.out.println("Choose the request you want to solve:");
                for (Object obj : c.requests) {
                    Request r = (Request) obj;
                    i++;
                    System.out.println("    " + i + ") " + r.getRequestType() + ": " + r.description + " (" + r.senderName + ")");
                    requests.put(i, r);
                }

                Scanner scanner = new Scanner(System.in);
                int index;
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    c.solveRequest(requests.get(index));
                }
            }
        } else if (u instanceof Admin) {
            Admin a = (Admin) u;
            if (a.requests.isEmpty() && Admin.RequestsHolder.requests.isEmpty()) {
                System.out.println("You don't have any requests!");
            } else {
                Map<Integer, Request> requests = new HashMap<Integer, Request>();
                int i = 0;

                System.out.println("Choose the request you want to solve:");
                for (Object obj : a.requests) {
                    Request r = (Request) obj;
                    i++;
                    System.out.println("    " + i + ") " + r.getRequestType() + ": " + r.description + " (" + r.senderName + ")");
                    requests.put(i, r);
                }
                int middle = i;
                for (Object obj : Admin.RequestsHolder.requests) {
                    Request r = (Request) obj;
                    i++;
                    System.out.println("    " + i + ") " + r.getRequestType() + ": " + r.description + " (" + r.senderName + ")");
                    requests.put(i, r);
                }

                Scanner scanner = new Scanner(System.in);
                int index;
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    a.solveRequest(requests.get(index));
                }
            }
        }
    }

    void updateSystem(User u) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to update:");
        System.out.println("    1) Actor");
        System.out.println("    2) Movie");
        System.out.println("    3) Series");

        int type1;
        if (scanner.hasNextInt()) {
            type1 = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
        }
        scanner.nextLine();
        Staff staff = (Staff) u;

        switch (type1) {
            case 1:
                staff.updateActor();
                break;
            case 2:
                staff.updateMovie();
                break;
            case 3:
                staff.updateSeries();
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
        }
    }

    String generateUsername(String firstName, String lastName) {
        String username = "";
        while (true) {
            Random random = new Random();
            int number = random.nextInt(90000) + 10000;
            String generatedUsername = firstName.toLowerCase() + "_" + lastName.toLowerCase() + "_" + number;

            boolean exists = false;
            for (User user1 : users) {
                if (user1.username.equals(generatedUsername)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                username = generatedUsername;
                break;
            }
        }

        return username;
    }

    String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }

    void modifyUsers(User user) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        Admin a = (Admin) user;

        System.out.println("What do you want to do:");
        System.out.println("    1) Add");
        System.out.println("    2) Delete");

        int action;
        if (scanner.hasNextInt()) {
            action = scanner.nextInt();
        } else {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        switch (action) {
            case 1:
                System.out.println("What type of user do you want to add:");
                System.out.println("    1) Regular");
                System.out.println("    2) Contributor");
                System.out.println("    3) Admin");
                int type;
                if (scanner.hasNextInt()) {
                    type = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                }
                scanner.nextLine();

                System.out.print("Enter the first name of the user: ");
                String firstName = scanner.next();
                System.out.print("Enter the last name of the user: ");
                String lastName = scanner.next();
                System.out.print("Enter the email of the user: ");
                String email = scanner.next();
                System.out.print("Enter the country of the user: ");
                String country = scanner.next();
                System.out.print("Enter the gender of the user: ");
                String gender = scanner.next();
                System.out.print("Enter the birthday of the user (YYYY-MM-DD): ");
                String birthDate = scanner.next();
                LocalDateTime birthday = null;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    LocalDate date = LocalDate.parse(birthDate, formatter);
                    birthday = date.atStartOfDay();
                } catch (Exception ex) {
                    System.out.println("Invalid date format!");
                    return;
                }
                LocalDate date = LocalDate.parse(birthDate);

                int age = Period.between(date, LocalDate.now()).getYears();

                String username = generateUsername(firstName, lastName);
                String password = generatePassword();
                String name = firstName + " " + lastName;
                System.out.println("User " + username + " created successfully with password: " + password + ".");

                User.Information info = null;
                try {
                    info = new User.Information.InformationBuilder(email, password)
                            .name(name)
                            .country(country)
                            .age(age)
                            .gender(gender)
                            .birthday(birthday)
                            .build();
                } catch (InformationIncompleteException e) {
                    System.out.println(e.getMessage());
                }

                switch (type) {
                    case 1:
                        User userObject = UserFactory.factory(AccountType.Regular);
                        userObject.accountType = AccountType.Regular;
                        userObject.info = info;
                        userObject.username = username;
                        userObject.experience = 0;
                        a.addUser(userObject);
                        break;
                    case 2:
                        User userObject1 = UserFactory.factory(AccountType.Contributor);
                        userObject1.accountType = AccountType.Contributor;
                        userObject1.info = info;
                        userObject1.username = username;
                        userObject1.experience = 0;
                        a.addUser(userObject1);
                        break;
                    case 3:
                        User userObject2 = UserFactory.factory(AccountType.Admin);
                        userObject2.accountType = AccountType.Admin;
                        userObject2.info = info;
                        userObject2.username = username;
                        userObject2.experience = 999;
                        a.addUser(userObject2);
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                }
                break;
            case 2:
                if (users.isEmpty()) {
                    System.out.println("There are no users in the system!");
                    return;
                }
                System.out.println("Choose the user you want to delete:");
                Map<Integer, User> users1 = new HashMap<Integer, User>();
                int i = 0;

                for (User user1 : users) {
                    if (!user1.equals(a)) {
                        i++;
                        System.out.println("    " + i + ") " + user1.username);
                        users1.put(i, user1);
                    }
                }

                int index;
                if (scanner.hasNextInt()) {
                    index = scanner.nextInt();
                } else {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    a.removeUser(users1.get(index));
                }
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    void terminalMenu(User user) throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose action:");
        System.out.println("    1) View productions details");
        System.out.println("    2) View actors details");
        System.out.println("    3) View notifications");
        System.out.println("    4) Search for actor/movie/series");
        System.out.println("    5) Add/Delete actor/movie/series to/from favorites");

        if (user.accountType == AccountType.Regular) {
            System.out.println("    6) Create/Withdraw request");
            System.out.println("    7) Add/Delete production/actor rating");
            System.out.println("    8) Logout\n");

            int action;
            if (scanner.hasNextInt()) {
                action = scanner.nextInt();
            } else {
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 8.");
            }
            switch (action) {
                case 1:
                    printProductions();
                    terminalMenu(user);
                    break;
                case 2:
                    printActors();
                    terminalMenu(user);
                    break;
                case 3:
                    printNotifications(user);
                    terminalMenu(user);
                    break;
                case 4:
                    search();
                    terminalMenu(user);
                    break;
                case 5:
                    modifyFavorites(user);
                    terminalMenu(user);
                    break;
                case 6:
                    modifyRequests(user);
                    terminalMenu(user);
                    break;
                case 7:
                    modifyRatings(user);
                    terminalMenu(user);
                    break;
                case 8:
                    user.userLogout();
                    break;
                default:
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 8.");
            }
        } else if (user.accountType == AccountType.Contributor) {
            System.out.println("    6) Create/Withdraw request");
            System.out.println("    7) Add/Delete actor/movie/series from system");
            System.out.println("    8) Solve a request");
            System.out.println("    9) Update actor/movie/series details");
            System.out.println("    10) Logout\n");

            int action;
            if (scanner.hasNextInt()) {
                action = scanner.nextInt();
            } else {
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 10.");
            }
            switch (action) {
                case 1:
                    printProductions();
                    terminalMenu(user);
                    break;
                case 2:
                    printActors();
                    terminalMenu(user);
                    break;
                case 3:
                    printNotifications(user);
                    terminalMenu(user);
                    break;
                case 4:
                    search();
                    terminalMenu(user);
                    break;
                case 5:
                    modifyFavorites(user);
                    terminalMenu(user);
                    break;
                case 6:
                    modifyRequests(user);
                    terminalMenu(user);
                    break;
                case 7:
                    modifySystem(user);
                    terminalMenu(user);
                    break;
                case 8:
                    solveRequest(user);
                    terminalMenu(user);
                    break;
                case 9:
                    updateSystem(user);
                    terminalMenu(user);
                    break;
                case 10:
                    user.userLogout();
                    break;
                default:
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 10.");
            }

        } else if (user.accountType == AccountType.Admin) {
            System.out.println("    6) Add/Delete actor/movie/series from system");
            System.out.println("    7) Solve a request");
            System.out.println("    8) Update actor/movie/series details");
            System.out.println("    9) Add/Delete user");
            System.out.println("    10) Logout\n");

            int action;
            if (scanner.hasNextInt()) {
                action = scanner.nextInt();
            } else {
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 10.");
            }
            switch (action) {
                case 1:
                    printProductions();
                    terminalMenu(user);
                    break;
                case 2:
                    printActors();
                    terminalMenu(user);
                    break;
                case 3:
                    printNotifications(user);
                    terminalMenu(user);
                    break;
                case 4:
                    search();
                    terminalMenu(user);
                    break;
                case 5:
                    modifyFavorites(user);
                    terminalMenu(user);
                    break;
                case 6:
                    modifySystem(user);
                    terminalMenu(user);
                    break;
                case 7:
                    solveRequest(user);
                    terminalMenu(user);
                    break;
                case 8:
                    updateSystem(user);
                    terminalMenu(user);
                    break;
                case 9:
                    modifyUsers(user);
                    terminalMenu(user);
                    break;
                case 10:
                    user.userLogout();
                    break;
                default:
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 10.");
            }
        }
    }

    void terminalMode() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome back! Enter your credentials!\n");

        User user = null;
        while (user == null) {
            System.out.print("    email: ");
            String email = scanner.next();
            System.out.print("    password: ");
            String password = scanner.next();

            for (User user1 : users) {
                if (user1.info.getCredentials().getEmail().equals(email) && user1.info.getCredentials().getPassword().equals(password)) {
                    user = user1;
                }
            }
            if (user == null) {
                System.out.println("Invalid credentials! Try again!\n");
            }
        }

        System.out.println("Welcome back user " + user.username + "!");
        System.out.println("Username: " + user.username);
        if (user.accountType == AccountType.Admin) {
            System.out.println("User experience: -");
        } else {
            System.out.println("User experience: " + user.experience);
        }

        terminalMenu(user);
    }

    void GUI() {
        try {
            new LoginPage();
        } catch (ClassNotFoundException e) {
        }
    }

    public void run() {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("main/resources/input/actors.json")) {
            Object obj = parser.parse(reader);

            JSONArray actorList = (JSONArray) obj;

            actorList.forEach(actor -> parseActorObject((JSONObject) actor));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader("main/resources/input/production.json")) {
            Object obj = parser.parse(reader);

            JSONArray productionList = (JSONArray) obj;

            productionList.forEach(production -> parseProductionObject((JSONObject) production));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader("main/resources/input/accounts.json")) {
            Object obj = parser.parse(reader);

            JSONArray userList = (JSONArray) obj;

            userList.forEach(user -> parseUserObject((JSONObject) user));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader("main/resources/input/requests.json")) {
            Object obj = parser.parse(reader);

            JSONArray requestList = (JSONArray) obj;

            requestList.forEach(request -> parseRequestObject((JSONObject) request));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose the app mode: ");
            System.out.println("    1) Terminal");
            System.out.println("    2) GUI");
            int mode;
            if (scanner.hasNextInt()) {
                mode = scanner.nextInt();
            } else {
                throw new InvalidCommandException("Invalid command! Please enter 1 for Terminal or 2 for GUI.");
            }

            if (mode == 1) {
                terminalMode();
            } else if (mode == 2) {
                GUI();
            } else {
                throw new InvalidCommandException("Invalid command! Please enter 1 for Terminal or 2 for GUI.");
            }
        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveData() {
        saveUsers();
        saveActors();
        saveProductions();
        saveRequests();
    }

    private void saveUsers() {
        JSONArray userList = new JSONArray();
        for (User user : users) {
            JSONObject userObject = new JSONObject();
            userObject.put("username", user.getUsername());
            userObject.put("experience", user.experience);

            JSONObject informationObject = new JSONObject();
            informationObject.put("name", user.getName());
            informationObject.put("country", user.getCountry());
            informationObject.put("age", user.getAge());
            informationObject.put("gender", user.getGender());
            informationObject.put("birthDate", user.getBirthDate());

            JSONObject credentialsObject = new JSONObject();
            credentialsObject.put("email", user.getEmail());
            credentialsObject.put("password", user.getPassword());
            informationObject.put("credentials", credentialsObject);

            userObject.put("information", informationObject);
            userObject.put("userType", user.accountType.toString());
           
            if (!user.notifications.isEmpty()) {
                JSONArray notificationsArray = new JSONArray();
                notificationsArray.addAll(user.notifications);
                userObject.put("notifications", notificationsArray);
            }

           
            if (!user.favorites.isEmpty()) {
                JSONArray favoritesArray = new JSONArray();
                for (Object favorite : user.favorites) {
                    if (favorite instanceof Actor) {
                        Actor actor = (Actor) favorite;
                        favoritesArray.add(actor.name);
                    }
                    else if (favorite instanceof Production) {
                        Production production = (Production) favorite;
                        favoritesArray.add(production.title);
                    }
                }

                userObject.put("favorites", favoritesArray);
            }

            if (user instanceof Staff) {
                Staff staff = (Staff) user;
                if (!staff.contributions.isEmpty()) {
                    JSONArray contributionsArray = new JSONArray();
                    for (Object contribution : staff.contributions) {
                        if (contribution instanceof Actor) {
                            Actor actor = (Actor) contribution;
                            contributionsArray.add(actor.name);
                        }
                        else if (contribution instanceof Production) {
                            Production production = (Production) contribution;
                            contributionsArray.add(production.title);
                        }
                    }
                    userObject.put("contributions", contributionsArray);
                }
            }
            userList.add(userObject);
        }
        writeToFile(userList, "main/resources/output/accounts.json");
    }

    private void saveActors() {
        JSONArray actorList = new JSONArray();
        for (Actor actor : actors) {
            JSONObject actorObject = new JSONObject();
           
            actorObject.put("name", actor.name);

            JSONArray rolesObject = new JSONArray();
            for (Map.Entry<String, ProductionType> entry : actor.roles.entrySet()) {
                JSONObject roleObject = new JSONObject();
                roleObject.put("title", entry.getKey());
                roleObject.put("type", entry.getValue().toString());
                rolesObject.add(roleObject);
            }
            actorObject.put("performances", rolesObject);

            if (actor.bio != null && !actor.bio.isEmpty()) {
                actorObject.put("biography", actor.bio);
            }

            actorList.add(actorObject);
        }
        writeToFile(actorList, "main/resources/output/actors.json");
    }

    private void saveProductions() {
        JSONArray productionList = new JSONArray();
        for (Production production : productions) {
            JSONObject productionObject = new JSONObject();
           
            productionObject.put("title", production.title);
            if (production instanceof Movie) {
                productionObject.put("type", "Movie");
            }
            else if (production instanceof Series) {
                productionObject.put("type", "Series");
            }

            JSONArray directorsArray = new JSONArray();
            for (String director : production.directors) {
                directorsArray.add(director);
            }
            productionObject.put("directors", directorsArray);

            JSONArray actorsArray = new JSONArray();
            for (String actor : production.actors) {
                actorsArray.add(actor);
            }
            productionObject.put("actors", actorsArray);

            JSONArray genresArray = new JSONArray();
            for (Genre genre : production.genres) {
                genresArray.add(genre.toString());
            }
            productionObject.put("genres", genresArray);

            JSONArray ratingsArray = new JSONArray();
            for (Rating rating : production.ratings) {
                JSONObject ratingObject = new JSONObject();
                ratingObject.put("username", rating.username);
                ratingObject.put("rating", rating.rating);
                ratingObject.put("comment", rating.comment);

                ratingsArray.add(ratingObject);
            }
            productionObject.put("ratings", ratingsArray);

            productionObject.put("plot", production.description);
            productionObject.put("averageRating", production.ranking);

            if (production instanceof Movie) {
                productionObject.put("duration", ((Movie) production).duration + " minutes");
                productionObject.put("releaseYear", ((Movie) production).year);
            }
            else if (production instanceof Series) {
                productionObject.put("releaseYear", ((Series) production).year);
                productionObject.put("numSeasons", ((Series) production).noOfSeasons);

                JSONObject seasonsObject = new JSONObject();
                for (Map.Entry<String, List<Episode>> entry : ((Series) production).getSeasons().entrySet()) {
                    JSONArray episodesArray = new JSONArray();
                    for (Episode episode : entry.getValue()) {
                        JSONObject episodeObject = new JSONObject();
                        episodeObject.put("episodeName", episode.name);
                        episodeObject.put("duration", episode.duration + " minutes");
                        episodesArray.add(episodeObject);
                    }
                    seasonsObject.put(entry.getKey(), episodesArray);
                }
                productionObject.put("seasons", seasonsObject);
            }

            productionList.add(productionObject);
        }
        writeToFile(productionList, "main/resources/output/production.json");
    }

    private void saveRequests() {
        JSONArray requestList = new JSONArray();
        for (Request request : requests) {
            JSONObject requestObject = new JSONObject();
           
            requestObject.put("requestType", request.getRequestType().toString());
            requestObject.put("createdDate", request.getDate().toString());
            requestObject.put("username", request.senderName);
            if (request.getRequestType().equals("MOVIE_ISSUE")) {
                requestObject.put("movieTitle", request.movieTitle);
            }
            else if (request.getRequestType().equals("ACTOR_ISSUE")) {
                requestObject.put("actorName", request.actorName);
            }
            requestObject.put("to", request.recipientName);
            requestObject.put("description", request.description);

            requestList.add(requestObject);
        }
        writeToFile(requestList, "main/resources/output/requests.json");
    }

    private void writeToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String jsonString = jsonObject.toJSONString();
                BufferedReader reader = new BufferedReader(new StringReader(jsonString));
                String line;
                while ((line = reader.readLine()) != null) {
                    file.write(line);
                    file.write(System.lineSeparator());
                }
            }
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
