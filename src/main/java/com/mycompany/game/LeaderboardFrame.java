package com.mycompany.game;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class LeaderboardFrame {

    private JFrame leaderboardFrame;
    private JLabel leaderboardLabel;
    private JButton startButton;

    public LeaderboardFrame() {
        leaderboardFrame = new JFrame("Simon Game");
        leaderboardFrame.setSize(600, 600);  // Adjusted size to make it bigger
        leaderboardFrame.setLocation(500, 200); // Set a fixed position on the screen
        leaderboardFrame.setLocationRelativeTo(null);  // Center the frame on the screen
        leaderboardFrame.setLayout(new BorderLayout());

        // Set the background color to black
        leaderboardFrame.getContentPane().setBackground(Color.BLACK);

        // Title label with top and bottom padding
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Simon Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));  // Big and bold font
        titleLabel.setForeground(Color.WHITE);  // White text on black background
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(600, 100));  // Add padding by adjusting height

        leaderboardFrame.add(titlePanel, BorderLayout.NORTH);

        // Panel to display the leaderboard
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BorderLayout());
        leaderboardPanel.setBackground(Color.BLACK);

        // Display leaderboard in a JLabel for better formatting
        leaderboardLabel = new JLabel();
        leaderboardLabel.setFont(new Font("Arial", Font.PLAIN, 18));  // Larger font size
        leaderboardLabel.setForeground(Color.WHITE);
        leaderboardLabel.setBackground(Color.BLACK);
        leaderboardLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center align the text
        leaderboardLabel.setVerticalAlignment(SwingConstants.TOP);  // Align the text from top to avoid it being cut off
        leaderboardLabel.setOpaque(true);
        leaderboardPanel.add(new JScrollPane(leaderboardLabel), BorderLayout.CENTER);

        leaderboardFrame.add(leaderboardPanel, BorderLayout.CENTER);

        // Start button with larger size and font
        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));  // Big, bold font
        startButton.setBackground(Color.GREEN);  // Green button for visibility
        startButton.setForeground(Color.BLACK);  // Black text for contrast
        startButton.setPreferredSize(new Dimension(200, 50));  // Bigger button
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);
        startButton.addActionListener(e -> {
            leaderboardFrame.dispose();  // Close leaderboard frame
            new SimonGame();  // Start the game
        });
        leaderboardFrame.add(startButton, BorderLayout.SOUTH);

        // Load the leaderboard from the file
        loadLeaderboard();

        leaderboardFrame.setVisible(true);
    }

    private void loadLeaderboard() {
        // Load player scores from the file
        List<Player> players = new ArrayList<>();
        File file = new File("scores.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    players.add(new Player(playerName, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort players by score in descending order
        players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        // Display the top 10 players in the label
        StringBuilder leaderboardText = new StringBuilder("<html><div style='text-align:center;'><br><b>Leaderboard<b><br><br>");
        int count = Math.min(10, players.size());
        for (int i = 0; i < count; i++) {
            Player player = players.get(i);
            leaderboardText.append((i + 1) + ". " + player.getName() + " - " + player.getScore() + "<br>");
        }
        leaderboardText.append("</div></html>");

        leaderboardLabel.setText(leaderboardText.toString());
    }

    // Player class to store name and score
    private static class Player {
        private String name;
        private int score;

        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(LeaderboardFrame::new);
    }*/
}
