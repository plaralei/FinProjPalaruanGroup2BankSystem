package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.table.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A table model implementation for displaying credit card accounts in a JTable.
 * This class extends AbstractTableModel and manages the display of CreditCardAccount
 * objects with appropriate columns and formatting.
 */
public class CreditCardAccountTableModel extends AbstractTableModel {
    /** Column headers for the table. */
    private final String[] columns = {"Account Number", "Account Holder", "Balance Due", "Credit Limit", "Status"};

    /** List of credit card accounts to be displayed in the table. */
    private List<CreditCardAccount> accounts;

    /**
     * Constructs a new CreditCardAccountTableModel and initializes it with
     * all available credit card accounts.
     */
    public CreditCardAccountTableModel() {
        refresh();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return the number of credit card accounts in the model
     */
    @Override
    public int getRowCount() {
        return accounts.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns defined for this table
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns the name of the specified column.
     *
     * @param column the column index
     * @return the name of the specified column
     */
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * Returns the value at the specified row and column.
     * Maps the CreditCardAccount properties to the appropriate columns with formatting.
     * Note that balance due is displayed as a positive number.
     *
     * @param row the row index
     * @param column the column index
     * @return the formatted value at the specified cell
     */
    @Override
    public Object getValueAt(int row, int column) {
        CreditCardAccount account = accounts.get(row);
        return switch (column) {
            case 0 -> account.getAccountNumber();
            case 1 -> account.getAccountHolderName();
            case 2 -> String.format("%.2f", -account.getBalance());
            case 3 -> String.format("%.2f", account.getCreditLimit());
            case 4 -> account.isActive() ? "Active" : "Closed";
            default -> null;
        };
    }

    /**
     * Returns the CreditCardAccount object at the specified row.
     *
     * @param row the row index
     * @return the CreditCardAccount at the specified row
     */
    public CreditCardAccount getAccountAt(int row) {
        return accounts.get(row);
    }

    /**
     * Refreshes the table data by retrieving all credit card accounts from the AccountManager.
     * Filters the accounts to include only CreditCardAccount instances.
     * Notifies listeners that the table data has changed.
     */
    public void refresh() {
        accounts = AccountManager.getAccounts().stream()
                .filter(acc -> acc instanceof CreditCardAccount)
                .map(acc -> (CreditCardAccount) acc)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
}