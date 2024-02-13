package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuPage extends JFrame {
    JLabel welcomeLabel;

    void refreshWelcomeLabel(User user) {
        if (user instanceof Regular) {
            welcomeLabel.setText("Welcome back user " + user.username + "! User experience: " + user.experience);
        } else if (user instanceof Contributor) {
            welcomeLabel.setText("Welcome back user " + user.username + "! User experience: " + user.experience);
        } else if (user instanceof Admin) {
            welcomeLabel.setText("Welcome back user " + user.username + "! User experience: -");
        }
        welcomeLabel.revalidate();
        welcomeLabel.repaint();
    }
    public MenuPage(User user) {
        setSize(700, 500);
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        if (user instanceof Regular) {
            setTitle("Menu - Regular");
            buttonPanel.setLayout(new GridLayout(4, 1));
            welcomeLabel = new JLabel("Welcome back user " + user.username + "! User experience: " + user.experience, SwingConstants.CENTER);
            add(welcomeLabel, BorderLayout.NORTH);
        } else if (user instanceof Contributor) {
            setTitle("Menu - Contributor");
            buttonPanel.setLayout(new GridLayout(5, 1));
            welcomeLabel = new JLabel("Welcome back user " + user.username + "! User experience: " + user.experience, SwingConstants.CENTER);
            add(welcomeLabel, BorderLayout.NORTH);
        } else if (user instanceof Admin) {
            setTitle("Menu - Admin");
            buttonPanel.setLayout(new GridLayout(5, 1));
            welcomeLabel = new JLabel("Welcome back user " + user.username + "! User experience: -", SwingConstants.CENTER);
            add(welcomeLabel, BorderLayout.NORTH);
        }

        JButton viewProductionsButton = new JButton("View productions details");
        viewProductionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new ProductionsPage(user);
            }
        });
        buttonPanel.add(viewProductionsButton);

        JButton viewActorsButton = new JButton("View actors details");
        viewActorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new ActorsPage();
            }
        });
        buttonPanel.add(viewActorsButton);

        JButton viewNotificationsButton = new JButton("View notifications");
        viewNotificationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new NotificationsPage(user);
            }
        });
        buttonPanel.add(viewNotificationsButton);

        JButton searchButton = new JButton("Search for actor/movie/series");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new SearchPage();
            }
        });
        buttonPanel.add(searchButton);

        JButton modifyFavoritesButton = new JButton("Add/Delete actor/movie/series to/from favorites");
        modifyFavoritesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModifyFavoritesPage(user);
            }
        });
        buttonPanel.add(modifyFavoritesButton);

        if (user.accountType == AccountType.Regular) {
            JButton createWithdrawRequestButton = new JButton("Create/Withdraw request");
            createWithdrawRequestButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ModifyRequestsPage(user);
                }
            });
            buttonPanel.add(createWithdrawRequestButton);

            JButton addDeleteProductionRatingButton = new JButton("Add/Delete production/actor rating");
            addDeleteProductionRatingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ModifyRatingsPage(user, MenuPage.this);
                }
            });
            buttonPanel.add(addDeleteProductionRatingButton);
        } else if (user.accountType == AccountType.Contributor) {
            JButton createWithdrawRequestButton = new JButton("Create/Withdraw request");
            createWithdrawRequestButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new ModifyRequestsPage(user);
                }
            });
            buttonPanel.add(createWithdrawRequestButton);

            JButton addDeleteActorMovieSeriesButton = new JButton("Add/Delete actor/movie/series from system");
            addDeleteActorMovieSeriesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new ModifySystemPage(user, MenuPage.this);
                }
            });
            buttonPanel.add(addDeleteActorMovieSeriesButton);

            JButton solveRequestButton = new JButton("Solve a request");
            solveRequestButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new SolveRequestPage(user, MenuPage.this);
                }
            });
            buttonPanel.add(solveRequestButton);

            JButton updateActorMovieSeriesButton = new JButton("Update actor/movie/series details");
            updateActorMovieSeriesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new UpdateSystemPage(user);
                }
            });
            buttonPanel.add(updateActorMovieSeriesButton);
        } else if (user.accountType == AccountType.Admin) {
            JButton addDeleteActorMovieSeriesButton = new JButton("Add/Delete actor/movie/series from system");
            addDeleteActorMovieSeriesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new ModifySystemPage(user, MenuPage.this);
                }
            });
            buttonPanel.add(addDeleteActorMovieSeriesButton);

            JButton solveRequestButton = new JButton("Solve a request");
            solveRequestButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new SolveRequestPage(user, MenuPage.this);
                }
            });
            buttonPanel.add(solveRequestButton);

            JButton updateActorMovieSeriesButton = new JButton("Update actor/movie/series details");
            updateActorMovieSeriesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new UpdateSystemPage(user);
                }
            });
            buttonPanel.add(updateActorMovieSeriesButton);

            JButton addDeleteUserButton = new JButton("Add/Delete user");
            addDeleteUserButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new ModifyUsersPage(user);
                }
            });
            buttonPanel.add(addDeleteUserButton);
        }

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(MenuPage.this, "Do you want to log in again?", "Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    try {
                        new LoginPage();
                    } catch (ClassNotFoundException exception) {
                    }
                    dispose();
                } else {
                    IMDb.getInstance().saveData();
                    System.exit(0);
                }
            }
        });
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new MainPage(user);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}