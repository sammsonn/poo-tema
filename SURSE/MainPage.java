package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class MainPage extends JFrame {

    private JList<String> recommendationList;
    private JButton genreFilterButton;
    private JButton reviewsFilterButton;
    private JButton searchButton;
    private JButton actorsButton;
    private JButton menuButton;
    private Set<Genre> selectedGenres;
    private int minReviews;

    public List<String> getRecommendationsForUser(User user) {
        List<String> recommendations = new ArrayList<>();

        for (Production production : IMDb.getInstance().productions) {
            for (String actor : production.actors) {
                for (Object favoriteActor : user.favorites) {
                    if (favoriteActor instanceof Actor) {
                        if (actor.equals(((Actor) favoriteActor).name)) {
                            if ((selectedGenres == null || production.genres.stream().anyMatch(genre -> selectedGenres.contains(genre))) &&
                                    production.ratings.size() >= minReviews) {
                                if (production instanceof Movie && !user.favorites.contains(production) && !recommendations.contains("Movie: " + production.title)) {
                                    recommendations.add("Movie: " + production.title);
                                } else if (production instanceof Series && !user.favorites.contains(production) && !recommendations.contains("Series: " + production.title)) {
                                    recommendations.add("Series: " + production.title);
                                }
                            }
                        }
                    }
                }
            }

            for (Object favoriteProduction : user.favorites) {
                if (favoriteProduction instanceof Production) {
                    for (Genre genre1 : ((Production) favoriteProduction).genres) {
                        if ((selectedGenres == null || production.genres.stream().anyMatch(genre -> selectedGenres.contains(genre))) && production.genres.contains(genre1) && production.ratings.size() >= minReviews) {
                            if (production instanceof Movie && !user.favorites.contains(production) && !recommendations.contains("Movie: " + production.title)) {
                                recommendations.add("Movie: " + production.title);
                            } else if (production instanceof Series && !user.favorites.contains(production) && !recommendations.contains("Series: " + production.title)) {
                                recommendations.add("Series: " + production.title);
                            }
                        }
                    }
                }
            }
        }

        if (recommendations.isEmpty() && selectedGenres == null && minReviews == 0) {
            List<Production> topProductions = IMDb.getInstance().productions.stream()
                    .sorted(Comparator.comparingDouble(Production::getRanking).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            for (Production p : topProductions) {
                if (p instanceof Movie) {
                    recommendations.add("Movie: " + p.title);
                } else if (p instanceof Series) {
                    recommendations.add("Series: " + p.title);
                }
            }
        }

        return recommendations;
    }

    public MainPage(User user) {
        setTitle("Main Page");
        setSize(700, 600);
        setLayout(new BorderLayout());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                IMDb.getInstance().saveData();
                System.exit(0);
            }
        });

       
        ImageIcon logoIcon = new ImageIcon(new ImageIcon("images/logo.png").getImage().getScaledInstance(200, 100, Image.SCALE_DEFAULT));


        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(logoIcon);


        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.add(logoLabel);


        add(logoPanel, BorderLayout.NORTH);

        recommendationList = new JList<>(getRecommendationsForUser(user).toArray(new String[0]));
        recommendationList.setFont(new Font("Arial", Font.PLAIN, 20));
        recommendationList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    String selectedTitle = recommendationList.getModel().getElementAt(index).split(" ", 2)[1];
                    Production selectedProduction = IMDb.getInstance().productions.stream()
                            .filter(production -> production.title.equals(selectedTitle))
                            .findFirst()
                            .orElse(null);

                    if (selectedProduction != null) {
                        JDialog productionDialog = new JDialog(MainPage.this, selectedTitle, true);
                        productionDialog.setSize(400, 300);
                        productionDialog.setLayout(new BorderLayout());

                        ImageIcon logoIcon = null;
                       
                        if (selectedProduction.title.equals("1917")) {
                            logoIcon = new ImageIcon(new ImageIcon("images/productions/1917.png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                        }
                        else {
                            logoIcon = new ImageIcon(new ImageIcon("images/productions/" + selectedProduction.title.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                        }

                        JLabel logoLabel = new JLabel();
                        logoLabel.setIcon(logoIcon);

                        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        logoPanel.add(logoLabel);

                        productionDialog.add(logoPanel, BorderLayout.NORTH);

                        JTextArea productionInfoArea = new JTextArea(selectedProduction.displayInfo());
                        productionInfoArea.setEditable(false);
                        productionInfoArea.setLineWrap(true);
                        productionInfoArea.setWrapStyleWord(true);
                        productionDialog.add(new JScrollPane(productionInfoArea), BorderLayout.CENTER);

                        JButton addToFavoritesButton = new JButton("Add to Favorites");
                        addToFavoritesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                boolean isFavorite = false;
                                for (Object favorite : user.favorites) {
                                    if (favorite instanceof Production) {
                                        if (((Production) favorite).title.equals(selectedProduction.title)) {
                                            isFavorite = true;
                                            JOptionPane.showMessageDialog(MainPage.this, "This production is already in your favorites", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                }
                                if (!isFavorite) {
                                    user.addFavorite(selectedProduction);
                                    JOptionPane.showMessageDialog(MainPage.this, "Added to favorites: " + selectedProduction.title, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        });

                        JButton rateButton = new JButton("Rate");
                        rateButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                boolean rated = false;
                                for (Production production : IMDb.getInstance().productions) {
                                    if (production.title.equalsIgnoreCase(selectedProduction.title)) {
                                        if (production.ratings.size() > 0) {
                                            for (Rating rating : production.ratings) {
                                                if (rating.username.equals(user.username)) {
                                                    rated = true;
                                                    JOptionPane.showMessageDialog(MainPage.this, "You have already rated this production", "Error", JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!rated) {
                                    JDialog rateDialog = new JDialog(productionDialog, "Rate " + selectedProduction.title, true);
                                    rateDialog.setSize(300, 200);
                                    rateDialog.setLayout(new BorderLayout());

                                    JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
                                    rateDialog.add(ratingSpinner, BorderLayout.NORTH);

                                    JTextArea commentArea = new JTextArea();
                                    commentArea.setLineWrap(true);
                                    commentArea.setWrapStyleWord(true);
                                    rateDialog.add(new JScrollPane(commentArea), BorderLayout.CENTER);

                                    JButton submitButton = new JButton("Submit");
                                    submitButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            Rating rating = new Rating();
                                            rating.username = user.username;
                                            rating.rating = (int) ratingSpinner.getValue();
                                            rating.comment = commentArea.getText();
                                            rating.productionTitle = selectedProduction.title;

                                            selectedProduction.ratings.add(rating);
                                            user.ratingsGiven.add(rating);

                                            int sum = 0, count = 0;
                                            for (Rating r : selectedProduction.ratings) {
                                                sum += r.rating;
                                                count++;
                                            }
                                            selectedProduction.ranking = (double) sum / count;

                                            boolean alreadyRated = false;
                                            for (Object r : user.ratingsHistory) {
                                                Rating r1 = (Rating) r;
                                                if (r1.productionTitle.equals(selectedProduction.title)) {
                                                    alreadyRated = true;
                                                    break;
                                                }
                                            }
                                            user.ratingsHistory.add(rating);

                                            if (!alreadyRated) {
                                                Context context = new Context(new AddRatingExperienceStrategy());
                                                user.updateExperience(context.executeStrategy());
                                            }

                                            selectedProduction.notifyObservers();
                                            selectedProduction.registerObserver(user);

                                            rateDialog.dispose();
                                        }
                                    });
                                    rateDialog.add(submitButton, BorderLayout.SOUTH);

                                    rateDialog.setLocationRelativeTo(productionDialog);
                                    rateDialog.setVisible(true);
                                }
                            }
                        });

                        JButton viewTrailerButton = new JButton("View Trailer");
                        viewTrailerButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Desktop.getDesktop().browse(new URI(selectedProduction.trailerLink));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(addToFavoritesButton);
                        buttonPanel.add(rateButton);
                        buttonPanel.add(viewTrailerButton);
                        productionDialog.add(buttonPanel, BorderLayout.SOUTH);

                        productionDialog.setLocationRelativeTo(MainPage.this);
                        productionDialog.setVisible(true);
                    }
                }
            }
        });
        add(new JScrollPane(recommendationList), BorderLayout.CENTER);

        genreFilterButton = new JButton("Filter by Genre");
        genreFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog genreDialog = new JDialog(MainPage.this, "Select Genre", true);
                genreDialog.setSize(200, 350);
                genreDialog.setLayout(new BorderLayout());


                Set<Genre> allGenres = IMDb.getInstance().productions.stream()
                        .flatMap(production -> production.genres.stream())
                        .collect(Collectors.toSet());

                JList<Genre> genreList = new JList<>(allGenres.toArray(new Genre[0]));
                genreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                genreDialog.add(new JScrollPane(genreList), BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedGenres = new HashSet<>(genreList.getSelectedValuesList());
                        recommendationList.setListData(getRecommendationsForUser(user).toArray(new String[0]));
                        genreDialog.dispose();
                    }
                });
                genreDialog.add(okButton, BorderLayout.SOUTH);

                genreDialog.setLocationRelativeTo(MainPage.this);
                genreDialog.setVisible(true);
            }
        });

        reviewsFilterButton = new JButton("Filter by Reviews");
        reviewsFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog reviewsDialog = new JDialog(MainPage.this, "Enter Minimum Number of Reviews", true);
                reviewsDialog.setSize(300, 100);
                reviewsDialog.setLayout(new BorderLayout());

                JTextField reviewsField = new JTextField();
                reviewsDialog.add(reviewsField, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            minReviews = Integer.parseInt(reviewsField.getText());
                            recommendationList.setListData(getRecommendationsForUser(user).toArray(new String[0]));
                            reviewsDialog.dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(reviewsDialog, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                reviewsDialog.add(okButton, BorderLayout.SOUTH);

                reviewsDialog.setLocationRelativeTo(MainPage.this);
                reviewsDialog.setVisible(true);
            }
        });

        actorsButton = new JButton("Actors");
        actorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame actorsFrame = new JFrame("Actors");
                actorsFrame.setSize(300, 350);
                actorsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                actorsFrame.setLayout(new BorderLayout());

                JLabel titleLabel = new JLabel("Actors", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                actorsFrame.add(titleLabel, BorderLayout.NORTH);

                List<String> actorNames = IMDb.getInstance().actors.stream()
                        .map(actor -> actor.name)
                        .sorted()
                        .collect(Collectors.toList());

                JList<String> actorsList = new JList<>(actorNames.toArray(new String[0]));
                actorsFrame.add(new JScrollPane(actorsList), BorderLayout.CENTER);

                actorsList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        JList list = (JList) evt.getSource();
                        if (evt.getClickCount() == 2) {
                            int index = list.locationToIndex(evt.getPoint());
                            String selectedName = actorsList.getModel().getElementAt(index);
                            Actor selectedActor = IMDb.getInstance().actors.stream()
                                    .filter(actor -> actor.name.equals(selectedName))
                                    .findFirst()
                                    .orElse(null);

                            if (selectedActor != null) {
                                JDialog actorDialog = new JDialog(MainPage.this, selectedName, true);
                                actorDialog.setSize(400, 300);
                                actorDialog.setLayout(new BorderLayout());

                                ImageIcon logoIcon = new ImageIcon(new ImageIcon("images/actors/" + selectedActor.name.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));

                                JLabel logoLabel = new JLabel();
                                logoLabel.setIcon(logoIcon);

                                JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                                logoPanel.add(logoLabel);

                                actorDialog.add(logoPanel, BorderLayout.NORTH);

                                JTextArea actorInfoArea = new JTextArea(selectedActor.displayInfo());
                                actorInfoArea.setEditable(false);
                                actorInfoArea.setLineWrap(true);
                                actorInfoArea.setWrapStyleWord(true);
                                actorDialog.add(new JScrollPane(actorInfoArea), BorderLayout.CENTER);

                                JButton addToFavoritesButton = new JButton("Add to Favorites");
                                addToFavoritesButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        boolean isFavorite = false;
                                        for (Object favorite : user.favorites) {
                                            if (favorite instanceof Actor) {
                                                if (((Actor) favorite).name.equals(selectedActor.name)) {
                                                    isFavorite = true;
                                                    JOptionPane.showMessageDialog(MainPage.this, "This actor is already in your favorites", "Error", JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                        }
                                        if (!isFavorite) {
                                            user.addFavorite(selectedActor);
                                            JOptionPane.showMessageDialog(MainPage.this, "Added to favorites: " + selectedActor.name, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                });
                                actorDialog.add(addToFavoritesButton, BorderLayout.SOUTH);

                                actorDialog.setLocationRelativeTo(MainPage.this);
                                actorDialog.setVisible(true);
                            }
                        }
                    }
                });

                actorsFrame.setLocationRelativeTo(MainPage.this);
                actorsFrame.setVisible(true);
            }
        });

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog searchDialog = new JDialog(MainPage.this, "Search", true);
                searchDialog.setSize(250, 100);
                searchDialog.setLayout(new BorderLayout());

                JTextField searchField = new JTextField();
                searchDialog.add(searchField, BorderLayout.NORTH);

                JButton searchButton = new JButton("Search");
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String query = searchField.getText();
                        Actor selectedActor = IMDb.getInstance().actors.stream()
                                .filter(actor -> actor.name.equalsIgnoreCase(query))
                                .findFirst()
                                .orElse(null);

                        Production selectedProduction = IMDb.getInstance().productions.stream()
                                .filter(production -> production.title.equalsIgnoreCase(query))
                                .findFirst()
                                .orElse(null);

                        if (selectedActor != null) {
                            JDialog actorDialog = new JDialog(MainPage.this, selectedActor.name, true);
                            actorDialog.setSize(400, 300);
                            actorDialog.setLayout(new BorderLayout());

                            ImageIcon logoIcon = new ImageIcon(new ImageIcon("images/actors/" + selectedActor.name.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));

                            JLabel logoLabel = new JLabel();
                            logoLabel.setIcon(logoIcon);

                            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                            logoPanel.add(logoLabel);

                            actorDialog.add(logoPanel, BorderLayout.NORTH);

                            JTextArea actorInfoArea = new JTextArea(selectedActor.displayInfo());
                            actorInfoArea.setEditable(false);
                            actorInfoArea.setLineWrap(true);
                            actorInfoArea.setWrapStyleWord(true);
                            actorDialog.add(new JScrollPane(actorInfoArea), BorderLayout.CENTER);

                            JButton addToFavoritesButton = new JButton("Add to Favorites");
                            addToFavoritesButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    boolean isFavorite = false;
                                    for (Object favorite : user.favorites) {
                                        if (favorite instanceof Actor) {
                                            if (((Actor) favorite).name.equals(selectedActor.name)) {
                                                isFavorite = true;
                                                JOptionPane.showMessageDialog(MainPage.this, "This actor is already in your favorites", "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                    if (!isFavorite) {
                                        user.addFavorite(selectedActor);
                                        JOptionPane.showMessageDialog(MainPage.this, "Added to favorites: " + selectedActor.name, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            });
                            actorDialog.add(addToFavoritesButton, BorderLayout.SOUTH);

                            actorDialog.setLocationRelativeTo(MainPage.this);
                            actorDialog.setVisible(true);
                        } else if (selectedProduction != null) {
                            JDialog productionDialog = new JDialog(MainPage.this, selectedProduction.title, true);
                            productionDialog.setSize(400, 300);
                            productionDialog.setLayout(new BorderLayout());

                            ImageIcon logoIcon = null;

                            if (selectedProduction.title.equals("1917")) {
                                logoIcon = new ImageIcon(new ImageIcon("images/productions/1917.png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                            }
                            else {
                                logoIcon = new ImageIcon(new ImageIcon("images/productions/" + selectedProduction.title.toLowerCase() + ".png").getImage().getScaledInstance(160, 90, Image.SCALE_DEFAULT));
                            }

                            JLabel logoLabel = new JLabel();
                            logoLabel.setIcon(logoIcon);

                            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                            logoPanel.add(logoLabel);

                            productionDialog.add(logoPanel, BorderLayout.NORTH);

                            JTextArea productionInfoArea = new JTextArea(selectedProduction.displayInfo());
                            productionInfoArea.setEditable(false);
                            productionInfoArea.setLineWrap(true);
                            productionInfoArea.setWrapStyleWord(true);
                            productionDialog.add(new JScrollPane(productionInfoArea), BorderLayout.CENTER);

                            JButton addToFavoritesButton = new JButton("Add to Favorites");
                            addToFavoritesButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    boolean isFavorite = false;
                                    for (Object favorite : user.favorites) {
                                        if (favorite instanceof Production) {
                                            if (((Production) favorite).title.equals(selectedProduction.title)) {
                                                isFavorite = true;
                                                JOptionPane.showMessageDialog(MainPage.this, "This production is already in your favorites", "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                    if (!isFavorite) {
                                        user.addFavorite(selectedProduction);
                                        JOptionPane.showMessageDialog(MainPage.this, "Added to favorites: " + selectedProduction.title, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            });

                            JButton rateButton = new JButton("Rate");
                            rateButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    boolean rated = false;
                                    for (Production production : IMDb.getInstance().productions) {
                                        if (production.title.equalsIgnoreCase(selectedProduction.title)) {
                                            if (production.ratings.size() > 0) {
                                                for (Rating rating : production.ratings) {
                                                    if (rating.username.equals(user.username)) {
                                                        rated = true;
                                                        JOptionPane.showMessageDialog(MainPage.this, "You have already rated this production", "Error", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!rated) {
                                        JDialog rateDialog = new JDialog(productionDialog, "Rate " + selectedProduction.title, true);
                                        rateDialog.setSize(300, 200);
                                        rateDialog.setLayout(new BorderLayout());

                                        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
                                        rateDialog.add(ratingSpinner, BorderLayout.NORTH);

                                        JTextArea commentArea = new JTextArea();
                                        commentArea.setLineWrap(true);
                                        commentArea.setWrapStyleWord(true);
                                        rateDialog.add(new JScrollPane(commentArea), BorderLayout.CENTER);

                                        JButton submitButton = new JButton("Submit");
                                        submitButton.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                Rating rating = new Rating();
                                                rating.username = user.username;
                                                rating.rating = (int) ratingSpinner.getValue();
                                                rating.comment = commentArea.getText();
                                                rating.productionTitle = selectedProduction.title;

                                                selectedProduction.ratings.add(rating);
                                                user.ratingsGiven.add(rating);

                                                int sum = 0, count = 0;
                                                for (Rating r : selectedProduction.ratings) {
                                                    sum += r.rating;
                                                    count++;
                                                }
                                                selectedProduction.ranking = (double) sum / count;

                                                boolean alreadyRated = false;
                                                for (Object r : user.ratingsHistory) {
                                                    Rating r1 = (Rating) r;
                                                    if (r1.productionTitle.equals(selectedProduction.title)) {
                                                        alreadyRated = true;
                                                        break;
                                                    }
                                                }
                                                user.ratingsHistory.add(rating);

                                                if (!alreadyRated) {
                                                    Context context = new Context(new AddRatingExperienceStrategy());
                                                    user.updateExperience(context.executeStrategy());
                                                }

                                                selectedProduction.notifyObservers();
                                                selectedProduction.registerObserver(user);

                                                rateDialog.dispose();
                                            }
                                        });
                                        rateDialog.add(submitButton, BorderLayout.SOUTH);

                                        rateDialog.setLocationRelativeTo(productionDialog);
                                        rateDialog.setVisible(true);
                                    }
                                }
                            });

                            JButton viewTrailerButton = new JButton("View Trailer");
                            viewTrailerButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    try {
                                        Desktop.getDesktop().browse(new URI(selectedProduction.trailerLink));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                            JPanel buttonPanel = new JPanel();
                            buttonPanel.add(addToFavoritesButton);
                            buttonPanel.add(rateButton);
                            buttonPanel.add(viewTrailerButton);
                            productionDialog.add(buttonPanel, BorderLayout.SOUTH);

                            productionDialog.setLocationRelativeTo(MainPage.this);
                            productionDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(MainPage.this, "No results found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                searchDialog.add(searchButton, BorderLayout.SOUTH);

                searchDialog.setLocationRelativeTo(MainPage.this);
                searchDialog.setVisible(true);
            }
        });

        menuButton = new JButton("Menu");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuPage(user);
                dispose();
            }
        });

        JPanel buttonPanelLeft = new JPanel();
        buttonPanelLeft.add(genreFilterButton);
        buttonPanelLeft.add(reviewsFilterButton);

        JPanel buttonPanelRight = new JPanel();
        buttonPanelRight.add(actorsButton);
        buttonPanelRight.add(searchButton);

        JPanel buttonPanelDown = new JPanel();
        buttonPanelDown.add(menuButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        buttonPanel.add(buttonPanelRight, BorderLayout.EAST);
        buttonPanel.add(buttonPanelDown, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}