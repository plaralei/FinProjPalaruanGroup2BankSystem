package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.table.*;
import java.util.*;
import java.util.stream.Collectors;

public class CheckingAccountTableModel extends AbstractTableModel {
    private final String[] columns = {"Account Number", "Account Holder", "Balance", "Overdraft Limit", "Status"};
    private List<CheckingAccount> accounts;

    public CheckingAccountTableModel() {
        refresh();
    }

    @Override
    public int getRowCount() {
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        CheckingAccount account = accounts.get(row);
        return switch (column) {
            case 0 -> account.getAccountNumber();
            case 1 -> account.getAccountHolderName();
            case 2 -> String.format("%.2f", account.getBalance());
            case 3 -> String.format("%.2f", account.getOverdraftLimit());
            case 4 -> account.isActive() ? "Active" : "Closed";
            default -> null;
        };
    }

    public CheckingAccount getAccountAt(int row) {
        return accounts.get(row);
    }

    public void refresh() {
        accounts = AccountManager.getAccounts().stream()
                .filter(acc -> acc instanceof CheckingAccount)
                .map(acc -> (CheckingAccount) acc)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
}