import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Random;

public class GuessingGame extends JFrame {

    private static final Color BG_TOP = new Color(18, 13, 47);
    private static final Color BG_BOTTOM = new Color(39, 54, 116);
    private static final Color SURFACE_TOP = new Color(28, 31, 68, 240);
    private static final Color SURFACE_BOTTOM = new Color(45, 40, 89, 235);
    private static final Color CARD_TOP = new Color(255, 255, 255, 22);
    private static final Color CARD_BOTTOM = new Color(255, 255, 255, 10);
    private static final Color BORDER_SOFT = new Color(255, 255, 255, 48);
    private static final Color TEXT_PRIMARY = new Color(248, 247, 255);
    private static final Color TEXT_MUTED = new Color(190, 203, 241);
    private static final Color ACCENT_GOLD = new Color(255, 203, 56);
    private static final Color ACCENT_CYAN = new Color(53, 223, 255);
    private static final Color ACCENT_PINK = new Color(255, 97, 178);
    private static final Color ACCENT_GREEN = new Color(35, 197, 127);
    private static final Color ACCENT_ORANGE = new Color(255, 164, 58);
    private static final Color ACCENT_RED = new Color(255, 109, 109);

    private final Random random = new Random();
    private final int maxNumber = 100;

    private int secretNumber;
    private int attempts;
    private int lowBound;
    private int highBound;

    private JLabel attemptsValueLabel;
    private JLabel rangeValueLabel;
    private JLabel feedbackLabel;
    private JLabel helperLabel;
    private RoundedTextField guessField;
    private MeterBar progressBar;
    private GameButton guessButton;
    private GameButton newGameButton;
    private JPanel mainPanel;

    public GuessingGame() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        initGame();
        buildUI();
        updateGameState();
        setVisible(true);
    }

    private void initGame() {
        secretNumber = random.nextInt(maxNumber) + 1;
        attempts = 0;
        lowBound = 1;
        highBound = maxNumber;
    }

    private void buildUI() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint background = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM);
                g2.setPaint(background);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 97, 178, 36));
                g2.fillOval(-110, -30, 330, 330);
                g2.setColor(new Color(53, 223, 255, 26));
                g2.fillOval(getWidth() - 230, 70, 300, 300);
                g2.setColor(new Color(255, 203, 56, 18));
                g2.fillOval(getWidth() / 2 - 120, getHeight() - 260, 260, 260);
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(mainPanel);

        RoundedPanel shell = new RoundedPanel(34, SURFACE_TOP, SURFACE_BOTTOM, BORDER_SOFT);
        shell.setLayout(new BoxLayout(shell, BoxLayout.Y_AXIS));
        shell.setBorder(new EmptyBorder(28, 28, 28, 28));
        shell.setPreferredSize(new Dimension(470, 640));

        JLabel taskLabel = createLabel("TASK 02", 12, Font.BOLD, ACCENT_CYAN);
        taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        shell.add(taskLabel);
        shell.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLabel = createLabel("Guess The Number", 31, Font.BOLD, ACCENT_GOLD);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        shell.add(titleLabel);

        JLabel subtitleLabel = createLabel(
                "Crack the hidden number with the help of smart range hints.",
                14, Font.PLAIN, TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        shell.add(Box.createRigidArea(new Dimension(0, 8)));
        shell.add(subtitleLabel);
        shell.add(Box.createRigidArea(new Dimension(0, 24)));

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        statsPanel.setOpaque(false);
        attemptsValueLabel = createLabel("0", 24, Font.BOLD, TEXT_PRIMARY);
        rangeValueLabel = createLabel("1 - 100", 20, Font.BOLD, TEXT_PRIMARY);
        statsPanel.add(createInfoCard("Attempts", attemptsValueLabel, ACCENT_PINK));
        statsPanel.add(createInfoCard("Live Range", rangeValueLabel, ACCENT_CYAN));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        shell.add(statsPanel);
        shell.add(Box.createRigidArea(new Dimension(0, 18)));

        RoundedPanel meterCard = new RoundedPanel(26, CARD_TOP, CARD_BOTTOM, BORDER_SOFT);
        meterCard.setLayout(new BoxLayout(meterCard, BoxLayout.Y_AXIS));
        meterCard.setBorder(new EmptyBorder(16, 18, 18, 18));
        meterCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel meterTitle = createLabel("Search Progress", 13, Font.BOLD, TEXT_MUTED);
        meterTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        meterCard.add(meterTitle);
        meterCard.add(Box.createRigidArea(new Dimension(0, 8)));

        progressBar = new MeterBar();
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        meterCard.add(progressBar);
        meterCard.add(Box.createRigidArea(new Dimension(0, 8)));

        helperLabel = createLabel("The range will shrink after every valid guess.", 12, Font.PLAIN, TEXT_MUTED);
        helperLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        meterCard.add(helperLabel);
        shell.add(meterCard);
        shell.add(Box.createRigidArea(new Dimension(0, 18)));

        RoundedPanel inputCard = new RoundedPanel(26, CARD_TOP, CARD_BOTTOM, BORDER_SOFT);
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        inputCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel inputTitle = createLabel("Enter Your Guess", 15, Font.BOLD, TEXT_PRIMARY);
        inputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(inputTitle);
        inputCard.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel inputHint = createLabel("Use a whole number within the current live range.", 12, Font.PLAIN, TEXT_MUTED);
        inputHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(inputHint);
        inputCard.add(Box.createRigidArea(new Dimension(0, 14)));

        guessField = new RoundedTextField("Example: 42");
        guessField.setAlignmentX(Component.LEFT_ALIGNMENT);
        guessField.addActionListener(e -> processGuess());
        inputCard.add(guessField);
        shell.add(inputCard);
        shell.add(Box.createRigidArea(new Dimension(0, 18)));

        RoundedPanel feedbackCard = new RoundedPanel(22, new Color(255, 255, 255, 18),
                new Color(255, 255, 255, 10), BORDER_SOFT);
        feedbackCard.setLayout(new BorderLayout());
        feedbackCard.setBorder(new EmptyBorder(16, 18, 16, 18));
        feedbackCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        feedbackLabel = createLabel("Enter your first guess to begin.", 15, Font.BOLD, TEXT_PRIMARY);
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackCard.add(feedbackLabel, BorderLayout.CENTER);
        shell.add(feedbackCard);
        shell.add(Box.createRigidArea(new Dimension(0, 22)));

        JPanel actionRow = new JPanel(new GridLayout(1, 2, 14, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        guessButton = new GameButton("Submit Guess", ACCENT_PINK, new Color(255, 132, 192), TEXT_PRIMARY);
        guessButton.addActionListener(e -> processGuess());
        actionRow.add(guessButton);

        newGameButton = new GameButton("New Game", ACCENT_GREEN, new Color(70, 219, 155), TEXT_PRIMARY);
        newGameButton.addActionListener(e -> resetGame());
        actionRow.add(newGameButton);
        shell.add(actionRow);
        shell.add(Box.createVerticalGlue());

        mainPanel.add(shell, new GridBagConstraints());
    }

    private JPanel createInfoCard(String title, JLabel valueLabel, Color accent) {
        RoundedPanel card = new RoundedPanel(24, CARD_TOP, CARD_BOTTOM, BORDER_SOFT);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent);
        accentLine.setMaximumSize(new Dimension(46, 4));
        accentLine.setPreferredSize(new Dimension(46, 4));
        accentLine.setMinimumSize(new Dimension(46, 4));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = createLabel(title.toUpperCase(), 11, Font.BOLD, TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accentLine);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);
        return card;
    }

    private void processGuess() {
        String input = guessField.getText().trim();
        if (input.isEmpty()) {
            showFeedback("Please enter a number first.", ACCENT_ORANGE);
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            guessField.setText("");
            showFeedback("Only whole numbers are allowed.", ACCENT_RED);
            return;
        }

        if (guess < lowBound || guess > highBound) {
            guessField.setText("");
            showFeedback("Stay inside the live range: " + lowBound + " - " + highBound + ".", ACCENT_ORANGE);
            return;
        }

        attempts++;

        if (guess < secretNumber) {
            lowBound = guess + 1;
            showFeedback("Too low. Move higher.", ACCENT_CYAN);
        } else if (guess > secretNumber) {
            highBound = guess - 1;
            showFeedback("Too high. Come lower.", ACCENT_PINK);
        } else {
            lowBound = secretNumber;
            highBound = secretNumber;
            showFeedback("Correct. You found " + secretNumber + " in " + attempts + " attempts.", ACCENT_GREEN);
            guessButton.setEnabled(false);
            guessField.setEnabled(false);
        }

        guessField.setText("");
        updateGameState();
        if (guessField.isEnabled()) {
            guessField.requestFocusInWindow();
        }
    }

    private void resetGame() {
        initGame();
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        guessField.setText("");
        showFeedback("New round started. Try to crack it quickly.", ACCENT_CYAN);
        updateGameState();
        guessField.requestFocusInWindow();
    }

    private void updateGameState() {
        attemptsValueLabel.setText(String.valueOf(attempts));
        rangeValueLabel.setText(lowBound + " - " + highBound);

        int narrowedSpan = maxNumber - (highBound - lowBound + 1);
        int progressValue = (int) Math.round((narrowedSpan * 100.0) / (maxNumber - 1));
        if (!guessButton.isEnabled()) {
            progressValue = 100;
            helperLabel.setText("Game complete. Start a new round to play again.");
            progressBar.setFillColor(ACCENT_GREEN);
        } else {
            helperLabel.setText("The range will shrink after every valid guess.");
            progressBar.setFillColor(ACCENT_PINK);
        }
        progressBar.setValue(progressValue);
    }

    private void showFeedback(String message, Color color) {
        feedbackLabel.setText("<html><div style='text-align:center;'>" + message + "</div></html>");
        feedbackLabel.setForeground(color);
    }

    private JLabel createLabel(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Trebuchet MS", style, size));
        label.setForeground(color);
        return label;
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color topColor;
        private final Color bottomColor;
        private final Color borderColor;

        RoundedPanel(int arc, Color topColor, Color bottomColor, Color borderColor) {
            this.arc = arc;
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTextField extends JTextField {
        private final String placeholder;

        RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(TEXT_PRIMARY);
            setCaretColor(ACCENT_GOLD);
            setFont(new Font("Trebuchet MS", Font.BOLD, 22));
            setBorder(new EmptyBorder(16, 18, 16, 18));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
            setPreferredSize(new Dimension(380, 58));
            setHorizontalAlignment(CENTER);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(13, 18, 44, 215));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.dispose();
            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D placeholderGraphics = (Graphics2D) g.create();
                placeholderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                placeholderGraphics.setFont(getFont().deriveFont(Font.PLAIN, 16f));
                placeholderGraphics.setColor(new Color(191, 200, 228, 150));
                FontMetrics metrics = placeholderGraphics.getFontMetrics();
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                String text = placeholder;
                int x = (getWidth() - metrics.stringWidth(text)) / 2;
                placeholderGraphics.drawString(text, x, y);
                placeholderGraphics.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isFocusOwner() ? ACCENT_CYAN : BORDER_SOFT);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
        }
    }

    private static class GameButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private final Color textColor;

        GameButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Trebuchet MS", Font.BOLD, 16));
            setPreferredSize(new Dimension(180, 52));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fillColor;
            if (!isEnabled()) {
                fillColor = new Color(124, 126, 151);
            } else if (getModel().isPressed()) {
                fillColor = baseColor.darker();
            } else if (getModel().isRollover()) {
                fillColor = hoverColor;
            } else {
                fillColor = baseColor;
            }

            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

            g2.setFont(getFont());
            g2.setColor(textColor);
            FontMetrics metrics = g2.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
            int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }

    private static class MeterBar extends JProgressBar {
        private Color fillColor = ACCENT_PINK;

        MeterBar() {
            super(0, 100);
            setValue(0);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setPreferredSize(new Dimension(380, 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        }

        void setFillColor(Color fillColor) {
            this.fillColor = fillColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 26));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            int fillWidth = (int) Math.round(getWidth() * (getPercentComplete() == 0 ? 0 : getPercentComplete()));
            if (fillWidth > 0) {
                g2.setPaint(new GradientPaint(0, 0, fillColor, getWidth(), 0, fillColor.brighter()));
                g2.fillRoundRect(0, 0, fillWidth, getHeight(), 16, 16);
            }

            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(GuessingGame::new);
    }
}
