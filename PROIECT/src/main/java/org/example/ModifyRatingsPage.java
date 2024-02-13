package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ModifyRatingsPage extends JFrame {

    private JList<String> ratingsList;
    private User user;

    public ModifyRatingsPage(User user, MenuPage menuPage) {
        this.user = user;
        setTitle("Modify Ratings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ratings:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        updateRatingsList();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add Rating");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRating(menuPage);
            }
        });
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove Rating");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeRating(user);
            }
        });
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JScrollPane scrollPane;

    private void updateRatingsList() {
        List<String> ratings = new ArrayList<>();
        for (Object rating : user.ratingsGiven) {
            if (rating instanceof Rating) {
                Rating rating1 = (Rating) rating;
                if (rating1.actorName != null) {
                    ratings.add(rating1.actorName + ": " + rating1.rating + " (" + rating1.comment + ")");
                } else if (rating1.productionTitle != null) {
                    ratings.add(rating1.productionTitle + ": " + rating1.rating + " (" + rating1.comment + ")");
                }
            }
        }

        if (scrollPane != null) {
            remove(scrollPane);
        }

        ratingsList = new JList<>(ratings.toArray(new String[0]));
        scrollPane = new JScrollPane(ratingsList);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addRating(MenuPage menuPage) {
        JDialog dialog = new JDialog(this, "Choose an option", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(2, 1));

       
        JButton actorButton = new JButton("Rate an Actor");
        JButton productionButton = new JButton("Rate a Production");

       
        actorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String actorName = JOptionPane.showInputDialog(dialog, "Enter the name of the actor to rate:");
                if (actorName != null && !actorName.isEmpty()) {
                    IMDb imdb = IMDb.getInstance();
                    boolean actorExists = false;
                    Actor selectedActor = null;
                    for (Actor a : imdb.actors) {
                        if (a.name.equalsIgnoreCase(actorName)) {
                            selectedActor = a;
                            actorName = a.name;
                            actorExists = true;
                            break;
                        }
                    }
                    if (!actorExists) {
                        JOptionPane.showMessageDialog(dialog, "No actor found with the given name.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ratingValue = JOptionPane.showInputDialog(dialog, "Enter the rating value (1-10):");
                    int rating;
                    try {
                        rating = Integer.parseInt(ratingValue);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid rating value.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (rating < 1 || rating > 10) {
                        JOptionPane.showMessageDialog(dialog, "Rating value must be between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ratingComment = JOptionPane.showInputDialog(dialog, "Enter the rating comment:");
                    if (ratingComment != null && !ratingComment.isEmpty() && ratingValue != null && !ratingValue.isEmpty()) {
                        try {
                            Rating newRating = new Rating();
                            newRating.actorName = actorName;
                            newRating.rating = rating;
                            newRating.username = user.username;
                            newRating.comment = ratingComment;

                            user.ratingsGiven.add(newRating);
                            selectedActor.ratings.add(newRating);

                            int sum = 0, count = 0;
                            for (Rating r : selectedActor.ratings) {
                                sum += r.rating;
                                count++;
                            }
                            selectedActor.ranking = (double) sum / count;

                            boolean alreadyRated = false;
                            for (Object r : user.ratingsHistory) {
                                Rating r1 = (Rating) r;
                                if (r1.actorName != null && r1.actorName.equals(selectedActor.name)) {
                                    alreadyRated = true;
                                    break;
                                }
                            }
                            user.ratingsHistory.add(newRating);

                            if (!alreadyRated) {
                                Context context = new Context(new AddRatingExperienceStrategy());
                                user.updateExperience(context.executeStrategy());
                                menuPage.refreshWelcomeLabel(user);
                            }

                            selectedActor.notifyObservers();
                            selectedActor.registerObserver(user);

                            updateRatingsList();
                        } catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(dialog, "Invalid rating value.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                dialog.dispose();
            }
        });

        productionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                String productionTitle = JOptionPane.showInputDialog(dialog, "Enter the title of the production to rate:");
                if (productionTitle != null && !productionTitle.isEmpty()) {
                    IMDb imdb = IMDb.getInstance();
                    boolean productionExists = false;
                    Production selectedProduction = null;
                    for (Production p : imdb.productions) {
                        if (p.title.equalsIgnoreCase(productionTitle)) {
                            selectedProduction = p;
                            productionTitle = p.title;
                            productionExists = true;
                            break;
                        }
                    }
                    if (!productionExists) {
                        JOptionPane.showMessageDialog(dialog, "No production found with the given title.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ratingValue = JOptionPane.showInputDialog(dialog, "Enter the rating value (1-10):");
                    int rating;
                    try {
                        rating = Integer.parseInt(ratingValue);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid rating value.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (rating < 1 || rating > 10) {
                        JOptionPane.showMessageDialog(dialog, "Rating value must be between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String ratingComment = JOptionPane.showInputDialog(dialog, "Enter the rating comment:");
                    if (ratingComment != null && !ratingComment.isEmpty() && ratingValue != null && !ratingValue.isEmpty()) {
                        try {
                            Rating newRating = new Rating();
                            newRating.productionTitle = productionTitle;
                            newRating.rating = rating;
                            newRating.username = user.username;
                            newRating.comment = ratingComment;

                            user.ratingsGiven.add(newRating);
                            selectedProduction.ratings.add(newRating);

                            int sum = 0, count = 0;
                            for (Rating r : selectedProduction.ratings) {
                                sum += r.rating;
                                count++;
                            }
                            selectedProduction.ranking = (double) sum / count;

                            boolean alreadyRated = false;
                            for (Object r : user.ratingsHistory) {
                                Rating r1 = (Rating) r;
                                if (r1.productionTitle != null && r1.productionTitle.equals(selectedProduction.title)) {
                                    alreadyRated = true;
                                    break;
                                }
                            }
                            user.ratingsHistory.add(newRating);

                            if (!alreadyRated) {
                                Context context = new Context(new AddRatingExperienceStrategy());
                                user.updateExperience(context.executeStrategy());
                                menuPage.refreshWelcomeLabel(user);
                            }

                            selectedProduction.notifyObservers();
                            selectedProduction.registerObserver(user);

                            updateRatingsList();
                        } catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(dialog, "Invalid rating value.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                dialog.dispose();
            }
        });

       
        dialog.add(actorButton);
        dialog.add(productionButton);

       
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void removeRating(User user) {
        String selectedRating = ratingsList.getSelectedValue();
        if (selectedRating != null) {
            Rating toRemove = null;
            for (Object rating : user.ratingsGiven) {
                if (rating instanceof Rating) {
                    Rating rating1 = (Rating) rating;
                    if ((rating1.productionTitle + ": " + rating1.rating + " (" + rating1.comment + ")").equals(selectedRating)) {
                        toRemove = rating1;
                        break;
                    }
                    else if ((rating1.actorName + ": " + rating1.rating + " (" + rating1.comment + ")").equals(selectedRating)) {
                        toRemove = rating1;
                        break;
                    }
                }
            }
            if (toRemove != null) {
                user.ratingsGiven.remove(toRemove);
                for (Production p : IMDb.getInstance().productions) {
                    if (p.title.equals(toRemove.productionTitle)) {
                        p.ratings.remove(toRemove);
                        p.removeObserver(user);
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

                for (Actor a : IMDb.getInstance().actors) {
                    if (a.name.equals(toRemove.actorName)) {
                        a.ratings.remove(toRemove);
                        a.removeObserver(user);
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
                JOptionPane.showMessageDialog(this, "Rating removed successfully.");
                updateRatingsList();
            }
        }
    }
}