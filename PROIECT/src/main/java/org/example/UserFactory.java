package org.example;

public class UserFactory {
    public static User factory(AccountType accountType) {
        switch (accountType) {
            case Regular:
                return new Regular();
            case Contributor:
                return new Contributor();
            case Admin:
                return new Admin();
            default:
                return null;
        }
    }
}
