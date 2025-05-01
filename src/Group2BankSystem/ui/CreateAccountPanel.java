package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import Group2BankSystem.exceptions.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;


public class CreateAccountPanel extends JPanel {
    private final MainFrame frame;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private String selectedAccountType;
    private String currentCard;

    private JTextField nameField;
    private JFormattedTextField amountField;
    private JLabel amountLabel;

    public CreateAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        initializeUI();
        currentCard = "TYPE_SELECTION";
    }

    private void initializeUI() {
        JPanel typePanel = createTypeSelectionPanel();
        JPanel formPanel = createFormPanel();
        JPanel successPanel = createSuccessPanel();

        cards.add(typePanel, "TYPE_SELECTION");
        cards.add(formPanel, "FORM");
        cards.add(successPanel, "SUCCESS");

        JPanel shadowPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250),
                        0, height, new Color(220, 220, 220));
                g2d.setPaint(gp);
                g2d.fillRoundRect(10, 10, width - 20, height - 20, 20, 20);

                g2d.setColor(new Color(150, 150, 150, 50));
                for (int i = 1; i <= 5; i++) {
                    g2d.drawRoundRect(10 - i, 10 - i, width - 20 + 2 * i, height - 20 + 2 * i, 20 + i * 2, 20 + i * 2);
                }
                g2d.dispose();
            }
        };
        shadowPanel.setOpaque(false);
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        shadowPanel.add(cards, BorderLayout.CENTER);

        add(shadowPanel, BorderLayout.CENTER);
        add(createBackButton(), BorderLayout.SOUTH);
    }

    private JPanel createTypeSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 25, 25));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String[][] accountTypes = {
                {"Bank Account", " ", "Basic account for daily transactions", "No minimum deposit required"},
                {"Investment Account", " ", "High-interest savings account", "Min deposit: ₱1,000.00"},
                {"Checking Account", " ", "Check-enabled transaction account", "Min deposit: ₱100.00"},
                {"Credit Card Account", " ", "Revolving credit facility", "Min credit limit: ₱5,000.00"}
        };

        for (int i = 0; i < accountTypes.length; i++) { // Ensure this is correct
            panel.add(createAccountTypeCard(accountTypes[i][0], accountTypes[i][1], accountTypes[i][2], accountTypes[i][3]));
        }

        return panel;
    }

    private JPanel createAccountTypeCard(String title, String emoji, String desc, String requirement) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setBackground(Color.WHITE);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        header.add(emojiLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(12, 46, 97));
        header.add(titleLabel);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 230)),
                BorderFactory.createEmptyBorder(15, 20, 20, 20)
        ));

        JTextArea descArea = new JTextArea(desc);
        styleTextArea(descArea, 15, false);
        descArea.setForeground(new Color(80, 80, 80));

        JLabel reqLabel = new JLabel(requirement);
        reqLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        reqLabel.setForeground(new Color(0, 120, 60));
        reqLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        content.add(descArea);
        content.add(reqLabel);

        card.addMouseListener(new MouseAdapter() {
            private final Color normalBackground = card.getBackground();
            private final Color hoverBackground = new Color(215, 230, 250);
            private final int normalBorderThickness = 1;
            private final int hoverBorderThickness = 3;

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverBackground);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(12, 46, 97), hoverBorderThickness),
                        BorderFactory.createEmptyBorder(22, 22, 22, 22)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(normalBackground);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 210, 220), normalBorderThickness),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectedAccountType = title;
                updateFormLabels();
                cardLayout.show(cards, "FORM");
                currentCard = "FORM";
            }
        });

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFormPanel() {
        JPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel shadowWrapper = new JPanel(new GridBagLayout());
        shadowWrapper.setOpaque(false);
        shadowWrapper.add(card);

        JLabel typeLabel = new JLabel();
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        typeLabel.setForeground(new Color(12, 46, 97));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        amountField = new JFormattedTextField(NumberFormat.getNumberInstance());
        amountField.setColumns(10);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        amountLabel = new JLabel();
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        amountLabel.setForeground(new Color(0, 0, 0));

        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(this::createAccount);
        styleButton(createBtn);

        formPanel.add(createLabel("Account Holder Name:"));
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(createBtn);

        card.add(typeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(formPanel);

        this.typeLabel = typeLabel;

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(shadowWrapper, BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel typeLabel;

    private void updateFormLabels() {
        if (typeLabel != null) {
            typeLabel.setText("Creating: " + selectedAccountType);
        }

        String labelText = "";
        String requirement = "";

        switch(selectedAccountType) {
            case "Bank Account":
                labelText = "Initial Deposit:";
                requirement = "Minimum: ₱0.00 (Optional)";
                break;
            case "Investment Account":
                labelText = "Initial Deposit:";
                requirement = "Minimum: ₱1,000.00";
                break;
            case "Checking Account":
                labelText = "Initial Deposit:";
                requirement = "Minimum: ₱100.00";
                break;
            case "Credit Card Account":
                labelText = "Credit Limit:";
                requirement = "Minimum: ₱5,000.00";
                break;
        }
        amountLabel.setText("<html><b>" + labelText + "</b> <font color='#28a745'>" + requirement + "</font></html>");
    }

    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        public RoundedPanel(int radius) {
            super();
            cornerRadius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private JPanel createSuccessPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setBackground(new Color(245, 247, 250));

        JLabel successLabel = new JLabel("✓ Account Created Successfully!");
        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        successLabel.setForeground(new Color(0, 0, 0));
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        detailsArea.setMargin(new Insets(10, 10, 10, 10));

        JButton newAccountBtn = new JButton("Create Another Account");
        newAccountBtn.addActionListener(e -> resetForm());
        styleButton(newAccountBtn);

        panel.add(successLabel, BorderLayout.NORTH);
        panel.add(detailsArea, BorderLayout.CENTER);
        panel.add(newAccountBtn, BorderLayout.SOUTH);

        this.detailsArea = detailsArea;

        return panel;
    }

    private JTextArea detailsArea;

    private void createAccount(ActionEvent e) {
        try {
            String name = validateName();
            double amount = validateAmount();

            BankAccount account = AccountFactory.createAccount(name, amount, selectedAccountType);
            AccountManager.addAccount(account);

            showSuccessMessage(account);

            cardLayout.show(cards, "SUCCESS");
            currentCard = "SUCCESS";

        } catch (InvalidAmountException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("An unexpected error occurred");
            ex.printStackTrace();
        }
    }

    private String validateName() throws IllegalArgumentException {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        return name;
    }

    private double validateAmount() throws InvalidAmountException {
        try {
            double amount = (amountField.getValue() != null)
                    ? ((Number) amountField.getValue()).doubleValue()
                    : 0.0;

            switch(selectedAccountType) {
                case "Bank Account":
                    if (amount < 0) throw new InvalidAmountException("Deposit cannot be negative");
                    break;
                case "Investment Account":
                    if (amount < 1000) throw new InvalidAmountException("Minimum initial deposit: ₱1,000.00");
                    break;
                case "Checking Account":
                    if (amount < 100) throw new InvalidAmountException("Minimum initial deposit: ₱100.00");
                    break;
                case "Credit Card Account":
                    if (amount < 5000) throw new InvalidAmountException("Minimum credit limit: ₱5,000.00");
                    break;
            }

            return amount;
        } catch (NumberFormatException ex) {
            throw new InvalidAmountException("Please enter a valid numerical value");
        }
    }

    private void showSuccessMessage(BankAccount account) {
        if (detailsArea != null) {
            String details = String.format("Account Number: %s\n" +
                            "Account Holder: %s\n" +
                            "Account Type: %s\n" +
                            "%s: %,.2f PHP",
                    account.getAccountNumber(),
                    account.getAccountHolderName(),
                    account.getAccountType(),
                    selectedAccountType.equals("Credit Card Account") ? "Credit Limit" : "Initial Deposit",
                    account.getBalance());
            detailsArea.setText(details);
        }
    }

    public void resetForm() {
        nameField.setText("");
        amountField.setValue(null);
        cardLayout.show(cards, "TYPE_SELECTION");
        currentCard = "TYPE_SELECTION";
    }

    private JButton createBackButton() {
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> handleBackNavigation());
        Color baseColor = new Color(108, 117, 125); // bootstrap's gray-600
        Color hoverColor = new Color(90, 98, 105);
        backBtn.setBackground(baseColor);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(baseColor);
            }
        });

        return backBtn;
    }

    private void handleBackNavigation() {
        switch (currentCard) {
            case "TYPE_SELECTION":
                frame.showCard(MainFrame.MAIN_MENU);
                break;
            case "FORM":
                cardLayout.show(cards, "TYPE_SELECTION");
                currentCard = "TYPE_SELECTION";
                break;
            case "SUCCESS":
                cardLayout.show(cards, "FORM");
                currentCard = "FORM";
                break;
        }
    }
    private void styleTextArea(JTextArea area, int size, boolean bulletPoints) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, size));
        area.setForeground(new Color(60, 60, 60));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setMargin(new Insets(10, bulletPoints ? 30 : 10, 10, 10));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        return label;
    }

    private void showError(String message) {
        String detailedMessage = message;

        switch(selectedAccountType) {
            case "Investment Account":
                detailedMessage += "\n\nInvestment Account Requirements:" +
                        "\n- Minimum Balance: ₱500.00" +
                        "\n- Interest Rate: 5% to 10%";
                break;
            case "Checking Account":
                detailedMessage += "\n\nChecking Account Requirements:" +
                        "\n- Minimum Balance: ₱300.00 to avoid fees" +
                        "\n- Interest Rate: 0% to 1%";
                break;
            case "Credit Card Account":
                detailedMessage += "\n\nCredit Card Requirements:" +
                        "\n- Annual Interest Rate: 15% to 25%" +
                        "\n- Cash Advance Limit: 50% of available credit";
                break;
        }

        JOptionPane.showMessageDialog(this, detailedMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}