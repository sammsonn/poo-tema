package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModifySystemPage extends JFrame {

    private User user;

    public ModifySystemPage(User user, MenuPage menuPage) {
        this.user = user;
        setTitle("Modify System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddPage(user, menuPage);
            }
        });
        add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeletePage(user);
            }
        });
        add(deleteButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class AddPage extends JFrame {
    public AddPage(User user, MenuPage menuPage) {
        setTitle("Add");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 3));

        JButton actorButton = new JButton("Actor");
        actorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddActorPage(user, menuPage);
            }
        });
        add(actorButton);

        JButton movieButton = new JButton("Movie");
        movieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMoviePage(user, menuPage);
            }
        });
        add(movieButton);

        JButton seriesButton = new JButton("Series");
        seriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSeriesPage(user, menuPage);
            }
        });
        add(seriesButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class AddActorPage extends JFrame {
    public AddActorPage(User user, MenuPage menuPage) {
        setTitle("Add Actor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JTextField nameField = new JTextField();
        add(new JLabel("Name:"));
        add(nameField);

        JTextField bioField = new JTextField();
        add(new JLabel("Bio:"));
        add(bioField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isEmpty() || bioField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(AddActorPage.this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String name = nameField.getText();
                String bio = bioField.getText();

                IMDb imdb = IMDb.getInstance();
                Actor actor = new Actor();
                actor.name = name;
                actor.bio = bio;

                Staff staff = (Staff) user;
                staff.addActorSystem(actor);
                staff.contributions.add(actor);
                if (!(staff instanceof Admin)) {
                    Context context = new Context(new AddContributionExperienceStrategy());
                    staff.updateExperience(context.executeStrategy());
                    menuPage.refreshWelcomeLabel(staff);
                }
                actor.registerObserver(staff);

                JOptionPane.showMessageDialog(AddActorPage.this, "New actor added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });
        add(submitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class AddMoviePage extends JFrame {
    public AddMoviePage(User user, MenuPage menuPage) {
        setTitle("Add Movie");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(9, 2));

        JTextField titleField = new JTextField();
        add(new JLabel("Title:"));
        add(titleField);

        JTextField directorField = new JTextField();
        add(new JLabel("Director:"));
        add(directorField);

        JTextField actorField = new JTextField();
        add(new JLabel("Actor:"));
        add(actorField);

        JTextField genreField = new JTextField();
        add(new JLabel("Genre:"));
        add(genreField);

        JTextField descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        JTextField durationField = new JTextField();
        add(new JLabel("Duration (in minutes):"));
        add(durationField);

        JTextField yearField = new JTextField();
        add(new JLabel("Year:"));
        add(yearField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (titleField.getText().isEmpty() || directorField.getText().isEmpty() || actorField.getText().isEmpty() || genreField.getText().isEmpty() || descriptionField.getText().isEmpty() || durationField.getText().isEmpty() || yearField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(AddMoviePage.this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String title = titleField.getText();
                    String director = directorField.getText();
                    String actor = actorField.getText();
                    Genre genre = Genre.valueOf(genreField.getText());
                    String description = descriptionField.getText();
                    int duration = Integer.parseInt(durationField.getText());
                    int year = Integer.parseInt(yearField.getText());

                    IMDb imdb = IMDb.getInstance();
                    Movie movie = new Movie();
                    movie.title = title;
                    movie.directors.add(director);
                    movie.actors.add(actor);
                    movie.genres.add(genre);
                    movie.description = description;
                    movie.duration = duration;
                    movie.year = year;

                    String[] words = movie.title.split(" ");
                    String trailerLink = "https://www.youtube.com/results?search_query=";
                    for (String word : words) {
                        trailerLink += word + "+";
                    }
                    trailerLink += "trailer";
                    movie.trailerLink = trailerLink;

                    Staff staff = (Staff) user;
                    staff.addProductionSystem(movie);
                    staff.contributions.add(movie);
                    if (!(staff instanceof Admin)) {
                        Context context = new Context(new AddContributionExperienceStrategy());
                        staff.updateExperience(context.executeStrategy());
                        menuPage.refreshWelcomeLabel(staff);
                    }
                    movie.registerObserver(staff);
                    JOptionPane.showMessageDialog(AddMoviePage.this, "New movie added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AddMoviePage.this, "Invalid number input for duration or year.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AddMoviePage.this, "Invalid genre.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(submitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class AddSeriesPage extends JFrame {
    public AddSeriesPage(User user, MenuPage menuPage) {
        setTitle("Add Series");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(9, 2));

        JTextField titleField = new JTextField();
        add(new JLabel("Title:"));
        add(titleField);

        JTextField directorField = new JTextField();
        add(new JLabel("Director:"));
        add(directorField);

        JTextField actorField = new JTextField();
        add(new JLabel("Actor:"));
        add(actorField);

        JTextField genreField = new JTextField();
        add(new JLabel("Genre:"));
        add(genreField);

        JTextField descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        JTextField yearField = new JTextField();
        add(new JLabel("Year:"));
        add(yearField);

        JTextField noOfSeasonsField = new JTextField();
        add(new JLabel("Number of Seasons:"));
        add(noOfSeasonsField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (titleField.getText().isEmpty() || directorField.getText().isEmpty() || actorField.getText().isEmpty() || genreField.getText().isEmpty() || descriptionField.getText().isEmpty() || yearField.getText().isEmpty() || noOfSeasonsField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(AddSeriesPage.this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String title = titleField.getText();
                    String director = directorField.getText();
                    String actor = actorField.getText();
                    Genre genre = Genre.valueOf(genreField.getText());
                    String description = descriptionField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    int noOfSeasons = Integer.parseInt(noOfSeasonsField.getText());

                    IMDb imdb = IMDb.getInstance();
                    Series series = new Series();
                    series.title = title;
                    series.directors.add(director);
                    series.actors.add(actor);
                    series.genres.add(genre);
                    series.description = description;
                    series.year = year;
                    series.noOfSeasons = noOfSeasons;

                    String[] words1 = series.title.split(" ");
                    String trailerLink1 = "https://www.youtube.com/results?search_query=";
                    for (String word : words1) {
                        trailerLink1 += word + "+";
                    }
                    trailerLink1 += "trailer";
                    series.trailerLink = trailerLink1;

                    Staff staff = (Staff) user;
                    staff.addProductionSystem(series);
                    staff.contributions.add(series);
                    if (!(staff instanceof Admin)) {
                        Context context = new Context(new AddContributionExperienceStrategy());
                        staff.updateExperience(context.executeStrategy());
                        menuPage.refreshWelcomeLabel(staff);
                    }
                    series.registerObserver(staff);
                    JOptionPane.showMessageDialog(AddSeriesPage.this, "New series added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AddSeriesPage.this, "Invalid number for duration or year.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AddSeriesPage.this, "Invalid genre.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(submitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class DeletePage extends JFrame {
    public DeletePage(User user) {
        setTitle("Delete");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            for (Object contribution : contributor.contributions) {
                String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                listModel.addElement(contributionName);
            }
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            for (Object contribution : admin.contributions) {
                String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                listModel.addElement(contributionName);
            }
            for (Object contribution : Admin.ContributionsHolder.contributions) {
                String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                listModel.addElement(contributionName);
            }
        }
        JList<String> contributionsList = new JList<>(listModel);
        add(new JScrollPane(contributionsList), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedContribution = contributionsList.getSelectedValue();
                if (selectedContribution != null) {
                    IMDb imdb = IMDb.getInstance();
                    if (user instanceof Contributor) {
                        Contributor contributor = (Contributor) user;
                        Object contributionToRemove = null;
                        for (Object contribution : contributor.contributions) {
                            String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                            if (contributionName.equals(selectedContribution)) {
                                contributionToRemove = contribution;
                                break;
                            }
                        }
                        if (contributionToRemove != null) {
                            if (contributionToRemove instanceof Actor) {
                                imdb.actors.remove(contributionToRemove);
                            } else if (contributionToRemove instanceof Production) {
                                imdb.productions.remove(contributionToRemove);
                            }
                            contributor.contributions.remove(contributionToRemove);
                            JOptionPane.showMessageDialog(null, "Contribution deleted successfully!");
                            listModel.removeElement(selectedContribution);
                        }
                    } else if (user instanceof Admin) {
                        Admin admin = (Admin) user;
                        Object contributionToRemove = null;
                        for (Object contribution : admin.contributions) {
                            String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                            if (contributionName.equals(selectedContribution)) {
                                contributionToRemove = contribution;
                                break;
                            }
                        }
                        if (contributionToRemove != null) {
                            if (contributionToRemove instanceof Actor) {
                                imdb.actors.remove(contributionToRemove);
                            } else if (contributionToRemove instanceof Production) {
                                imdb.productions.remove(contributionToRemove);
                            }
                            admin.contributions.remove(contributionToRemove);
                            JOptionPane.showMessageDialog(null, "Contribution deleted successfully!");
                            listModel.removeElement(selectedContribution);
                        } else {
                            for (Object contribution : Admin.ContributionsHolder.contributions) {
                                String contributionName = contribution instanceof Actor ? ((Actor) contribution).name : ((Production) contribution).title;
                                if (contributionName.equals(selectedContribution)) {
                                    contributionToRemove = contribution;
                                    break;
                                }
                            }
                            if (contributionToRemove != null) {
                                if (contributionToRemove instanceof Actor) {
                                    imdb.actors.remove(contributionToRemove);
                                } else if (contributionToRemove instanceof Production) {
                                    imdb.productions.remove(contributionToRemove);
                                }
                                Admin.ContributionsHolder.contributions.remove(contributionToRemove);
                                JOptionPane.showMessageDialog(null, "Contribution deleted successfully!");
                                listModel.removeElement(selectedContribution);
                            }
                        }
                    }
                }
            }
        });
        add(deleteButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}