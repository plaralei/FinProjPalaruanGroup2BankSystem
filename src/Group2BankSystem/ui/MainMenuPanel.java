package Group2BankSystem.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {
    private final MainFrame frame;
    private final Color PRIMARY_COLOR = new Color(12, 46, 97);
    private final Color SECONDARY_COLOR = new Color(82, 109, 130);

    public MainMenuPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        initializeUI();
    }

    private void initializeUI() {
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(225, 235, 245));
                g2d.fillOval(-150, -150, 300, 300); // Top-left circle
                g2d.fillRect(getWidth() - 250, getHeight() - 150, 300, 300); // Bottom-right square
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JPanel titlePanel = createTitlePanel();
        JPanel descPanel = createDescriptionPanel();
        JPanel accountTypesPanel = createAccountTypesPanel();
        JPanel footerPanel = createFooterPanel();

        contentPanel.add(titlePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(descPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(accountTypesPanel);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(footerPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Group 2 Bank System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator titleLine = new JSeparator(SwingConstants.HORIZONTAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, SECONDARY_COLOR));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titleLine.setPreferredSize(new Dimension(200, 3));

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(titleLine);
        return panel;
    }

    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        panel.setLayout(new BorderLayout());

        JTextArea description = new JTextArea();
        description.setText("A Banking System project for the course requirement of CS122 Computer Programming 2, emphasizing Graphical User Interface, File Input/Output, Java Collections Framework, and Streams API. This project aims to develop a robust banking system that efficiently manages essential banking transactions across various account types while providing a user-friendly interface for seamless interaction. Key features will include account creation, management, transaction history logging, and comprehensive report generation, all designed to replicate real-world banking operations. By leveraging Java's collection classes and implementing permanent data storage, this project will enhance the learning experience and ensure compliance with the specified requirements.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(60, 60, 60));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setOpaque(false);
        description.setEditable(false);
        description.setMargin(new Insets(10, 10, 10, 10));
        description.setPreferredSize(new Dimension(600, 140));

        panel.add(description, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccountTypesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        panel.add(createAccountCard("Bank Account", "A secure financial account that allows individuals to deposit, withdraw, and manage their funds."));
        panel.add(createAccountCard("Investment", "It enables individuals to buy and sell various financial assets to grow their wealth over time."));
        panel.add(createAccountCard("Credit Card", "It allows users to make purchases and borrow money, which must be repaid with interest."));
        panel.add(createAccountCard("Checking", "It allows for easy access to funds for everyday expenses, including deposits, withdrawals, and bill payments."));

        return panel;
    }

    private JPanel createAccountCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(80, 80, 80));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setMargin(new Insets(8, 0, 0, 0));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(descArea);

        card.addMouseListener(new MouseAdapter() {
            private final Color normalBackground = card.getBackground();
            private final Color highlightBackground = new Color(230, 240, 255);
            private final int normalBorderThickness = 1;
            private final int highlightBorderThickness = 3;

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(12, 46, 97), highlightBorderThickness),
                        BorderFactory.createEmptyBorder(17, 17, 17, 17)
                ));
                card.setBackground(highlightBackground);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 210, 220), normalBorderThickness),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                card.setBackground(normalBackground);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.repaint();
            }
        });

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));

        JLabel creditLabel = new JLabel("Developed by Princess Laralei Palaruan, Jay-Lu Bautista, Keiser Angelo Lucena, " +
                "Em Kenzi Elayda, Kirsten Rapada, Christine Sarquilla, Ellouise Advnicula ");
        creditLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        creditLabel.setForeground(new Color(80, 80, 80));
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footer.add(Box.createHorizontalGlue());
        footer.add(creditLabel);
        footer.add(Box.createHorizontalGlue());

        return footer;
    }
}