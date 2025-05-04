package Group2BankSystem.model;

import Group2BankSystem.exceptions.AccountClosedException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final Map<String, BankAccount> accounts = new ConcurrentHashMap<>();

    static {
        loadAccounts();
    }

    public static synchronized void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        saveAccounts();
    }

    public static List<BankAccount> getAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @SuppressWarnings("unchecked")
    public static <T extends BankAccount> List<T> getAccounts(Class<T> type) {
        return accounts.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public static Optional<BankAccount> getAccountByNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    public static synchronized void updateAccount(BankAccount updatedAccount) {
        accounts.put(updatedAccount.getAccountNumber(), updatedAccount);
        saveAccounts();
    }

    public static List<BankAccount> searchAccounts(String query) {
        String lowerQuery = query.toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getAccountNumber().contains(query) ||
                        a.getAccountHolderName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public static synchronized void applyMonthlyInterest() {
        getAccounts(InvestmentAccount.class).forEach(account -> {
            try {
                account.applyMonthlyInterest();
            } catch (AccountClosedException e) {
                System.err.println("Skipped closed account: " + account.getAccountNumber());
            }
        });
        saveAccounts();
    }

    public static synchronized void reloadAccounts() {
        loadAccounts();
    }

    private static synchronized void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(new ArrayList<>(accounts.values()));
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static synchronized void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
            List<BankAccount> loaded = (List<BankAccount>) ois.readObject();
            accounts.clear();
            loaded.forEach(acc -> accounts.put(acc.getAccountNumber(), acc));
        } catch (FileNotFoundException e) {
            System.out.println("No existing accounts file. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }
}
