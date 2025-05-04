package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    public static String MENU;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public static final String MAIN_MENU = "MainMenu";
    public static final String CREATE_ACCOUNT = "CreateAccount";
    public static final String MANAGE_ACCOUNTS = "ManageAccounts";
    public static final String SEARCH_ACCOUNTS = "SearchAccounts";
    public static final String REPORTS = "Reports";
    public static final String ACCOUNT_STATEMENTS = "AccountStatements";

    public MainFrame() {
        configureFrame();
        initializeUI();
    }

    private void configureFrame() {
        setTitle("Group 2 Bank System");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250)); // Light gray background
    }

    private void initializeUI() {
        JPanel navPanel = createNavigationPanel();

        cardPanel.add(new MainMenuPanel(this), MAIN_MENU);
        cardPanel.add(new CreateAccountPanel(this), CREATE_ACCOUNT);
        cardPanel.add(new ManageAccountPanel(this), MANAGE_ACCOUNTS);
        cardPanel.add(new SearchAccountPanel(this), SEARCH_ACCOUNTS);
        cardPanel.add(new GenerateReportPanel(this), REPORTS);
        cardPanel.add(new AccountStatementPanel(), ACCOUNT_STATEMENTS);

        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(12, 46, 97));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));

        String[] buttons = {
                "Main Menu", "Create Account", "Manage Accounts",
                "Search Accounts", "Generate Reports", "Account Statements"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            styleNavigationButton(btn);
            btn.addActionListener(e -> showCard(getCardName(text)));
            navPanel.add(btn);
            navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return navPanel;
    }

    private void refreshBankData() {
        JDialog loadingDialog = new JDialog(this, "Refreshing Data", true);
        JLabel loadingLabel = new JLabel("Refreshing bank data...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadingDialog.add(loadingLabel);
        loadingDialog.setSize(250, 100);
        loadingDialog.setLocationRelativeTo(this);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {

                AccountManager.reloadAccounts();
                TransactionManager.reloadTransactions();
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Bank data refreshed successfully!",
                        "Refresh Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private void styleNavigationButton(JButton button) {
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(24, 90, 189));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 134, 222), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 134, 222));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(24, 90, 189));
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private String getCardName(String buttonText) {
        return switch (buttonText) {
            case "Main Menu" -> MAIN_MENU;
            case "Create Account" -> CREATE_ACCOUNT;
            case "Manage Accounts" -> MANAGE_ACCOUNTS;
            case "Search Accounts" -> SEARCH_ACCOUNTS;
            case "Generate Reports" -> REPORTS;
            case "Account Statements" -> ACCOUNT_STATEMENTS;
            default -> MAIN_MENU;
        };
    }

    public void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
    }
}
