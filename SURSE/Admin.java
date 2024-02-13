package org.example;

import java.util.*;

public class Admin extends Staff {
    static class ContributionsHolder {
        static List<Object> contributions = new ArrayList<Object>();

        static void addContribution(Object contribution) {
            contributions.add(contribution);
        }

        static void removeContribution(Object contribution) {
            contributions.remove(contribution);
        }
    }

    static class RequestsHolder {
        static List<Request> requests = new ArrayList<Request>();

        static void addRequest(Request request) {
            requests.add(request);
        }

        static void removeRequest(Request request) {
            requests.remove(request);
        }
    }

    void addUser(User u) {
        IMDb.getInstance().users.add(u);
    }

    void removeUser(User u) {
        IMDb.getInstance().users.remove(u);

        if (u instanceof Contributor) {
            Contributor c = (Contributor) u;
            for (Object o : c.contributions) {
                ContributionsHolder.addContribution(o);
                if (o instanceof Actor) {
                    Actor a = (Actor) o;
                    for (User user : IMDb.getInstance().users) {
                        if (user instanceof Admin) {
                            a.registerObserver(user);
                        }
                    }
                }
                else if (o instanceof Production) {
                    Production p = (Production) o;
                    for (User user : IMDb.getInstance().users) {
                        if (user instanceof Admin) {
                            p.registerObserver(user);
                        }
                    }
                }
            }
        }

        for (Production p : IMDb.getInstance().productions) {
            Iterator<Rating> iterator = p.ratings.iterator();
            while (iterator.hasNext()) {
                Rating rating = iterator.next();
                if (rating.username.equals(u.username)) {
                    iterator.remove();
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
        }

        Iterator<Request> requestIterator = RequestsHolder.requests.iterator();
        while (requestIterator.hasNext()) {
            Request r = requestIterator.next();
            if (r.senderName.equals(u.username)) {
                requestIterator.remove();
            }
        }

        for (User u1 : IMDb.getInstance().users) {
            if (u1 instanceof Staff) {
                Staff s = (Staff) u1;
                Iterator<Object> staffRequestIterator = s.requests.iterator();
                while (staffRequestIterator.hasNext()) {
                    Request r1 = (Request) staffRequestIterator.next();
                    if (r1.senderName.equals(u.username)) {
                        staffRequestIterator.remove();
                    }
                }
            }
        }
    }
}
