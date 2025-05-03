package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class SearchAccountPanel extends JPanel {
    private final MainFrame frame;
    private final JTextField searchField;
    private final JComboBox<String> accountTypeComboBox;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JPopupMenu suggestionsPopup;
    private final int MAX_SUGGESTIONS = 7;
    private boolean isSuggestionSelected = false;

    public SearchAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create search panel with both text search and account type filter
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");

        // Add account type filter dropdown
        String[] accountTypes = {"All Types", "Bank Account", "Checking Account", "Investment Account", "Credit Card Account"};
        accountTypeComboBox = new JComboBox<>(accountTypes);
        accountTypeComboBox.setPreferredSize(new Dimension(150, 28));
        accountTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Layout components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        searchPanel.add(new JLabel("Search by Name or Account Number:"), gbc);

        gbc.gridy = 1;
        searchPanel.add(searchField, gbc);

        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        searchPanel.add(new JLabel("Filter by Account Type:"), gbc);

        gbc.gridy = 1;
        searchPanel.add(accountTypeComboBox, gbc);

        gbc.gridx = 4;
        searchPanel.add(searchButton, gbc);

        suggestionsPopup = new JPopupMenu();
        suggestionsPopup.setFocusable(false);
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        String[] columns = {"Account Number", "Account Holder", "Type", "Balance", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        // Customize the table styling
        resultTable.setRowHeight(25);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add button to view account details when selected
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewDetailsBtn.addActionListener(e -> viewAccountDetails());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewDetailsBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(this::performSearch);
        searchField.addActionListener(this::performSearch);

        // Add action listener to account type combo box to filter immediately
        accountTypeComboBox.addActionListener(e -> {
            if (!searchField.getText().trim().isEmpty()) {
                performSearch(new ActionEvent(accountTypeComboBox, ActionEvent.ACTION_PERFORMED, null));
            } else if (!accountTypeComboBox.getSelectedItem().equals("All Types")) {
                searchByTypeOnly();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Use timer to prevent UI issues when typing while hovering
                Timer timer = new Timer(50, event -> {
                    if (!isSuggestionSelected) {
                        refreshSuggestions();
                    }
                    isSuggestionSelected = false;
                });
                timer.setRepeats(false);
                timer.start();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Timer timer = new Timer(50, event -> {
                    if (!isSuggestionSelected) {
                        refreshSuggestions();
                    }
                    isSuggestionSelected = false;
                });
                timer.setRepeats(false);
                timer.start();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text fields
            }
        });

        suggestionsPopup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!suggestionsPopup.isVisible() ||
                        e.getOppositeComponent() == null ||
                        !SwingUtilities.isDescendingFrom(e.getOppositeComponent(), suggestionsPopup)) {
                    Timer timer = new Timer(200, event -> {
                        if (suggestionsPopup.isVisible()) {
                            suggestionsPopup.setVisible(false);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
    }

    private void viewAccountDetails() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an account to view details",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String accountNumber = (String) tableModel.getValueAt(selectedRow, 0);
        AccountManager.getAccountByNumber(accountNumber).ifPresent(account -> {
            JTextArea detailsArea = new JTextArea();
            detailsArea.setEditable(false);
            detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            detailsArea.setMargin(new Insets(10, 10, 10, 10));

            StringBuilder details = new StringBuilder();
            details.append("Account Number: ").append(account.getAccountNumber()).append("\n");
            details.append("Account Holder: ").append(account.getAccountHolderName()).append("\n");
            details.append("Account Type: ").append(account.getAccountType()).append("\n");
            details.append("Balance: ₱").append(String.format("%.2f", account.getBalance())).append("\n");
            details.append("Status: ").append(account.isActive() ? "Active" : "Closed").append("\n");
            details.append("Date Created: ").append(account.getDateCreated()).append("\n");
            details.append("Last Updated: ").append(account.getDateLastUpdated()).append("\n");

            // Add type-specific details
            if (account instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount) account;
                details.append("Overdraft Limit: ₱").append(String.format("%.2f", checkingAccount.getOverdraftLimit())).append("\n");
                details.append("Available Balance: ₱").append(String.format("%.2f", checkingAccount.getAvailableBalance())).append("\n");
            } else if (account instanceof InvestmentAccount) {
                InvestmentAccount investmentAccount = (InvestmentAccount) account;
                details.append("Interest Rate: ").append(String.format("%.2f%%", investmentAccount.getInterestRate() * 100)).append("\n");
                details.append("Total Interest Earned: ₱").append(String.format("%.2f", investmentAccount.getTotalInterestEarned())).append("\n");
            }

            detailsArea.setText(details.toString());

            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Account Details", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void searchByTypeOnly() {
        tableModel.setRowCount(0);
        String selectedType = (String) accountTypeComboBox.getSelectedItem();

        List<BankAccount> filteredAccounts = AccountManager.getAccounts().stream()
                .filter(account -> selectedType.equals("All Types") || account.getAccountType().equals(selectedType))
                .collect(Collectors.toList());

        displayAccounts(filteredAccounts);
    }

    private void refreshSuggestions() {
        SwingUtilities.invokeLater(() -> {
            suggestionsPopup.setVisible(false);
            showSuggestions();
        });
    }

    private void showSuggestions() {
        String query = searchField.getText().trim();
        suggestionsPopup.removeAll();

        if (query.isEmpty()) {
            suggestionsPopup.setVisible(false);
            return;
        }

        List<BankAccount> suggestions = getSuggestions(query);

        if (suggestions.isEmpty()) {
            suggestionsPopup.setVisible(false);
            return;
        }

        for (BankAccount account : suggestions) {
            String displayText = account.getAccountNumber() + " - " + account.getAccountHolderName();

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel itemLabel = new JLabel(displayText);
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (account.getAccountNumber().contains(query) ||
                    account.getAccountHolderName().toLowerCase().contains(query.toLowerCase())) {
                itemLabel.setForeground(new Color(0, 100, 200));
            }

            itemPanel.add(itemLabel, BorderLayout.CENTER);

            final BankAccount selectedAccount = account;
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    itemPanel.setBackground(new Color(230, 240, 250));
                    itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    itemPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isSuggestionSelected = true;
                    searchField.setText(selectedAccount.getAccountNumber());
                    suggestionsPopup.setVisible(false);
                    displayAccountInTable(selectedAccount);
                }
            });

            suggestionsPopup.add(itemPanel);
        }

        if (suggestionsPopup.getComponentCount() > 0) {
            suggestionsPopup.show(searchField, 0, searchField.getHeight());
            suggestionsPopup.setPopupSize(searchField.getWidth(),
                    Math.min(suggestions.size(), MAX_SUGGESTIONS) * 35);
        } else {
            suggestionsPopup.setVisible(false);
        }
    }

    private void displayAccountInTable(BankAccount account) {
        tableModel.setRowCount(0);

        Object[] row = {
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getAccountType(),
                String.format("%.2f", account.getBalance()),
                account.isActive() ? "Active" : "Closed"
        };
        tableModel.addRow(row);
    }

    private List<BankAccount> getSuggestions(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase();
        String selectedType = (String) accountTypeComboBox.getSelectedItem();

        return AccountManager.getAccounts().stream()
                .filter(account ->
                        (selectedType.equals("All Types") || account.getAccountType().equals(selectedType)) &&
                                (account.getAccountNumber().contains(query) ||
                                        account.getAccountHolderName().toLowerCase().contains(lowerQuery)))
                .limit(MAX_SUGGESTIONS)
                .collect(Collectors.toList());
    }

    private void performSearch(ActionEvent e) {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);
        String selectedType = (String) accountTypeComboBox.getSelectedItem();

        if (query.isEmpty() && selectedType.equals("All Types")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search query or select an account type",
                    "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If query is empty but we have a selected type, just filter by type
        if (query.isEmpty()) {
            searchByTypeOnly();
            return;
        }

        List<BankAccount> filteredAccounts = AccountManager.searchAccounts(query).stream()
                .filter(account -> selectedType.equals("All Types") || account.getAccountType().equals(selectedType))
                .collect(Collectors.toList());

        displayAccounts(filteredAccounts);
        suggestionsPopup.setVisible(false);
    }

    private void displayAccounts(List<BankAccount> accounts) {
        if (accounts.isEmpty()) {
            String message = "No accounts found";
            if (!accountTypeComboBox.getSelectedItem().equals("All Types")) {
                message += " of type: " + accountTypeComboBox.getSelectedItem();
            }
            if (!searchField.getText().trim().isEmpty()) {
                message += " matching: " + searchField.getText().trim();
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (BankAccount account : accounts) {
            Object[] row = {
                    account.getAccountNumber(),
                    account.getAccountHolderName(),
                    account.getAccountType(),
                    String.format("%.2f", account.getBalance()),
                    account.isActive() ? "Active" : "Closed"
            };
            tableModel.addRow(row);
        }
    }
}