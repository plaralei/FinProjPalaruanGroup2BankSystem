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

/**
 * A panel within the banking system UI for searching and displaying account information.
 * <p>
 * Allows users to search by account number, name, or type. Provides auto-suggestions as the user types,
 * and displays results in a table with key account details.
 * </p>
 */
public class SearchAccountPanel extends JPanel {

    /** Reference to the main application frame. */
    private final MainFrame frame;

    /** Field where the user enters the search query. */
    private final JTextField searchField;

    /** Table that displays matching search results. */
    private final JTable resultTable;

    /** Model for managing table data. */
    private final DefaultTableModel tableModel;

    /** Popup that displays dynamic search suggestions. */
    private final JPopupMenu suggestionsPopup;

    /** Maximum number of suggestions to show. */
    private final int MAX_SUGGESTIONS = 7;

    /** Flag to track whether a suggestion was clicked. */
    private boolean isSuggestionSelected = false;

    /**
     * Constructs the search panel and initializes UI components and listeners.
     *
     * @param frame the main application frame containing this panel
     */
    public SearchAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search by Account Name/Number/Type:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

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
        resultTable.setRowHeight(25);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(this::performSearch);
        searchField.addActionListener(this::performSearch);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                delayedRefreshSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                delayedRefreshSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text fields
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

    /**
     * Adds a short delay before refreshing suggestions to improve user experience.
     */
    private void delayedRefreshSuggestions() {
        Timer timer = new Timer(50, event -> {
            if (!isSuggestionSelected) {
                refreshSuggestions();
            }
            isSuggestionSelected = false;
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Triggers suggestion rendering on the UI thread.
     */
    private void refreshSuggestions() {
        SwingUtilities.invokeLater(() -> {
            suggestionsPopup.setVisible(false);
            showSuggestions();
        });
    }

    /**
     * Displays dynamic suggestions based on the current input in the search field.
     */
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
            String displayText = account.getAccountNumber() + " - " +
                    account.getAccountHolderName() + " - " +
                    account.getAccountType();

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

    /**
     * Displays a single account's details in the result table.
     *
     * @param account the bank account to display
     */
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

    /**
     * Fetches a list of matching accounts based on the search query.
     *
     * @param query the search string input by the user
     * @return a list of suggested bank accounts matching the query
     */
    private List<BankAccount> getSuggestions(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase();

        return AccountManager.getAccounts().stream()
                .filter(account ->
                        account.getAccountNumber().contains(query) ||
                                account.getAccountHolderName().toLowerCase().contains(lowerQuery) ||
                                account.getAccountType().toLowerCase().startsWith(lowerQuery))
                .limit(MAX_SUGGESTIONS)
                .collect(Collectors.toList());
    }

    /**
     * Performs the actual search operation and displays results in the table.
     *
     * @param e the action event from the search button or Enter key
     */
    private void performSearch(ActionEvent e) {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search query",
                    "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<BankAccount> results = AccountManager.searchAccounts(query);

        results.forEach(account -> {
            Object[] row = {
                    account.getAccountNumber(),
                    account.getAccountHolderName(),
                    account.getAccountType(),
                    String.format("%.2f", account.getBalance()),
                    account.isActive() ? "Active" : "Closed"
            };
            tableModel.addRow(row);
        });

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No accounts found matching: " + query,
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }

        suggestionsPopup.setVisible(false);
    }
}