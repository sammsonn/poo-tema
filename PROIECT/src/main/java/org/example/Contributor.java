package org.example;

import java.util.*;

public class Contributor extends Staff implements RequestsManager {
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
}
