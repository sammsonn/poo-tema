package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class ModifyFavoritesPage extends JFrame {

    private JList<String> favoritesList;
    private JScrollPane scrollPane;
    private User user;

    public ModifyFavoritesPage(User user) {
        this.user = user;
        setTitle("Favorites");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Favorites:", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        updateFavoritesList();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add Favorite");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFavorite();
                updateFavoritesList();
            }
        });
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove Favorite");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFavorite();
                updateFavoritesList();
            }
        });
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateFavoritesList() {
        List<String> favorites = new ArrayList<>();
        for (Object favorite : user.favorites) {
            if (favorite instanceof Actor) {
                favorites.add(((Actor) favorite).name);
            } else if (favorite instanceof Movie) {
                favorites.add(((Movie) favorite).title);
            } else if (favorite instanceof Series) {
                favorites.add(((Series) favorite).title);
            }
        }

        if (scrollPane != null) {
            remove(scrollPane);
        }

        favoritesList = new JList<>(favorites.toArray(new String[0]));
        scrollPane = new JScrollPane(favoritesList);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addFavorite() {
        String favoriteName = JOptionPane.showInputDialog(this, "Enter the name of the favorite to add:");
        if (favoriteName != null) {
            IMDb imdb = IMDb.getInstance();
            boolean alreadyFavorite = false;
            for (Object favorite : user.favorites) {
                if (favorite instanceof Actor && ((Actor) favorite).name.equalsIgnoreCase(favoriteName)) {
                    alreadyFavorite = true;
                    break;
                } else if (favorite instanceof Movie && ((Movie) favorite).title.equalsIgnoreCase(favoriteName)) {
                    alreadyFavorite = true;
                    break;
                } else if (favorite instanceof Series && ((Series) favorite).title.equalsIgnoreCase(favoriteName)) {
                    alreadyFavorite = true;
                    break;
                }
            }
            if (!alreadyFavorite) {
                for (Actor actor : imdb.actors) {
                    if (actor.name.equalsIgnoreCase(favoriteName)) {
                        user.addFavorite((Comparable) actor);
                        return;
                    }
                }
                for (Production production : imdb.productions) {
                    if (production.title.equalsIgnoreCase(favoriteName)) {
                        user.addFavorite((Comparable) production);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "The name wasn't found in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The given favorite is already in the list.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeFavorite() {
        String selectedFavorite = favoritesList.getSelectedValue();
        if (selectedFavorite != null) {
            for (Object favorite : user.favorites) {
                if (favorite instanceof Actor && ((Actor) favorite).name.equals(selectedFavorite)) {
                    user.removeFavorite((Comparable) favorite);
                    return;
                } else if (favorite instanceof Movie && ((Movie) favorite).title.equals(selectedFavorite)) {
                    user.removeFavorite((Comparable) favorite);
                    return;
                } else if (favorite instanceof Series && ((Series) favorite).title.equals(selectedFavorite)) {
                    user.removeFavorite((Comparable) favorite);
                    return;
                }
            }
        }
    }
}