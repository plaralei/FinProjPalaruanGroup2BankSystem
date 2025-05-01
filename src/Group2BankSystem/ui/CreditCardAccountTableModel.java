package Group2BankSystem.ui;

import Group2BankSystem.model.*;
import javax.swing.table.*;
import java.util.*;
import java.util.stream.Collectors;

public class CreditCardAccountTableModel extends AbstractTableModel {
    private final String[] columns = {"Account Number", "Account Holder", "Balance Due", "Credit Limit", "Status"};
    private List<CreditCardAccount> accounts;

    public CreditCardAccountTableModel() {
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

    public CreditCardAccount getAccountAt(int row) {
        return accounts.get(row);
    }

    public void refresh() {
        accounts = AccountManager.getAccounts().stream()
                .filter(acc -> false)
                .map(CreditCardAccount.class::cast)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
}