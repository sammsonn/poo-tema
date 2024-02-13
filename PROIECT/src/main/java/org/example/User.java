package org.example;

import java.time.LocalDateTime;
import java.util.*;

public abstract class User<T extends Comparable<T>> implements Observer {
    static class Information {
        private Credentials credentials;
        private String name;
        private String country;
        private long age;
        private String gender;
        private LocalDateTime birthday;

        public Credentials getCredentials() {
            return credentials;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public long getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public LocalDateTime getBirthday() {
            return birthday;
        }

        private Information(InformationBuilder builder) {
            this.credentials = builder.credentials;
            this.name = builder.name;
            this.country = builder.country;
            this.age = builder.age;
            this.gender = builder.gender;
            this.birthday = builder.birthday;
        }

        public static class InformationBuilder {
            private Credentials credentials;
            private String name;
            private String country;
            private long age;
            private String gender;
            private LocalDateTime birthday;

            public InformationBuilder(String email, String password) throws InformationIncompleteException {
                if (email == null || password == null) {
                    throw new InformationIncompleteException("Email and password cannot be null!");
                }
                credentials = new Credentials(email, password);
            }

            public InformationBuilder name(String name) throws InformationIncompleteException {
                if (name == null) {
                    throw new InformationIncompleteException("Name cannot be null!");
                }
                this.name = name;
                return this;
            }

            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }

            public InformationBuilder age(long age) {
                this.age = age;
                return this;
            }

            public InformationBuilder gender(String gender) {
                this.gender = gender;
                return this;
            }

            public InformationBuilder birthday(LocalDateTime birthday) {
                this.birthday = birthday;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }
    }

    Information info;
    AccountType accountType;
    String username;
    int experience;
    List<String> notifications = new ArrayList<String>();
    SortedSet<T> favorites = new TreeSet<T>();
    List<Rating> ratingsGiven = new ArrayList<Rating>();
    List<Rating> ratingsHistory = new ArrayList<Rating>();

    String getUsername() {
        return username;
    }

    void addFavorite(T favorite) {
        favorites.add(favorite);
    }

    void removeFavorite(T favorite) {
        favorites.remove(favorite);
    }

    void updateExperience(int experience) {
        this.experience += experience;
    }

    String getName() {
        return info.name;
    }

    String getCountry() {
        return info.country;
    }

    long getAge() {
        return info.age;
    }

    String getGender() {
        return info.gender;
    }

    String getBirthDate() {
        return info.birthday.toString();
    }

    String getEmail() {
        return info.credentials.getEmail();
    }

    String getPassword() {
        return info.credentials.getPassword();
    }

    void userLogout() throws InvalidCommandException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to do:");
        System.out.println("    1) Log in again");
        System.out.println("    2) Exit");

        int action;
        try {
            action = scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
        scanner.nextLine();

        switch (action) {
            case 1:
                try {
                    IMDb.getInstance().terminalMode();
                } catch (InvalidCommandException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                IMDb.getInstance().saveData();
                System.exit(0);
                break;
            default:
                throw new InvalidCommandException("Invalid command! Please enter a number between 1 and 2.");
        }
    }

    public void update(Object obj) {
        if (obj instanceof Request) {
            Request r = (Request) obj;
            if (this.username.equals(r.senderName) && r.solved) {
                if (r.accepted) {
                    notifications.add("Your " + r.getRequestType() + " request has been accepted! (" + r.description + ")");
                } else {
                    notifications.add("Your " + r.getRequestType() + " request has been rejected! (" + r.description + ")");
                }
            } else if ((this.username.equals(r.recipientName) && !r.solved) || (r.recipientName.equals("ADMIN") && this.accountType.equals(AccountType.Admin) && !r.solved)) {
                notifications.add("You have received a new " + r.getRequestType() + " request from " + r.senderName + ": " + r.description);
            }
        } else if (obj instanceof Production) {
            Production p = (Production) obj;
            if (this instanceof Regular) {
                for (Rating r : ratingsGiven) {
                    if (r.productionTitle != null && r.productionTitle.equals(p.title)) {
                        notifications.add("The " + p.title + " production which you have rated has received a new review from the user " + p.ratings.get(p.ratings.size() - 1).username + " -> " + p.ratings.get(p.ratings.size() - 1).rating + "!");
                        break;
                    }
                }
            } else if (this instanceof Staff) {
                Staff s = (Staff) this;
                for (Object object : s.contributions) {
                    if (object instanceof Production) {
                        Production p2 = (Production) object;
                        if (p2.title.equals(p.title)) {
                            notifications.add("The " + p.title + " production which you have contributed to has received a new review from the user " + p.ratings.get(p.ratings.size() - 1).username + " -> " + p.ratings.get(p.ratings.size() - 1).rating + "!");
                            break;
                        }
                    }
                }
                if (s instanceof Admin) {
                    Admin a = (Admin) s;
                    for (Object object : Admin.ContributionsHolder.contributions) {
                        if (object instanceof Production) {
                            Production p2 = (Production) object;
                            if (p2.title.equals(p.title)) {
                                notifications.add("The " + p.title + " production which you have contributed to has received a new review from the user " + p.ratings.get(p.ratings.size() - 1).username + " -> " + p.ratings.get(p.ratings.size() - 1).rating + "!");
                                break;
                            }
                        }
                    }
                }
            }
        } else if (obj instanceof Actor) {
            Actor a = (Actor) obj;
            if (this instanceof Regular) {
                for (Rating r : ratingsGiven) {
                    if (r.actorName != null && r.actorName.equals(a.name)) {
                        notifications.add("The actor " + a.name + " which you have rated has received a new review from the user " + a.ratings.get(a.ratings.size() - 1).username + " -> " + a.ratings.get(a.ratings.size() - 1).rating + "!");
                        break;
                    }
                }
            } else if (this instanceof Staff) {
                Staff s = (Staff) this;
                for (Object object : s.contributions) {
                    if (object instanceof Actor) {
                        Actor a2 = (Actor) object;
                        if (a2.name.equals(a.name)) {
                            notifications.add("The actor " + a.name + " which you have contributed to has received a new review from the user " + a.ratings.get(a.ratings.size() - 1).username + " -> " + a.ratings.get(a.ratings.size() - 1).rating + "!");
                            break;
                        }
                    }
                }
                if (s instanceof Admin) {
                    Admin a2 = (Admin) s;
                    for (Object object : Admin.ContributionsHolder.contributions) {
                        if (object instanceof Actor) {
                            Actor a3 = (Actor) object;
                            if (a3.name.equals(a.name)) {
                                notifications.add("The actor " + a.name + " which you have contributed to has received a new review from the user " + a.ratings.get(a.ratings.size() - 1).username + " -> " + a.ratings.get(a.ratings.size() - 1).rating + "!");
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
