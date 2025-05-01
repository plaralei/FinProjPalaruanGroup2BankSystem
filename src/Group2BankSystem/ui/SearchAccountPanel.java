package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class SearchAccountPanel extends JPanel {
    private final MainFrame frame;
    private final JTextField searchField;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;

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

        JButton backButton = new JButton("Back to Main");
        backButton.addActionListener(e -> frame.showCard(MainFrame.MENU));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        searchButton.addActionListener(this::performSearch);
        searchField.addActionListener(this::performSearch);
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
    }
}