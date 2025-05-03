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
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JPopupMenu suggestionsPopup;
    private final int MAX_SUGGESTIONS = 7;
    private boolean isSuggestionSelected = false;

    public SearchAccountPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search by Name or Account Number:"));
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

        // Customize the table styling
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


        return AccountManager.getAccounts().stream()
                .filter(account ->
                        account.getAccountNumber().contains(query) ||
                                account.getAccountHolderName().toLowerCase().contains(lowerQuery))
                .limit(MAX_SUGGESTIONS)
                .collect(Collectors.toList());
    }

    private void performSearch(ActionEvent e) {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search query",
                    "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AccountManager.searchAccounts(query).forEach(account -> {
            Object[] row = {
                    account.getAccountNumber(),
                    account.getAccountHolderName(),
                    account.getAccountType(),
                    String.format("%.2f", account.getBalance()),
                    account.isActive() ? "Active" : "Closed"
            };
            tableModel.addRow(row);
        });

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No accounts found matching: " + query,
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }

        suggestionsPopup.setVisible(false);
    }
}