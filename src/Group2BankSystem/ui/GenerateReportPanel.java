package Group2BankSystem.ui;

import Group2BankSystem.model.Transaction;
import Group2BankSystem.model.TransactionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GenerateReportPanel extends JPanel {
    private final JPanel reportOptionsPanel;
    private final DefaultTableModel tableModel;
    private final JTable reportTable;
    private String currentTransactionType = "All";

    public GenerateReportPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

//        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
//        String[] transactionTypes = {"All", "Deposit", "Withdrawal", "Transfer", "Fee", "Interest"};
//        for (String type : transactionTypes) {
//            JButton btn = new JButton(type);
//            btn.setFocusPainted(false);
//            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
//            btn.setBackground(type.equals(currentTransactionType) ? new Color(12, 46, 97) : UIManager.getColor("Button.background"));
//            btn.setForeground(type.equals(currentTransactionType) ? Color.WHITE : Color.BLACK);
//            btn.setPreferredSize(new Dimension(110, 32));
//            btn.addActionListener(e -> {
//                currentTransactionType = type;
//                updateButtonStyles();
//                loadReportOptions();
//                loadTransactions();
//            });
//            buttonPanel.add(btn);
//        }
//        add(buttonPanel, BorderLayout.NORTH);

        reportOptionsPanel = new JPanel();
        reportOptionsPanel.setLayout(new BoxLayout(reportOptionsPanel, BoxLayout.Y_AXIS));
        add(reportOptionsPanel, BorderLayout.CENTER);

        String[] columns = {"Date", "Type", "Account Number", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Only Amount (index 3) editable
                return col == 3;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return col == 3 ? Double.class : String.class;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setFillsViewportHeight(true);
        reportTable.setRowHeight(24);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                updateTransactionAmount(row);
            }
        });

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setPreferredSize(new Dimension(900, 300));
        add(scrollPane, BorderLayout.SOUTH);

        loadReportOptions();
        loadTransactions();
        reportTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = reportTable.columnAtPoint(e.getPoint());
                String columnName = reportTable.getColumnName(column);

                if ("Date".equals(columnName)) {
                    JPopupMenu dateMenu = new JPopupMenu();

                    JMenuItem sortMonth = new JMenuItem("Sort by Month");
                    sortMonth.addActionListener(ev -> sortTransactionsByMonth());

                    JMenuItem sortDay = new JMenuItem("Sort by Day");
                    sortDay.addActionListener(ev -> sortTransactionsByDay());

                    JMenuItem todayItem = new JMenuItem("Filter: Today");
                    todayItem.addActionListener(ev -> filterTransactionsToday());

                    JMenuItem previousItem = new JMenuItem("Filter: Previous");
                    previousItem.addActionListener(ev -> filterTransactionsPrevious());

                    dateMenu.add(sortMonth);
                    dateMenu.add(sortDay);
                    dateMenu.add(todayItem);
                    dateMenu.add(previousItem);

                    dateMenu.show(reportTable.getTableHeader(), e.getX(), e.getY());
                }

                if ("Type".equals(columnName)) {
                    JPopupMenu typeMenu = new JPopupMenu();
                    String[] types = {"ACCOUNT_CLOSED", "DEPOSIT", "WITHDRAWAL", "TRANSFER", "INTEREST", "FEES"};
                    for (String type : types) {
                        JMenuItem item = new JMenuItem(type);
                        item.addActionListener(ev -> filterTransactionsByType(type));
                        typeMenu.add(item);
                    }

                    typeMenu.show(reportTable.getTableHeader(), e.getX(), e.getY());
                }

                if ("Account Number".equals(columnName)) {
                    JPopupMenu accountMenu = new JPopupMenu();

                    JMenuItem sortAsc = new JMenuItem("Sort Ascending");
                    sortAsc.addActionListener(ev -> sortByAccountNumber(true));

                    JMenuItem sortDesc = new JMenuItem("Sort Descending");
                    sortDesc.addActionListener(ev -> sortByAccountNumber(false));

                    accountMenu.add(sortAsc);
                    accountMenu.add(sortDesc);

                    accountMenu.show(reportTable.getTableHeader(), e.getX(), e.getY());
                }

                if ("Amount".equals(columnName)) {
                    JPopupMenu amountMenu = new JPopupMenu();

                    JMenuItem sortAsc = new JMenuItem("Sort Ascending");
                    sortAsc.addActionListener(ev -> sortByAmount(true));

                    JMenuItem sortDesc = new JMenuItem("Sort Descending");
                    sortDesc.addActionListener(ev -> sortByAmount(false));

                    amountMenu.add(sortAsc);
                    amountMenu.add(sortDesc);

                    amountMenu.show(reportTable.getTableHeader(), e.getX(), e.getY());
                }
            }
        });
    }

//    private void updateButtonStyles() {
//        for (Component comp : buttonPanel.getComponents()) {
//            if (comp instanceof JButton) {
//                JButton btn = (JButton) comp;
//                if (btn.getText().equals(currentTransactionType)) {
//                    btn.setBackground(new Color(12, 46, 97));
//                    btn.setForeground(Color.WHITE);
//                } else {
//                    btn.setBackground(UIManager.getColor("Button.background"));
//                    btn.setForeground(Color.BLACK);
//                }
//            }
//        }
//    }

    private void filterTransactionsByType(String type) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getType().equalsIgnoreCase(type)) {
                filtered.add(t);
            }
        }
        displayTransactions(filtered);
    }

    private void sortByAccountNumber(boolean ascending) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        if (!currentTransactionType.equals("All")) {
            all.removeIf(t -> !t.getType().equalsIgnoreCase(currentTransactionType));
        }

        all.sort(Comparator.comparing(Transaction::getAccountNumber));
        if (!ascending) Collections.reverse(all);

        displayTransactions(all);
    }

    private void sortByAmount(boolean ascending) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        if (!currentTransactionType.equals("All")) {
            all.removeIf(t -> !t.getType().equalsIgnoreCase(currentTransactionType));
        }

        all.sort(Comparator.comparingDouble(Transaction::getAmount));
        if (!ascending) Collections.reverse(all);

        displayTransactions(all);
    }

    private void sortTransactionsByMonth() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        all.sort(Comparator.comparing(t -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getDate());
            return cal.get(Calendar.MONTH);
        }));
        displayTransactions(all);
    }

    private void sortTransactionsByDay() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        all.sort(Comparator.comparing(t -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getDate());
            return cal.get(Calendar.DAY_OF_MONTH);
        }));
        displayTransactions(all);
    }

    private void filterTransactionsToday() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.getTransactionsByDateRange(new Date(0), new Date())) {
            cal.setTime(t.getDate());
            if (cal.get(Calendar.YEAR) == year &&
                    cal.get(Calendar.MONTH) == month &&
                    cal.get(Calendar.DAY_OF_MONTH) == day) {
                filtered.add(t);
            }
        }
        displayTransactions(filtered);
    }

    private void filterTransactionsPrevious() {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.getTransactionsByDateRange(new Date(0), new Date())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(t.getDate());
            if (cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == yesterday.get(Calendar.MONTH) &&
                    cal.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH)) {
                filtered.add(t);
            }
        }
        displayTransactions(filtered);
    }

    private void loadReportOptions() {
        reportOptionsPanel.removeAll();

        JLabel header = new JLabel("Report Options for: " + currentTransactionType);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));
        reportOptionsPanel.add(header);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton dailyBtn = createActionButton("Daily Transactions", e -> loadDailyTransactions());
        JButton editBtn = createActionButton("Edit Transaction", e -> editSelectedTransaction());
        JButton summaryTypeBtn = createActionButton("Summary of Transactions", e -> loadSummaryByType());
        JButton perAccountBtn = createActionButton("Per Account", e -> loadSummaryPerAccount());
        JButton onDemandBtn = createActionButton("On Demand Reports", e -> onDemandSearch());


        actionsPanel.add(dailyBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(summaryTypeBtn);
        actionsPanel.add(perAccountBtn);
        actionsPanel.add(onDemandBtn);

        reportOptionsPanel.add(actionsPanel);
        reportOptionsPanel.revalidate();
        reportOptionsPanel.repaint();
    }

    private JButton createActionButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 32));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.addActionListener(action);
        return btn;
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);

        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Transaction t : all) {
            if (currentTransactionType.equals("All") || t.getType().equalsIgnoreCase(currentTransactionType)) {
                tableModel.addRow(new Object[]{
                        sdf.format(t.getDate()),
                        t.getType(),
                        t.getAccountNumber(),
                        t.getAmount(),
                        t.getDescription()
                });
            }
        }
    }

    private void loadDailyTransactions() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        JTextField startDateField = new JTextField();
        panel.add(startDateField);

        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        JTextField endDateField = new JTextField();
        panel.add(endDateField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Select Date Range", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDateField.getText().trim());
                Date end = sdf.parse(endDateField.getText().trim());
                List<Transaction> filtered = TransactionManager.getTransactionsByDateRange(start, end);
                displayTransactions(filtered);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedTransaction() {
        int row = reportTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction to edit from the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter new amount:", tableModel.getValueAt(row, 3));
        if (amountStr == null) return;
        try {
            double newAmount = Double.parseDouble(amountStr);
            tableModel.setValueAt(newAmount, row, 3); // Trigger table model update
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSummaryByType() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        tableModel.setRowCount(0); // Clear the table
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        double totalAmount = 0.0;
        for (Transaction t : all) {
            tableModel.addRow(new Object[]{
                    sdf.format(t.getDate()),
                    t.getType(),
                    t.getAccountNumber(),
                    t.getAmount(),
                    t.getDescription()
            });
        }
    }

    private void loadSummaryPerAccount() {
        Map<String, Double> summary = new HashMap<>();

        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        for (Transaction t : all) {
            if (currentTransactionType.equals("All") || t.getType().equalsIgnoreCase(currentTransactionType)) {
                summary.put(t.getAccountNumber(), summary.getOrDefault(t.getAccountNumber(), 0.0) + t.getAmount());
            }
        }

        tableModel.setRowCount(0);
        for (Map.Entry<String, Double> entry : summary.entrySet()) {
            tableModel.addRow(new Object[]{
                    "", "", entry.getKey(), entry.getValue(), "Summary for account"
            });
        }
    }

    private void onDemandSearch() {
        String keyword = JOptionPane.showInputDialog(this, "Enter keyword to search in transactions:");
        if (keyword == null || keyword.trim().isEmpty()) return;
        List<Transaction> results = TransactionManager.searchTransactions(keyword.trim().toLowerCase());
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching transactions found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        displayTransactions(results);
    }

    private void displayTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Transaction t : transactions) {
            if (currentTransactionType.equals("All") || t.getType().equalsIgnoreCase(currentTransactionType)) {
                tableModel.addRow(new Object[]{
                        sdf.format(t.getDate()),
                        t.getType(),
                        t.getAccountNumber(),
                        t.getAmount(),
                        t.getDescription()
                });
            }
        }
    }

    private void updateTransactionAmount(int row) {
        String dateStr = (String) tableModel.getValueAt(row, 0);
        String type = (String) tableModel.getValueAt(row, 1);
        String accountNumber = (String) tableModel.getValueAt(row, 2);
        String description = (String) tableModel.getValueAt(row, 4);
        double newAmount = (Double) tableModel.getValueAt(row, 3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Transaction> transactions = TransactionManager.getTransactionsByAccount(accountNumber);
        for (Transaction t : transactions) {
            if (sdf.format(t.getDate()).equals(dateStr)
                    && t.getType().equalsIgnoreCase(type)
                    && t.getDescription().equals(description)) {
                boolean success = TransactionManager.updateTransaction(t.getTransactionId(), newAmount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Transaction updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadTransactions();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Could not find matching transaction to update.", "Error", JOptionPane.ERROR_MESSAGE);
    }

}