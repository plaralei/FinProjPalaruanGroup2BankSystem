package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides a user interface panel for generating transaction reports.
 * <p>
 * Allows filtering transactions by type and date range, displaying them in a table,
 * and exporting results or updating transaction amounts directly from the UI.
 * </p>
 */
public class ReportPanel extends JPanel {

    /** Reference to the main application frame. */
    private final MainFrame frame;

    /** Table for displaying the filtered transaction report. */
    private final JTable reportTable;

    /** Table model to manage the transaction data. */
    private final DefaultTableModel tableModel;

    /** Dropdown for selecting transaction type filters. */
    private final JComboBox<String> typeCombo;

    /** Field for selecting the start date of the report. */
    private final JFormattedTextField startDateField;

    /** Field for selecting the end date of the report. */
    private final JFormattedTextField endDateField;

    /**
     * Constructs the report panel and initializes its components.
     *
     * @param frame the parent frame that contains this panel
     */
    public ReportPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        typeCombo = new JComboBox<>(new String[]{"All", "Deposits", "Withdrawals", "Transfers", "Fees", "Interest"});

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDateField = new JFormattedTextField(dateFormat);
        endDateField = new JFormattedTextField(dateFormat);
        startDateField.setValue(new Date());
        endDateField.setValue(new Date());

        JButton filterButton = new JButton("Apply Filters");
        JButton exportButton = new JButton("Export CSV"); // Placeholder for future export functionality

        filterPanel.add(new JLabel("Transaction Type:"));
        filterPanel.add(typeCombo);
        filterPanel.add(new JLabel("Start Date:"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("End Date:"));
        filterPanel.add(endDateField);
        filterPanel.add(filterButton);
        filterPanel.add(exportButton);

        String[] columns = {"Date", "Account", "Type", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Allow editing only for the amount column
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        JButton backButton = new JButton("Back to Main");
        backButton.addActionListener(e -> frame.showCard(MainFrame.MENU));

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        filterButton.addActionListener(this::applyFilters);

        reportTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                updateTransactionAmount(e.getFirstRow());
            }
        });
    }

    /**
     * Applies the selected filters to the transaction report.
     *
     * @param e the event triggered by the filter button
     */
    private void applyFilters(ActionEvent e) {
        Date startDate = (Date) startDateField.getValue();
        Date endDate = (Date) endDateField.getValue();
        String type = (String) typeCombo.getSelectedItem();

        tableModel.setRowCount(0); // Clear previous results

        TransactionManager.getTransactionsByDateRange(startDate, endDate)
                .stream()
                .filter(t -> type.equals("All") || t.getType().equalsIgnoreCase(type))
                .forEach(t -> tableModel.addRow(new Object[]{
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.getDate()),
                        t.getAccountNumber(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription()
                }));
    }

    /**
     * Updates the transaction amount in the system when edited in the report table.
     *
     * @param row the index of the row being edited
     */
    private void updateTransactionAmount(int row) {
        String transactionId = (String) tableModel.getValueAt(row, 0); // Note: this currently references the Date column, not ID
        double newAmount = (Double) tableModel.getValueAt(row, 3);

        // This assumes transaction ID is in the "Date" column — which is incorrect unless modified.
        if (TransactionManager.updateTransaction(transactionId, newAmount)) {
            JOptionPane.showMessageDialog(this, "Transaction updated successfully");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update transaction",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}