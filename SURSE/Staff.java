package org.example;

import java.util.*;

public class Staff<T extends Comparable<T>> extends User implements StaffInterface {
    List<Request> requests = new ArrayList<Request>();
    SortedSet<T> contributions = new TreeSet<T>();

    public void addProductionSystem(Production p) {
        IMDb.getInstance().productions.add(p);
    }

    public void addActorSystem(Actor a) {
        IMDb.getInstance().actors.add(a);
    }

    public void removeProductionSystem(String name) {
        for (Production p : IMDb.getInstance().productions) {
            if (p.title.equals(name)) {
                IMDb.getInstance().productions.remove(p);
                break;
            }
        }
    }

    public void removeActorSystem(String name) {
        for (Actor a : IMDb.getInstance().actors) {
            if (a.name.equals(name)) {
                IMDb.getInstance().actors.remove(a);
                break;
            }
        }
    }

    public void updateMovie() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        if (this instanceof Contributor) {
            int k = 0;
            for (Object obj : this.contributions) {
                if (obj instanceof Movie) {
                    k++;
                }
            }
            if (k == 0) {
                System.out.println("There are no movies you contributed to!");
                return;
            } else {
                System.out.println("Choose the movie you want to update:");
                Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
                int i = 0;

                for (Object obj : this.contributions) {
                    if (obj instanceof Movie) {
                        Movie movie = (Movie) obj;
                        i++;
                        System.out.println("    " + i + ") " + movie.title);
                        movies.put(i, movie);
                    }
                }

                int index;
                try {
                    index = scanner.nextInt();
                } catch (InputMismatchException e) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    System.out.println("What do you want to update:");
                    System.out.println("    1) Title");
                    System.out.println("    2) Description");
                    System.out.println("    3) Duration");
                    System.out.println("    4) Year");
                    System.out.println("    5) Directors");
                    System.out.println("    6) Actors");
                    System.out.println("    7) Genres");
                    int type2;
                    try {
                        type2 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                    }
                    scanner.nextLine();

                    switch (type2) {
                        case 1:
                            System.out.print("Enter the new title: ");
                            String title = scanner.nextLine();
                            movies.get(index).title = title;
                            String[] words = movies.get(index).title.split(" ");
                            String trailerLink = "https://www.youtube.com/results?search_query=";
                            for (String word : words) {
                                trailerLink += word + "+";
                            }
                            trailerLink += "trailer";
                            movies.get(index).trailerLink = trailerLink;
                            break;
                        case 2:
                            System.out.print("Enter the new description: ");
                            String description = scanner.nextLine();
                            movies.get(index).description = description;
                            break;
                        case 3:
                            System.out.print("Enter the new duration: ");
                            int duration;
                            try {
                                duration = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("The duration must be an integer!");
                            }
                            scanner.nextLine();
                            movies.get(index).duration = duration;
                            break;
                        case 4:
                            System.out.print("Enter the new year: ");
                            int year;
                            try {
                                year = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("The year must be an integer!");
                            }
                            scanner.nextLine();
                            movies.get(index).year = year;
                            break;
                        case 5:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type3;
                            try {
                                type3 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type3) {
                                case 1:
                                    System.out.print("Enter the new director: ");
                                    String director = scanner.nextLine();
                                    movies.get(index).directors.add(director);
                                    break;
                                case 2:
                                    if (movies.get(index).directors.isEmpty()) {
                                        System.out.println("There are no directors to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the director you want to delete:");
                                    Map<Integer, String> directors = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (String director1 : movies.get(index).directors) {
                                        j++;
                                        System.out.println("    " + j + ") " + director1);
                                        directors.put(j, director1);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).directors.remove(directors.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        case 6:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type4;
                            try {
                                type4 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type4) {
                                case 1:
                                    System.out.print("Enter the new actor: ");
                                    String actor = scanner.nextLine();
                                    movies.get(index).actors.add(actor);
                                    break;
                                case 2:
                                    if (movies.get(index).actors.isEmpty()) {
                                        System.out.println("There are no actors to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the actor you want to delete:");
                                    Map<Integer, String> actors = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (String actor1 : movies.get(index).actors) {
                                        j++;
                                        System.out.println("    " + j + ") " + actor1);
                                        actors.put(j, actor1);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).actors.remove(actors.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        case 7:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type5;
                            try {
                                type5 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type5) {
                                case 1:
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
                                    int genre1;
                                    try {
                                        genre1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                    }
                                    scanner.nextLine();

                                    switch (genre1) {
                                        case 1:
                                            if (!movies.get(index).genres.contains(Genre.Action)) {
                                                movies.get(index).genres.add(Genre.Action);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 2:
                                            if (!movies.get(index).genres.contains(Genre.Adventure)) {
                                                movies.get(index).genres.add(Genre.Adventure);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 3:
                                            if (!movies.get(index).genres.contains(Genre.Comedy)) {
                                                movies.get(index).genres.add(Genre.Comedy);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 4:
                                            if (!movies.get(index).genres.contains(Genre.Drama)) {
                                                movies.get(index).genres.add(Genre.Drama);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 5:
                                            if (!movies.get(index).genres.contains(Genre.Horror)) {
                                                movies.get(index).genres.add(Genre.Horror);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 6:
                                            if (!movies.get(index).genres.contains(Genre.SF)) {
                                                movies.get(index).genres.add(Genre.SF);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 7:
                                            if (!movies.get(index).genres.contains(Genre.Fantasy)) {
                                                movies.get(index).genres.add(Genre.Fantasy);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 8:
                                            if (!movies.get(index).genres.contains(Genre.Romance)) {
                                                movies.get(index).genres.add(Genre.Romance);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 9:
                                            if (!movies.get(index).genres.contains(Genre.Mystery)) {
                                                movies.get(index).genres.add(Genre.Mystery);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 10:
                                            if (!movies.get(index).genres.contains(Genre.Thriller)) {
                                                movies.get(index).genres.add(Genre.Thriller);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 11:
                                            if (!movies.get(index).genres.contains(Genre.Crime)) {
                                                movies.get(index).genres.add(Genre.Crime);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 12:
                                            if (!movies.get(index).genres.contains(Genre.Biography)) {
                                                movies.get(index).genres.add(Genre.Biography);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 13:
                                            if (!movies.get(index).genres.contains(Genre.War)) {
                                                movies.get(index).genres.add(Genre.War);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 14:
                                            if (!movies.get(index).genres.contains(Genre.Cooking)) {
                                                movies.get(index).genres.add(Genre.Cooking);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        default:
                                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                    }
                                    break;
                                case 2:
                                    if (movies.get(index).genres.isEmpty()) {
                                        System.out.println("There are no genres to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the genre you want to delete:");
                                    Map<Integer, Genre> genres = new HashMap<Integer, Genre>();
                                    int j = 0;
                                    for (Genre genre : movies.get(index).genres) {
                                        j++;
                                        System.out.println("    " + j + ") " + genre);
                                        genres.put(j, genre);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).genres.remove(genres.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        default:
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                    }
                }
            }
        } else if (this instanceof Admin) {
            int k = 0;
            for (Object obj : this.contributions) {
                if (obj instanceof Movie) {
                    k++;
                }
            }
            for (Object obj : Admin.ContributionsHolder.contributions) {
                if (obj instanceof Movie) {
                    k++;
                }
            }
            if (k == 0) {
                System.out.println("There are no movies you contributed to!");
                return;
            } else {
                System.out.println("Choose the movie you want to update:");
                Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
                int i = 0;

                for (Object obj : this.contributions) {
                    if (obj instanceof Movie) {
                        Movie movie = (Movie) obj;
                        i++;
                        System.out.println("    " + i + ") " + movie.title);
                        movies.put(i, movie);
                    }
                }
                for (Object obj : Admin.ContributionsHolder.contributions) {
                    if (obj instanceof Movie) {
                        Movie movie = (Movie) obj;
                        i++;
                        System.out.println("    " + i + ") " + movie.title);
                        movies.put(i, movie);
                    }
                }

                int index;
                try {
                    index = scanner.nextInt();
                } catch (InputMismatchException e) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    System.out.println("What do you want to update:");
                    System.out.println("    1) Title");
                    System.out.println("    2) Description");
                    System.out.println("    3) Duration");
                    System.out.println("    4) Year");
                    System.out.println("    5) Directors");
                    System.out.println("    6) Actors");
                    System.out.println("    7) Genres");
                    int type2;
                    try {
                        type2 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                    }
                    scanner.nextLine();

                    switch (type2) {
                        case 1:
                            System.out.print("Enter the new title: ");
                            String title = scanner.nextLine();
                            movies.get(index).title = title;
                            String[] words = movies.get(index).title.split(" ");
                            String trailerLink = "https://www.youtube.com/results?search_query=";
                            for (String word : words) {
                                trailerLink += word + "+";
                            }
                            trailerLink += "trailer";
                            movies.get(index).trailerLink = trailerLink;
                            break;
                        case 2:
                            System.out.print("Enter the new description: ");
                            String description = scanner.nextLine();
                            movies.get(index).description = description;
                            break;
                        case 3:
                            System.out.print("Enter the new duration: ");
                            int duration;
                            try {
                                duration = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("The duration must be an integer!");
                            }
                            scanner.nextLine();
                            movies.get(index).duration = duration;
                            break;
                        case 4:
                            System.out.print("Enter the new year: ");
                            int year;
                            try {
                                year = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("The year must be an integer!");
                            }
                            scanner.nextLine();
                            movies.get(index).year = year;
                            break;
                        case 5:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type3;
                            try {
                                type3 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type3) {
                                case 1:
                                    System.out.print("Enter the new director: ");
                                    String director = scanner.nextLine();
                                    movies.get(index).directors.add(director);
                                    break;
                                case 2:
                                    if (movies.get(index).directors.isEmpty()) {
                                        System.out.println("There are no directors to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the director you want to delete:");
                                    Map<Integer, String> directors = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (String director1 : movies.get(index).directors) {
                                        j++;
                                        System.out.println("    " + j + ") " + director1);
                                        directors.put(j, director1);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).directors.remove(directors.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        case 6:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type4;
                            try {
                                type4 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type4) {
                                case 1:
                                    System.out.print("Enter the new actor: ");
                                    String actor = scanner.nextLine();
                                    movies.get(index).actors.add(actor);
                                    break;
                                case 2:
                                    if (movies.get(index).actors.isEmpty()) {
                                        System.out.println("There are no actors to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the actor you want to delete:");
                                    Map<Integer, String> actors = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (String actor1 : movies.get(index).actors) {
                                        j++;
                                        System.out.println("    " + j + ") " + actor1);
                                        actors.put(j, actor1);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).actors.remove(actors.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        case 7:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type5;
                            try {
                                type5 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type5) {
                                case 1:
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
                                    int genre1;
                                    try {
                                        genre1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                    }
                                    scanner.nextLine();

                                    switch (genre1) {
                                        case 1:
                                            if (!movies.get(index).genres.contains(Genre.Action)) {
                                                movies.get(index).genres.add(Genre.Action);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 2:
                                            if (!movies.get(index).genres.contains(Genre.Adventure)) {
                                                movies.get(index).genres.add(Genre.Adventure);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 3:
                                            if (!movies.get(index).genres.contains(Genre.Comedy)) {
                                                movies.get(index).genres.add(Genre.Comedy);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 4:
                                            if (!movies.get(index).genres.contains(Genre.Drama)) {
                                                movies.get(index).genres.add(Genre.Drama);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 5:
                                            if (!movies.get(index).genres.contains(Genre.Horror)) {
                                                movies.get(index).genres.add(Genre.Horror);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 6:
                                            if (!movies.get(index).genres.contains(Genre.SF)) {
                                                movies.get(index).genres.add(Genre.SF);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 7:
                                            if (!movies.get(index).genres.contains(Genre.Fantasy)) {
                                                movies.get(index).genres.add(Genre.Fantasy);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 8:
                                            if (!movies.get(index).genres.contains(Genre.Romance)) {
                                                movies.get(index).genres.add(Genre.Romance);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 9:
                                            if (!movies.get(index).genres.contains(Genre.Mystery)) {
                                                movies.get(index).genres.add(Genre.Mystery);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 10:
                                            if (!movies.get(index).genres.contains(Genre.Thriller)) {
                                                movies.get(index).genres.add(Genre.Thriller);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 11:
                                            if (!movies.get(index).genres.contains(Genre.Crime)) {
                                                movies.get(index).genres.add(Genre.Crime);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 12:
                                            if (!movies.get(index).genres.contains(Genre.Biography)) {
                                                movies.get(index).genres.add(Genre.Biography);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 13:
                                            if (!movies.get(index).genres.contains(Genre.War)) {
                                                movies.get(index).genres.add(Genre.War);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        case 14:
                                            if (!movies.get(index).genres.contains(Genre.Cooking)) {
                                                movies.get(index).genres.add(Genre.Cooking);
                                            } else {
                                                System.out.println("The genre is already added!");
                                            }
                                            break;
                                        default:
                                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                    }
                                    break;
                                case 2:
                                    if (movies.get(index).genres.isEmpty()) {
                                        System.out.println("There are no genres to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the genre you want to delete:");
                                    Map<Integer, Genre> genres = new HashMap<Integer, Genre>();
                                    int j = 0;
                                    for (Genre genre : movies.get(index).genres) {
                                        j++;
                                        System.out.println("    " + j + ") " + genre);
                                        genres.put(j, genre);
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        movies.get(index).genres.remove(genres.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        default:
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                    }
                }
            }
        }

    }

    public void updateSeries() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        int k2 = 0;
        for (Object obj : this.contributions) {
            if (obj instanceof Series) {
                k2++;
            }
        }
        if (k2 == 0) {
            System.out.println("There are no series you contributed to!");
            return;
        } else {
            System.out.println("Choose the series you want to update:");
            Map<Integer, Series> series = new HashMap<Integer, Series>();
            int i = 0;

            for (Object obj : this.contributions) {
                if (obj instanceof Series) {
                    Series s = (Series) obj;
                    i++;
                    System.out.println("    " + i + ") " + s.title);
                    series.put(i, s);
                }
            }

            int index;
            try {
                index = scanner.nextInt();
            } catch (InputMismatchException e) {
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
            }
            scanner.nextLine();

            if (index > i || index < 1) {
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
            } else {
                System.out.println("What do you want to update:");
                System.out.println("    1) Title");
                System.out.println("    2) Description");
                System.out.println("    3) Year");
                System.out.println("    4) Directors");
                System.out.println("    5) Actors");
                System.out.println("    6) Genres");
                System.out.println("    7) Seasons");
                int type2;
                try {
                    type2 = scanner.nextInt();
                } catch (InputMismatchException e) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                }
                scanner.nextLine();

                switch (type2) {
                    case 1:
                        System.out.print("Enter the new title: ");
                        String title = scanner.nextLine();
                        series.get(index).title = title;
                        String[] words = series.get(index).title.split(" ");
                        String trailerLink = "https://www.youtube.com/results?search_query=";
                        for (String word : words) {
                            trailerLink += word + "+";
                        }
                        trailerLink += "trailer";
                        series.get(index).trailerLink = trailerLink;
                        break;
                    case 2:
                        System.out.print("Enter the new description: ");
                        String description = scanner.nextLine();
                        series.get(index).description = description;
                        break;
                    case 3:
                        System.out.print("Enter the new year: ");
                        int year;
                        try {
                            year = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new InvalidCommandException("The year must be an integer!");
                        }
                        scanner.nextLine();
                        series.get(index).year = year;
                        break;
                    case 4:
                        System.out.println("What do you want to do:");
                        System.out.println("    1) Add");
                        System.out.println("    2) Delete");
                        int type3;
                        try {
                            type3 = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        scanner.nextLine();

                        switch (type3) {
                            case 1:
                                System.out.print("Enter the new director: ");
                                String director = scanner.nextLine();
                                series.get(index).directors.add(director);
                                break;
                            case 2:
                                if (series.get(index).directors.isEmpty()) {
                                    System.out.println("There are no directors to delete!");
                                    return;
                                }
                                System.out.println("Choose the director you want to delete:");
                                Map<Integer, String> directors = new HashMap<Integer, String>();
                                int j = 0;
                                for (String director1 : series.get(index).directors) {
                                    j++;
                                    System.out.println("    " + j + ") " + director1);
                                    directors.put(j, director1);
                                }

                                int index1;
                                try {
                                    index1 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                }
                                scanner.nextLine();

                                if (index1 > j || index1 < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                } else {
                                    series.get(index).directors.remove(directors.get(index1));
                                }
                                break;
                            default:
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        break;
                    case 5:
                        System.out.println("What do you want to do:");
                        System.out.println("    1) Add");
                        System.out.println("    2) Delete");
                        int type4;
                        try {
                            type4 = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        scanner.nextLine();

                        switch (type4) {
                            case 1:
                                System.out.print("Enter the new actor: ");
                                String actor = scanner.nextLine();
                                series.get(index).actors.add(actor);
                                break;
                            case 2:
                                if (series.get(index).actors.isEmpty()) {
                                    System.out.println("There are no actors to delete!");
                                    return;
                                }
                                System.out.println("Choose the actor you want to delete:");
                                Map<Integer, String> actors = new HashMap<Integer, String>();
                                int j = 0;
                                for (String actor1 : series.get(index).actors) {
                                    j++;
                                    System.out.println("    " + j + ") " + actor1);
                                    actors.put(j, actor1);
                                }

                                int index1;
                                try {
                                    index1 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                }
                                scanner.nextLine();

                                if (index1 > j || index1 < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                } else {
                                    series.get(index).actors.remove(actors.get(index1));
                                }
                                break;
                            default:
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        break;
                    case 6:
                        System.out.println("What do you want to do:");
                        System.out.println("    1) Add");
                        System.out.println("    2) Delete");
                        int type5;
                        try {
                            type5 = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        scanner.nextLine();

                        switch (type5) {
                            case 1:
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
                                int genre1;
                                try {
                                    genre1 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                }
                                scanner.nextLine();

                                switch (genre1) {
                                    case 1:
                                        if (!series.get(index).genres.contains(Genre.Action)) {
                                            series.get(index).genres.add(Genre.Action);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 2:
                                        if (!series.get(index).genres.contains(Genre.Adventure)) {
                                            series.get(index).genres.add(Genre.Adventure);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 3:
                                        if (!series.get(index).genres.contains(Genre.Comedy)) {
                                            series.get(index).genres.add(Genre.Comedy);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 4:
                                        if (!series.get(index).genres.contains(Genre.Drama)) {
                                            series.get(index).genres.add(Genre.Drama);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 5:
                                        if (!series.get(index).genres.contains(Genre.Horror)) {
                                            series.get(index).genres.add(Genre.Horror);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 6:
                                        if (!series.get(index).genres.contains(Genre.SF)) {
                                            series.get(index).genres.add(Genre.SF);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 7:
                                        if (!series.get(index).genres.contains(Genre.Fantasy)) {
                                            series.get(index).genres.add(Genre.Fantasy);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 8:
                                        if (!series.get(index).genres.contains(Genre.Romance)) {
                                            series.get(index).genres.add(Genre.Romance);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 9:
                                        if (!series.get(index).genres.contains(Genre.Mystery)) {
                                            series.get(index).genres.add(Genre.Mystery);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 10:
                                        if (!series.get(index).genres.contains(Genre.Thriller)) {
                                            series.get(index).genres.add(Genre.Thriller);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 11:
                                        if (!series.get(index).genres.contains(Genre.Crime)) {
                                            series.get(index).genres.add(Genre.Crime);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 12:
                                        if (!series.get(index).genres.contains(Genre.Biography)) {
                                            series.get(index).genres.add(Genre.Biography);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 13:
                                        if (!series.get(index).genres.contains(Genre.War)) {
                                            series.get(index).genres.add(Genre.War);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    case 14:
                                        if (!series.get(index).genres.contains(Genre.Cooking)) {
                                            series.get(index).genres.add(Genre.Cooking);
                                        } else {
                                            System.out.println("The genre is already added!");
                                        }
                                        break;
                                    default:
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 15.");
                                }
                                break;
                            case 2:
                                if (series.get(index).genres.isEmpty()) {
                                    System.out.println("There are no genres to delete!");
                                    return;
                                }
                                System.out.println("Choose the genre you want to delete:");
                                Map<Integer, Genre> genres = new HashMap<Integer, Genre>();
                                int j = 0;
                                for (Genre genre : series.get(index).genres) {
                                    j++;
                                    System.out.println("    " + j + ") " + genre);
                                    genres.put(j, genre);
                                }

                                int index1;
                                try {
                                    index1 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                }
                                scanner.nextLine();

                                if (index1 > j || index1 < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                } else {
                                    series.get(index).genres.remove(genres.get(index1));
                                }
                                break;
                            default:
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        break;
                    case 7:
                        System.out.println("What do you want to do:");
                        System.out.println("    1) Add");
                        System.out.println("    2) Delete");
                        int type6;
                        try {
                            type6 = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        scanner.nextLine();

                        switch (type6) {
                            case 1:
                                System.out.print("Enter the number of episodes in the new season: ");
                                int noOfEpisodes;
                                try {
                                    noOfEpisodes = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("The number of episodes must be an integer!");
                                }
                                scanner.nextLine();

                                List<Episode> episodes = new ArrayList<Episode>();
                                for (int l = 1; l <= noOfEpisodes; l++) {
                                    System.out.print("Enter the title of episode " + l + ": ");
                                    String episodeTitle = scanner.nextLine();
                                    System.out.print("Enter the duration of episode " + l + " in minutes: ");
                                    int episodeDuration;
                                    try {
                                        episodeDuration = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("The duration must be an integer!");
                                    }
                                    scanner.nextLine();

                                    Episode episode = new Episode();
                                    episode.name = episodeTitle;
                                    episode.duration = episodeDuration;

                                    episodes.add(episode);
                                }

                                series.get(index).noOfSeasons++;
                                series.get(index).addSeason("Season " + series.get(index).noOfSeasons, episodes);
                                break;
                            case 2:
                                if (series.get(index).getSeasons().isEmpty()) {
                                    System.out.println("There are no seasons to delete!");
                                    return;
                                }
                                System.out.println("Choose the season you want to delete:");
                                Map<Integer, String> seasons = new HashMap<Integer, String>();
                                int j = 0;

                                for (String seasonName : series.get(index).getSeasons().keySet()) {
                                    j++;
                                    System.out.println("    " + j + ") " + seasonName);
                                    seasons.put(j, seasonName);
                                }

                                int index1;
                                try {
                                    index1 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                }
                                scanner.nextLine();

                                if (index1 > j || index1 < 1) {
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                } else {
                                    series.get(index).noOfSeasons--;
                                    series.get(index).getSeasons().remove(seasons.get(index1));
                                }
                                break;
                            default:
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                        }
                        break;
                    default:
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 7.");
                }
            }
        }
    }

    public void updateActor() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);
        if (this instanceof Contributor) {
            int k1 = 0;
            for (Object obj : this.contributions) {
                if (obj instanceof Actor) {
                    k1++;
                }
            }
            if (k1 == 0) {
                System.out.println("There are no actors you contributed to!");
                return;
            } else {
                System.out.println("Choose the actor you want to update:");
                Map<Integer, Actor> actors1 = new HashMap<Integer, Actor>();
                int i = 0;
                for (Object obj : this.contributions) {
                    if (obj instanceof Actor) {
                        Actor actor = (Actor) obj;
                        i++;
                        System.out.println("    " + i + ") " + actor.name);
                        actors1.put(i, actor);
                    }
                }

                int index;
                try {
                    index = scanner.nextInt();
                } catch (InputMismatchException e) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    System.out.println("What do you want to update:");
                    System.out.println("    1) Name");
                    System.out.println("    2) Biography");
                    System.out.println("    3) Roles");
                    int type2;
                    try {
                        type2 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                    }
                    scanner.nextLine();

                    switch (type2) {
                        case 1:
                            System.out.print("Enter the new name: ");
                            String name = scanner.nextLine();
                            actors1.get(index).name = name;
                            break;
                        case 2:
                            System.out.print("Enter the new biography: ");
                            String bio = scanner.nextLine();
                            actors1.get(index).bio = bio;
                            break;
                        case 3:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type3;
                            try {
                                type3 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type3) {
                                case 1:
                                    System.out.println("What do you want to add:");
                                    System.out.println("    1) Movie");
                                    System.out.println("    2) Series");
                                    int type4;
                                    try {
                                        type4 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                                    }
                                    scanner.nextLine();

                                    switch (type4) {
                                        case 1:
                                            System.out.print("Enter the title of the movie: ");
                                            String title = scanner.nextLine();
                                            actors1.get(index).roles.put(title, ProductionType.Movie);
                                            break;
                                        case 2:
                                            System.out.print("Enter the title of the series: ");
                                            String title1 = scanner.nextLine();
                                            actors1.get(index).roles.put(title1, ProductionType.Series);
                                            break;
                                        default:
                                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                                    }
                                    break;
                                case 2:
                                    if (actors1.get(index).roles.isEmpty()) {
                                        System.out.println("There are no roles to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the role you want to delete:");
                                    Map<Integer, String> roles = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (Map.Entry<String, ProductionType> entry : actors1.get(index).roles.entrySet()) {
                                        j++;
                                        System.out.println("    " + j + ") " + entry.getKey() + " (" + entry.getValue() + ")");
                                        roles.put(j, entry.getKey());
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        actors1.get(index).roles.remove(roles.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        default:
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                    }
                }
            }
        } else if (this instanceof Admin) {
            int k1 = 0;
            for (Object obj : this.contributions) {
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
                System.out.println("Choose the actor you want to update:");
                Map<Integer, Actor> actors1 = new HashMap<Integer, Actor>();
                int i = 0;
                for (Object obj : this.contributions) {
                    if (obj instanceof Actor) {
                        Actor actor = (Actor) obj;
                        i++;
                        System.out.println("    " + i + ") " + actor.name);
                        actors1.put(i, actor);
                    }
                }
                for (Object obj : Admin.ContributionsHolder.contributions) {
                    if (obj instanceof Actor) {
                        Actor actor = (Actor) obj;
                        i++;
                        System.out.println("    " + i + ") " + actor.name);
                        actors1.put(i, actor);
                    }
                }

                int index;
                try {
                    index = scanner.nextInt();
                } catch (InputMismatchException e) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                }
                scanner.nextLine();

                if (index > i || index < 1) {
                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + i + ".");
                } else {
                    System.out.println("What do you want to update:");
                    System.out.println("    1) Name");
                    System.out.println("    2) Biography");
                    System.out.println("    3) Roles");
                    int type2;
                    try {
                        type2 = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                    }
                    scanner.nextLine();

                    switch (type2) {
                        case 1:
                            System.out.print("Enter the new name: ");
                            String name = scanner.nextLine();
                            actors1.get(index).name = name;
                            break;
                        case 2:
                            System.out.print("Enter the new biography: ");
                            String bio = scanner.nextLine();
                            actors1.get(index).bio = bio;
                            break;
                        case 3:
                            System.out.println("What do you want to do:");
                            System.out.println("    1) Add");
                            System.out.println("    2) Delete");
                            int type3;
                            try {
                                type3 = scanner.nextInt();
                            } catch (InputMismatchException e) {
                                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            scanner.nextLine();

                            switch (type3) {
                                case 1:
                                    System.out.println("What do you want to add:");
                                    System.out.println("    1) Movie");
                                    System.out.println("    2) Series");
                                    int type4;
                                    try {
                                        type4 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                                    }
                                    scanner.nextLine();

                                    switch (type4) {
                                        case 1:
                                            System.out.print("Enter the title of the movie: ");
                                            String title = scanner.nextLine();
                                            actors1.get(index).roles.put(title, ProductionType.Movie);
                                            break;
                                        case 2:
                                            System.out.print("Enter the title of the series: ");
                                            String title1 = scanner.nextLine();
                                            actors1.get(index).roles.put(title1, ProductionType.Series);
                                            break;
                                        default:
                                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                                    }
                                    break;
                                case 2:
                                    if (actors1.get(index).roles.isEmpty()) {
                                        System.out.println("There are no roles to delete!");
                                        return;
                                    }
                                    System.out.println("Choose the role you want to delete:");
                                    Map<Integer, String> roles = new HashMap<Integer, String>();
                                    int j = 0;
                                    for (Map.Entry<String, ProductionType> entry : actors1.get(index).roles.entrySet()) {
                                        j++;
                                        System.out.println("    " + j + ") " + entry.getKey() + " (" + entry.getValue() + ")");
                                        roles.put(j, entry.getKey());
                                    }

                                    int index1;
                                    try {
                                        index1 = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    }
                                    scanner.nextLine();

                                    if (index1 > j || index1 < 1) {
                                        throw new InvalidCommandException("Invalid command! Please enter a number between 1 and " + j + ".");
                                    } else {
                                        actors1.get(index).roles.remove(roles.get(index1));
                                    }
                                    break;
                                default:
                                    throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
                            }
                            break;
                        default:
                            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 3.");
                    }
                }
            }
        }
    }

    public void solveRequest(Request r) throws InvalidCommandException {
        System.out.println("What do you want to do:");
        System.out.println("    1) Accept");
        System.out.println("    2) Reject");
        Scanner scanner = new Scanner(System.in);
        int action;
        try {
            action = scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        User sender = null;
        for (User u : IMDb.getInstance().users) {
            if (u.username.equals(r.senderName)) {
                sender = u;
                break;
            }
        }
        r.solved = true;

        switch (action) {
            case 1:
                r.accepted = true;
                if (r.getRequestType().equals(RequestTypes.DELETE_ACCOUNT)) {
                    Admin a = (Admin) this;
                    a.removeUser(sender);
                    break;
                }
                if (sender instanceof Regular) {
                    Regular r1 = (Regular) sender;
                    if (!r.getRequestType().equals(RequestTypes.OTHERS)) {
                        Context context = new Context(new AcceptedRequestExperienceStrategy());
                        r1.updateExperience(context.executeStrategy());
                    }
                    r1.removeRequest(r);
                } else if (sender instanceof Contributor) {
                    Contributor c = (Contributor) sender;
                    if (!r.getRequestType().equals(RequestTypes.OTHERS)) {
                        Context context = new Context(new AcceptedRequestExperienceStrategy());
                        c.updateExperience(context.executeStrategy());
                    }
                    c.removeRequest(r);
                }
                r.notifyObservers();
                break;
            case 2:
                if (sender instanceof Regular) {
                    Regular r1 = (Regular) sender;
                    r1.removeRequest(r);
                } else if (sender instanceof Contributor) {
                    Contributor c = (Contributor) sender;
                    c.removeRequest(r);
                }
                r.notifyObservers();
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }
}
