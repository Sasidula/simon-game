package com.mycompany.game;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimonGame {
    private JFrame frame;
    private JButton[] buttons;
    private List<String> sequence = new ArrayList<>();
    private int sequenceIndex = 0;
    private boolean playerTurn = false;
    private int score = 0;
    private List<Integer> leaderboard = new ArrayList<>();

    private final String[] colors = {"Red", "Blue", "Green", "Yellow"};
    private final Color[] buttonColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

    public SimonGame() {
        frame = new JFrame("Simon Game");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);  // Center the frame on the screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 5));

        buttons = new JButton[4];
        JButton[][] grid = new JButton[5][5];

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setBackground(Color.BLACK);
                btn.setPreferredSize(new Dimension(100, 100));
                frame.add(btn);
                grid[row][col] = btn;
            }
        }

        int[][] positions = {{1, 1}, {1, 3}, {3, 1}, {3, 3}};
        for (int i = 0; i < 4; i++) {
            int row = positions[i][0];
            int col = positions[i][1];
            buttons[i] = new JButton();
            buttons[i].setBackground(buttonColors[i]);
            buttons[i].setOpaque(true);
            buttons[i].setBorderPainted(false);
            buttons[i].setPreferredSize(new Dimension(150, 150));
            int index = i;
            buttons[i].addActionListener(e -> handlePlayerInput(index));
            grid[row][col].setEnabled(false);
            frame.remove(grid[row][col]);
            frame.add(buttons[i], row * 5 + col);
        }

        frame.setVisible(true);
        showStartingScreen();  // Show the starting screen first
    }

    private void showStartingScreen() {
        // Create a JFrame for the starting screen
        JFrame startingFrame = new JFrame();
        startingFrame.setSize(600, 50);
        startingFrame.setUndecorated(true);  // Remove the title bar
        startingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startingFrame.setLocationRelativeTo(frame);  // Center it on the main frame
        startingFrame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Game Starting!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        startingFrame.add(label, BorderLayout.CENTER);

        startingFrame.setVisible(true);

        // Set a timer to close the starting screen after 3 seconds
        Timer timer = new Timer(3000, e -> startingFrame.dispose());
        timer.setRepeats(false);
        timer.start();

        // Start the game after the "Game Starting" screen disappears
        timer.addActionListener(evt -> startNewGame());
    }

    private void startNewGame() {
        sequence.clear();
        score = 0;
        addToSequence();
        playSequence();
    }

    private void addToSequence() {
        Random random = new Random();
        int nextColor = random.nextInt(4);
        sequence.add(colors[nextColor]);
    }

    private void playSequence() {
        playerTurn = false;
        setButtonsEnabled(false); // Disable buttons during sequence playback
        sequenceIndex = 0;

        System.out.println("Required Sequence: " + sequence);

        Timer timer = new Timer(1000, new ActionListener() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i < sequence.size()) {
                    flashButton(sequence.get(i));
                    i++;
                } else {
                    ((Timer) e.getSource()).stop();
                    setButtonsEnabled(true); // Re-enable buttons after sequence
                    playerTurn = true;
                }
            }
        });
        timer.start();
    }

    private void flashButton(String color) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(color)) {  // Match color name directly
                Color original = buttonColors[i];
                buttons[i].setBackground(Color.WHITE);

                int index = i;
                Timer timer = new Timer(500, e -> buttons[index].setBackground(original));
                timer.setRepeats(false);
                timer.start();
                break;
            }
        }
    }

    private void handlePlayerInput(int index) {
        if (!playerTurn) return;

        String selectedColor = colors[index];
        System.out.println("Player Entered: " + selectedColor);

        if (selectedColor.equals(sequence.get(sequenceIndex))) {
            sequenceIndex++;

            if (sequenceIndex == sequence.size()) {
                score++;
                addToSequence();
                playSequence();
            }
        } else {
            endGame();
        }
    }

    private void endGame() {
        leaderboard.add(score);
        leaderboard.sort(Collections.reverseOrder());
        if (leaderboard.size() > 10) {
            leaderboard = leaderboard.subList(0, 10);
        }
        showGameOverScreen();
    }

    private void showGameOverScreen() {
        // Create a JFrame for the game over screen
        JFrame gameOverFrame = new JFrame();
        gameOverFrame.setSize(500, 400);  // Adjusted size to fit the text field, buttons, and labels
        gameOverFrame.setUndecorated(true);  // Remove the title bar
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setLocationRelativeTo(frame);  // Center it on the main frame
        gameOverFrame.setLayout(new BorderLayout());

        // Main panel for centering content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverFrame.add(mainPanel, BorderLayout.CENTER);

        // Create label to show "Game Over" with big font and padding
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 40));  // Big and bold font for "Game Over"
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(gameOverLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));  // Add space between elements

        // Create label for the score with bigger font
        JLabel scoreLabel = new JLabel("<html><div style='text-align: center;'>Your Score:<br><span style='font-size: 28px; font-weight: bold;'>"
                + score + "</span></div></html>", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));  // Larger font for the score
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scoreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));  // Add space between elements

        // Create label and text field for player name input
        JLabel nameLabel = new JLabel("Enter Player Name:", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 24));  // Default font for label
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JTextField nameField = new JTextField("Player", 15);  // Default name is "Player"
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));  // Larger font for the input field
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(nameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));  // Add space between elements

        // Create a restart button with style
        JPanel restartButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Center the button
        JButton restartButton = new JButton("Restart");
        restartButtonPanel.setBackground(Color.BLACK);
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));  // Big font for button
        restartButton.setBackground(Color.GREEN);  // Green background for visibility
        restartButton.setForeground(Color.BLACK);  // Black text for contrast
        restartButton.setPreferredSize(new Dimension(150, 50));  // Big button size
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setOpaque(true);
        restartButton.addActionListener(e -> {
            String playerName = nameField.getText().trim().isEmpty() ? "Player" : nameField.getText().trim();
            saveScore(playerName, score);  // Save the score and player name to a text file
            gameOverFrame.dispose();
            showStartingScreen();  // Show the starting screen again for the next game
        });
        restartButtonPanel.add(restartButton);  // Add button to the panel
        mainPanel.add(restartButtonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));  // Add space between elements

        // Create a back button for navigating to the leaderboard
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Center the button
        backButtonPanel.setBackground(Color.BLACK);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));  // Big font for button
        backButton.setBackground(Color.RED);  // Red background for visibility
        backButton.setForeground(Color.WHITE);  // White text for contrast
        backButton.setPreferredSize(new Dimension(200, 50));  // Bigger button size
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.addActionListener(e -> {
            gameOverFrame.dispose();  // Close the game over screen
            showLeaderboard();  // Show the leaderboard screen
        });
        backButtonPanel.add(backButton);  // Add button to the panel
        mainPanel.add(backButtonPanel);  // Add the panel with the button
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));  // Add space between elements

        gameOverFrame.setVisible(true);
    }


    private void showLeaderboard() {
        // Dispose of the game frame
        if (frame != null) {
            frame.dispose(); // Closes the main game window
        }

        // Open the leaderboard frame
        new LeaderboardFrame();  // Assuming you have a class that handles the leaderboard
    }


    private void saveScore(String playerName, int score) {
        // Create a file to store the scores (if it doesn't exist)
        File file = new File("scores.txt");

        try (FileWriter writer = new FileWriter(file, true)) {
            // Append the player's name and score to the file
            writer.write(playerName + ": " + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (JButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(SimonGame::new);
    }*/
}
