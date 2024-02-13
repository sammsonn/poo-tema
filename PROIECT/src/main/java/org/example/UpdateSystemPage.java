package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateSystemPage extends JFrame {
    private JComboBox<String> typeComboBox;
    private JButton updateButton;
    private User user;

    public UpdateSystemPage(User user) {
        this.user = user;
        setTitle("Update System");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] types = {"Actor", "Movie", "Series"};
        typeComboBox = new JComboBox<>(types);
        add(typeComboBox, BorderLayout.NORTH);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedType = (String) typeComboBox.getSelectedItem();
                    if (selectedType.equals("Actor")) {
                        new UpdateActorPage(user);
                    } else if (selectedType.equals("Movie")) {
                        new UpdateMoviePage(user);
                    } else if (selectedType.equals("Series")) {
                        new UpdateSeriesPage(user);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(updateButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class UpdateActorPage extends JFrame {
    private JComboBox<String> actorComboBox;
    private JTextField nameField;
    private JTextField bioField;
    private JButton updateButton;
    private User user;

    public UpdateActorPage(User user) {
        this.user = user;
        setTitle("Update Actor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));


        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            for (Object contribution : contributor.contributions) {
                if (contribution instanceof Actor) {
                    model.addElement(((Actor) contribution).name);
                }
            }
        }
        actorComboBox = new JComboBox<>(model);
        add(new JLabel("Select Actor:"));
        add(actorComboBox);

        nameField = new JTextField();
        add(new JLabel("New Name:"));
        add(nameField);

        bioField = new JTextField();
        add(new JLabel("New Bio:"));
        add(bioField);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedActorName = (String) actorComboBox.getSelectedItem();
                if (selectedActorName == null) {
                    JOptionPane.showMessageDialog(null, "Please select an actor to update.");
                }

                String newName = nameField.getText();
                String newBio = bioField.getText();

                IMDb imdb = IMDb.getInstance();
                Actor actorToUpdate = null;
                for (Actor actor : imdb.actors) {
                    if (actor.name.equals(selectedActorName)) {
                        actorToUpdate = actor;
                        break;
                    }
                }

                if (actorToUpdate != null) {
                    if (newName.equals("") && newBio.equals("")) {
                        JOptionPane.showMessageDialog(null, "No changes were made.");
                        dispose();
                        return;
                    }
                    if (!newName.equals("")) {
                        actorToUpdate.name = newName;
                    }
                    if (!newBio.equals("")) {
                        actorToUpdate.bio = newBio;
                    }
                    JOptionPane.showMessageDialog(null, "Actor updated successfully.");
                }
                dispose();
            }
        });
        add(updateButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class UpdateMoviePage extends JFrame {
    private JComboBox<String> movieComboBox;
    private JTextField titleField;
    private JTextField genreField;
    private JTextField descriptionField;
    private JTextField durationField;
    private JTextField yearField;
    private JTextField directorField;
    private JTextField actorField;
    private JButton updateButton;
    private User user;

    public UpdateMoviePage(User user) {
        this.user = user;
        setTitle("Update Movie");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(9, 2));


        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            for (Object contribution : contributor.contributions) {
                if (contribution instanceof Movie) {
                    model.addElement(((Movie) contribution).title);
                }
            }
        }
        movieComboBox = new JComboBox<>(model);
        add(new JLabel("Select Movie:"));
        add(movieComboBox);

        titleField = new JTextField();
        add(new JLabel("Title:"));
        add(titleField);

        directorField = new JTextField();
        add(new JLabel("Director:"));
        add(directorField);

        actorField = new JTextField();
        add(new JLabel("Actor:"));
        add(actorField);

        genreField = new JTextField();
        add(new JLabel("Genre:"));
        add(genreField);

        descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        durationField = new JTextField();
        add(new JLabel("Duration:"));
        add(durationField);

        yearField = new JTextField();
        add(new JLabel("Year:"));
        add(yearField);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedMovieTitle = (String) movieComboBox.getSelectedItem();
                    String newTitle = titleField.getText();
                    String newDirector = directorField.getText();
                    String newActor = actorField.getText();
                    String newDescription = descriptionField.getText();
                    int newDuration = 0;
                    if (!durationField.getText().equals("")) {
                        newDuration = Integer.parseInt(durationField.getText());
                    }
                    int newYear = 0;
                    if (!yearField.getText().equals("")) {
                        newYear = Integer.parseInt(yearField.getText());
                    }
                    String newGenre = genreField.getText();

                    IMDb imdb = IMDb.getInstance();
                    Movie movieToUpdate = null;
                    for (Production production : imdb.productions) {
                        if (production.title.equals(selectedMovieTitle)) {
                            movieToUpdate = (Movie) production;
                            break;
                        }
                    }

                    if (movieToUpdate != null) {
                        if (newTitle.equals("") && newGenre.equals("") && newDirector.equals("") && newActor.equals("") && newDescription.equals("") && newDuration == 0 && newYear == 0) {
                            JOptionPane.showMessageDialog(null, "No changes were made.");
                            dispose();
                            return;
                        }
                        if (!newTitle.equals("")) {
                            movieToUpdate.title = newTitle;
                        }
                        movieToUpdate.title = newTitle;
                        if (!newDirector.equals("")) {
                            movieToUpdate.directors.add(newDirector);
                        }
                        if (!newActor.equals("")) {
                            movieToUpdate.actors.add(newActor);
                        }
                        if (!newDescription.equals("")) {
                            movieToUpdate.description = newDescription;
                        }
                        if (!newGenre.equals("")) {
                            movieToUpdate.genres.add(Genre.valueOf(newGenre));
                        }
                        if (newDuration != 0) {
                            movieToUpdate.duration = newDuration;
                        }
                        if (newYear != 0) {
                            movieToUpdate.year = newYear;
                        }
                        JOptionPane.showMessageDialog(null, "Movie updated successfully.");
                    }
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UpdateMoviePage.this, "Invalid number input for duration or year.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(UpdateMoviePage.this, "Invalid genre.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        add(updateButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class UpdateSeriesPage extends JFrame {
    private JComboBox<String> seriesComboBox;
    private JTextField titleField;
    private JTextField genreField;
    private JTextField descriptionField;
    private JTextField directorField;
    private JTextField actorField;
    private JTextField yearField;
    private JTextField noOfSeasonsField;
    private JButton updateButton;
    private User user;

    public UpdateSeriesPage(User user) {
        this.user = user;
        setTitle("Update Series");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(9, 2));


        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (user instanceof Contributor) {
            Contributor contributor = (Contributor) user;
            for (Object contribution : contributor.contributions) {
                if (contribution instanceof Series) {
                    model.addElement(((Series) contribution).title);
                }
            }
        }
        seriesComboBox = new JComboBox<>(model);
        add(new JLabel("Select Series:"));
        add(seriesComboBox);

        titleField = new JTextField();
        add(new JLabel("Title:"));
        add(titleField);

        directorField = new JTextField();
        add(new JLabel("Director:"));
        add(directorField);

        actorField = new JTextField();
        add(new JLabel("Actor:"));
        add(actorField);

        descriptionField = new JTextField();
        add(new JLabel("Description:"));
        add(descriptionField);

        yearField = new JTextField();
        add(new JLabel("Year:"));
        add(yearField);

        genreField = new JTextField();
        add(new JLabel("Genre:"));
        add(genreField);

        noOfSeasonsField = new JTextField();
        add(new JLabel("Number of Seasons:"));
        add(noOfSeasonsField);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedSeriesTitle = (String) seriesComboBox.getSelectedItem();
                    String newTitle = titleField.getText();
                    String newGenre = genreField.getText();
                    String newDirector = directorField.getText();
                    String newActor = actorField.getText();
                    String newDescription = descriptionField.getText();
                    int newYear = 0;
                    if (!yearField.getText().equals("")) {
                        newYear = Integer.parseInt(yearField.getText());
                    }
                    int newNoOfSeasons = 0;
                    if (!noOfSeasonsField.getText().equals("")) {
                        newNoOfSeasons = Integer.parseInt(noOfSeasonsField.getText());
                    }

                    IMDb imdb = IMDb.getInstance();
                    Series seriesToUpdate = null;
                    for (Production production : imdb.productions) {
                        if (production.title.equals(selectedSeriesTitle)) {
                            seriesToUpdate = (Series) production;
                            break;
                        }
                    }

                    if (seriesToUpdate != null) {
                        if (newTitle.equals("") && newGenre.equals("") && newDirector.equals("") && newActor.equals("") && newDescription.equals("") && newYear == 0 && newNoOfSeasons == 0) {
                            JOptionPane.showMessageDialog(null, "No changes were made.");
                            dispose();
                            return;
                        }
                        if (!newTitle.equals("")) {
                            seriesToUpdate.title = newTitle;
                        }
                        if (!newGenre.equals("")) {
                            seriesToUpdate.genres.add(Genre.valueOf(newGenre));
                        }
                        if (!newDirector.equals("")) {
                            seriesToUpdate.directors.add(newDirector);
                        }
                        if (!newActor.equals("")) {
                            seriesToUpdate.actors.add(newActor);
                        }
                        if (!newDescription.equals("")) {
                            seriesToUpdate.description = newDescription;
                        }
                        if (newYear != 0) {
                            seriesToUpdate.year = newYear;
                        }
                        if (newNoOfSeasons != 0) {
                            seriesToUpdate.noOfSeasons = newNoOfSeasons;
                        }
                        JOptionPane.showMessageDialog(null, "Series updated successfully.");
                    }
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UpdateSeriesPage.this, "Invalid number input for duration or year.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(UpdateSeriesPage.this, "Invalid genre.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        add(updateButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}