package Group2BankSystem.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout layout;
    private final JPanel cards;

    public static final String MENU = "Menu";
    public static final String CREATE = "Create";
    public static final String MANAGE = "Manage";
    public static final String SEARCH = "Search";
    public static final String REPORTS = "Reports";
    public static final String STATEMENTS = "Statements";

    public MainFrame() {
        setTitle("Banking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        cards = new JPanel(layout);

        // Add panels
        cards.add(new MainMenuPanel(this), MENU);
        cards.add(new CreateAccountPanel(this), CREATE);
        cards.add(new ManageAccountPanel(this), MANAGE);
        cards.add(new SearchAccountPanel(this), SEARCH);
        cards.add(new ReportPanel(this), REPORTS);
        cards.add(new StatementPanel(this), STATEMENTS);

        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton(buttonPanel, "Main Menu", MENU);
        addButton(buttonPanel, "Create Account", CREATE);
        addButton(buttonPanel, "Manage Accounts", MANAGE);
        addButton(buttonPanel, "Search Accounts", SEARCH);
        addButton(buttonPanel, "Generate Reports", REPORTS);
        addButton(buttonPanel, "Account Statements", STATEMENTS);
        addButton(buttonPanel, "Exit", () -> System.exit(0));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.WEST);
        add(cards, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.addActionListener(e -> layout.show(cards, cardName));
        panel.add(button);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    public void showCard(String cardName) {
        layout.show(cards, cardName);
    }
}