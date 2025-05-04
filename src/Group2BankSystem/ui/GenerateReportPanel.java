package Group2BankSystem.ui;

import Group2BankSystem.model.Transaction;
import Group2BankSystem.model.TransactionManager;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GenerateReportPanel extends JPanel {
    private final JTabbedPane tabbedPane;

    private final DefaultTableModel dailyTableModel;
    private final JTable dailyTable;
    private final JButton dailyLoadBtn;
    private final JButton dailyEditBtn;

    private final DefaultTableModel summaryTableModel;
    private final JTable summaryTable;
    private final JButton summaryLoadBtn;

    private final DefaultTableModel perAccountTableModel;
    private final JTable perAccountTable;
    private final JButton perAccountLoadBtn;

    private final DefaultTableModel onDemandTableModel;
    private final JTable onDemandTable;
    private final JTextField onDemandSearchField;
    private final JButton onDemandSearchBtn;

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String currentTransactionType = "All";

    public GenerateReportPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setOpaque(true);

        JPanel dailyPanel = new JPanel(new BorderLayout(10, 10));
        dailyPanel.setBackground(Color.WHITE);
        dailyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dailyLoadBtn = new JButton("Load Daily Transactions");
        styleButton(dailyLoadBtn);
        dailyLoadBtn.addActionListener(e -> loadDailyTransactions());

        dailyEditBtn = new JButton("Edit Selected Transaction");
        styleButton(dailyEditBtn);
        dailyEditBtn.addActionListener(e -> editTransaction());

        String[] dailyCols = {"Date", "Type", "Account Number", "Amount", "Description"};
        dailyTableModel = new DefaultTableModel(dailyCols, 0) {
            public boolean isCellEditable(int row, int col) { return col == 3; }
            public Class<?> getColumnClass(int col) { return col == 3 ? Double.class : String.class; }
        };
        dailyTable = new JTable(dailyTableModel);
        customizeTable(dailyTable);

        dailyTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                updateTransactionAmount(e.getFirstRow());
            }
        });

        dailyTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = dailyTable.columnAtPoint(e.getPoint());
                String columnName = dailyTable.getColumnName(col);
                showColumnPopupForDaily(e, columnName);
            }
        });

        JScrollPane dailyScroll = new JScrollPane(dailyTable);
        dailyScroll.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));

        JPanel dailyButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        dailyButtonPanel.setBackground(Color.WHITE);
        dailyButtonPanel.add(dailyLoadBtn);
        dailyButtonPanel.add(dailyEditBtn);

        dailyPanel.add(dailyButtonPanel, BorderLayout.NORTH);
        dailyPanel.add(dailyScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Daily Transactions", dailyPanel);

        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        summaryLoadBtn = new JButton("Load Summary of Transactions");
        styleButton(summaryLoadBtn);
        summaryLoadBtn.addActionListener(e -> loadSummaryOfTransactions());

        String[] summaryCols = {"Transaction Type", "Total Amount"};
        summaryTableModel = new DefaultTableModel(summaryCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col) { return col == 1 ? Double.class : String.class; }
        };
        summaryTable = new JTable(summaryTableModel);
        customizeTable(summaryTable);

        summaryTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = summaryTable.columnAtPoint(e.getPoint());
                if (col == 1) sortSummaryByAmount();
            }
        });

        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        summaryScroll.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));

        summaryPanel.add(summaryLoadBtn, BorderLayout.NORTH);
        summaryPanel.add(summaryScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Summary of Transactions", summaryPanel);

        JPanel perAccountPanel = new JPanel(new BorderLayout(10, 10));
        perAccountPanel.setBackground(Color.WHITE);
        perAccountPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        perAccountLoadBtn = new JButton("Load Per Account Summary");
        styleButton(perAccountLoadBtn);
        perAccountLoadBtn.addActionListener(e -> loadSummaryPerAccount());

        String[] perAccountCols = {"Account Number", "Total Amount"};
        perAccountTableModel = new DefaultTableModel(perAccountCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col) { return col == 1 ? Double.class : String.class; }
        };
        perAccountTable = new JTable(perAccountTableModel);
        customizeTable(perAccountTable);

        perAccountTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = perAccountTable.columnAtPoint(e.getPoint());
                if (col == 0) sortPerAccountByAccountNumber();
                else if (col == 1) sortPerAccountByAmount();
            }
        });

        JScrollPane perAccountScroll = new JScrollPane(perAccountTable);
        perAccountScroll.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));

        perAccountPanel.add(perAccountLoadBtn, BorderLayout.NORTH);
        perAccountPanel.add(perAccountScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Per Account", perAccountPanel);

        JPanel onDemandPanel = new JPanel(new BorderLayout(10, 10));
        onDemandPanel.setBackground(Color.WHITE);
        onDemandPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel onDemandSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        onDemandSearchPanel.setBackground(Color.WHITE);

        onDemandSearchField = new JTextField(30);
        onDemandSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        onDemandSearchBtn = new JButton("Search");
        styleButton(onDemandSearchBtn);
        onDemandSearchBtn.addActionListener(e -> performOnDemandSearch());

        onDemandSearchPanel.add(new JLabel("Keyword or Field Search:"));
        onDemandSearchPanel.add(onDemandSearchField);
        onDemandSearchPanel.add(onDemandSearchBtn);

        String[] onDemandCols = {"Date", "Type", "Account Number", "Amount", "Description"};
        onDemandTableModel = new DefaultTableModel(onDemandCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col) { return col == 3 ? Double.class : String.class; }
        };
        onDemandTable = new JTable(onDemandTableModel);
        customizeTable(onDemandTable);

        onDemandTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = onDemandTable.columnAtPoint(e.getPoint());
                String colName = onDemandTable.getColumnName(col);
                sortOnDemandTable(colName);
            }
        });

        JScrollPane onDemandScroll = new JScrollPane(onDemandTable);
        onDemandScroll.setBorder(BorderFactory.createLineBorder(new Color(12, 46, 97), 2));

        onDemandPanel.add(onDemandSearchPanel, BorderLayout.NORTH);
        onDemandPanel.add(onDemandScroll, BorderLayout.CENTER);

        tabbedPane.addTab("On Demand Reports", onDemandPanel);

        add(tabbedPane, BorderLayout.CENTER);

        loadDailyTransactions();
    }

    private void showColumnPopupForDaily(MouseEvent e, String columnName) {
        switch (columnName) {
            case "Date": showDatePopup(e); break;
            case "Type": showTypePopup(e); break;
            case "Account Number": showAccountPopup(e); break;
            case "Amount": showAmountPopup(e); break;
        }
    }

    private void showDatePopup(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem sortMonth = new JMenuItem("Sort by Month");
        sortMonth.addActionListener(a -> sortTransactionsByMonth());
        JMenuItem sortDay = new JMenuItem("Sort by Day");
        sortDay.addActionListener(a -> sortTransactionsByDay());
        JMenuItem filterToday = new JMenuItem("Filter: Today");
        filterToday.addActionListener(a -> filterTransactionsToday());
        JMenuItem filterPrev = new JMenuItem("Filter: Previous");
        filterPrev.addActionListener(a -> filterTransactionsPrevious());

        menu.add(sortMonth);
        menu.add(sortDay);
        menu.addSeparator();
        menu.add(filterToday);
        menu.add(filterPrev);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showTypePopup(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        String[] types = {"ACCOUNT_CLOSED", "DEPOSIT", "WITHDRAWAL", "TRANSFER", "INTEREST", "FEES"};
        for (String t : types) {
            JMenuItem item = new JMenuItem(t);
            item.addActionListener(a -> filterTransactionsByType(t));
            menu.add(item);
        }
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showAccountPopup(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem asc = new JMenuItem("Sort Ascending");
        asc.addActionListener(a -> sortByAccountNumber(true));
        JMenuItem desc = new JMenuItem("Sort Descending");
        desc.addActionListener(a -> sortByAccountNumber(false));
        menu.add(asc);
        menu.add(desc);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showAmountPopup(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem asc = new JMenuItem("Sort Ascending");
        asc.addActionListener(a -> sortByAmount(true));
        JMenuItem desc = new JMenuItem("Sort Descending");
        desc.addActionListener(a -> sortByAmount(false));
        menu.add(asc);
        menu.add(desc);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void loadDailyTransactions() {
        Date start = getStartOfDay(new Date());
        Date end = getEndOfDay(new Date());
        List<Transaction> dailyTransactions = TransactionManager.getTransactionsByDateRange(start, end);
        populateTable(dailyTableModel, dailyTransactions);
    }

    private Date getEndOfDay(Date date) {
        return date;
    }

    private Date getStartOfDay(Date date) {
        return date;
    }

    private void loadSummaryOfTransactions() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        Map<String, Double> summary = new HashMap<>();
        for (Transaction t : all) {
            summary.put(t.getType(), summary.getOrDefault(t.getType(), 0.0) + t.getAmount());
        }
        summaryTableModel.setRowCount(0);
        summary.forEach((type, amount) -> summaryTableModel.addRow(new Object[]{type, amount}));
    }

    private void loadSummaryPerAccount() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        Map<String, Double> summary = new HashMap<>();
        for (Transaction t : all) {
            summary.put(t.getAccountNumber(), summary.getOrDefault(t.getAccountNumber(), 0.0) + t.getAmount());
        }
        perAccountTableModel.setRowCount(0);
        summary.forEach((acc, amount) -> perAccountTableModel.addRow(new Object[]{acc, amount}));
    }

    private void performOnDemandSearch() {
        String keyword = onDemandSearchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Transaction> results = TransactionManager.searchTransactions(keyword.toLowerCase());
        populateTable(onDemandTableModel, results);
    }

    private void populateTable(DefaultTableModel model, List<Transaction> transactions) {
        model.setRowCount(0);
        for (Transaction t : transactions) {
            if (currentTransactionType.equals("All") || t.getType().equalsIgnoreCase(currentTransactionType)) {
                model.addRow(new Object[]{
                        DATE_TIME_FORMAT.format(t.getDate()),
                        t.getType(),
                        t.getAccountNumber(),
                        t.getAmount(),
                        t.getDescription()
                });
            }
        }
    }

    private void editTransaction() {
        int selectedRow = dailyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object currentAmount = dailyTableModel.getValueAt(selectedRow, 3);
        String input = JOptionPane.showInputDialog(this, "Enter new amount:", currentAmount);
        if (input == null) return;
        try {
            double newAmount = Double.parseDouble(input);
            dailyTableModel.setValueAt(newAmount, selectedRow, 3);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTransactionAmount(int row) {
        String dateStr = (String) dailyTableModel.getValueAt(row, 0);
        String type = (String) dailyTableModel.getValueAt(row, 1);
        String accNumber = (String) dailyTableModel.getValueAt(row, 2);
        String desc = (String) dailyTableModel.getValueAt(row, 4);
        double newAmount = (Double) dailyTableModel.getValueAt(row, 3);

        List<Transaction> transactions = TransactionManager.getTransactionsByAccount(accNumber);
        for (Transaction t : transactions) {
            if (DATE_TIME_FORMAT.format(t.getDate()).equals(dateStr)
                    && t.getType().equalsIgnoreCase(type)
                    && t.getDescription().equals(desc)) {
                boolean success = TransactionManager.updateTransaction(t.getTransactionId(), newAmount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Transaction updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                loadDailyTransactions();
                break;
            }
        }
    }

    private void sortTransactionsByMonth() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        all.sort(Comparator.comparing(t -> {
            Calendar c = Calendar.getInstance();
            c.setTime(t.getDate());
            return c.get(Calendar.MONTH);
        }));
        populateTable(dailyTableModel, all);
    }

    private void sortTransactionsByDay() {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        all.sort(Comparator.comparing(t -> {
            Calendar c = Calendar.getInstance();
            c.setTime(t.getDate());
            return c.get(Calendar.DAY_OF_MONTH);
        }));
        populateTable(dailyTableModel, all);
    }

    private void filterTransactionsToday() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.getTransactionsByDateRange(new Date(0), new Date())) {
            cal.setTime(t.getDate());
            if (cal.get(Calendar.YEAR) == y && cal.get(Calendar.MONTH) == m && cal.get(Calendar.DAY_OF_MONTH) == d) filtered.add(t);
        }
        populateTable(dailyTableModel, filtered);
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
                    cal.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH)) filtered.add(t);
        }
        populateTable(dailyTableModel, filtered);
    }

    private void sortByAccountNumber(boolean ascending) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        if (!currentTransactionType.equals("All")) all.removeIf(t -> !t.getType().equalsIgnoreCase(currentTransactionType));
        all.sort(Comparator.comparing(Transaction::getAccountNumber));
        if (!ascending) Collections.reverse(all);
        populateTable(dailyTableModel, all);
    }

    private void sortByAmount(boolean ascending) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        if (!currentTransactionType.equals("All")) all.removeIf(t -> !t.getType().equalsIgnoreCase(currentTransactionType));
        all.sort(Comparator.comparingDouble(Transaction::getAmount));
        if (!ascending) Collections.reverse(all);
        populateTable(dailyTableModel, all);
    }

    private void filterTransactionsByType(String type) {
        List<Transaction> all = TransactionManager.getTransactionsByDateRange(new Date(0), new Date(Long.MAX_VALUE));
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getType().equalsIgnoreCase(type)) filtered.add(t);
        }
        currentTransactionType = type;
        populateTable(dailyTableModel, filtered);
    }

    private void sortSummaryByAmount() {
        int count = summaryTableModel.getRowCount();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(new Object[]{summaryTableModel.getValueAt(i, 0), summaryTableModel.getValueAt(i, 1)});
        }
        rows.sort(Comparator.comparing(o -> (Double) o[1]));
        summaryTableModel.setRowCount(0);
        for (Object[] r : rows) summaryTableModel.addRow(r);
    }

    private void sortPerAccountByAccountNumber() {
        int count = perAccountTableModel.getRowCount();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(new Object[]{perAccountTableModel.getValueAt(i, 0), perAccountTableModel.getValueAt(i, 1)});
        }
        rows.sort(Comparator.comparing(o -> o[0].toString()));
        perAccountTableModel.setRowCount(0);
        for (Object[] r : rows) perAccountTableModel.addRow(r);
    }

    private void sortPerAccountByAmount() {
        int count = perAccountTableModel.getRowCount();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(new Object[]{perAccountTableModel.getValueAt(i, 0), perAccountTableModel.getValueAt(i, 1)});
        }
        rows.sort(Comparator.comparingDouble(o -> (Double) o[1]));
        perAccountTableModel.setRowCount(0);
        for (Object[] r : rows) perAccountTableModel.addRow(r);
    }

    private void sortOnDemandTable(String columnName) {
        int count = onDemandTableModel.getRowCount();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(new Object[]{
                    onDemandTableModel.getValueAt(i, 0),
                    onDemandTableModel.getValueAt(i, 1),
                    onDemandTableModel.getValueAt(i, 2),
                    onDemandTableModel.getValueAt(i, 3),
                    onDemandTableModel.getValueAt(i, 4)
            });
        }
        Comparator<Object[]> comparator;
        switch (columnName) {
            case "Date": comparator = Comparator.comparing(o -> {
                try {
                    return DATE_TIME_FORMAT.parse(o[0].toString());
                } catch (Exception ex) {
                    return new Date(0);
                }
            }); break;
            case "Type": comparator = Comparator.comparing(o -> o[1].toString()); break;
            case "Account Number": comparator = Comparator.comparing(o -> o[2].toString()); break;
            case "Amount": comparator = Comparator.comparingDouble(o -> (Double) o[3]); break;
            case "Description": comparator = Comparator.comparing(o -> o[4].toString()); break;
            default: return;
        }
        rows.sort(comparator);
        onDemandTableModel.setRowCount(0);
        for (Object[] r : rows) onDemandTableModel.addRow(r);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(12, 46, 97));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(8, 32, 70)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(12, 46, 97)); }
        });
    }

    private void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
