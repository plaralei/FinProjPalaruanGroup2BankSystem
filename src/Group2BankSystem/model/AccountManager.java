package Group2BankSystem.model;

import Group2BankSystem.exceptions.AccountClosedException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages all bank account operations including storage, retrieval, updates,
 * interest applications, and persistence.
 */
public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final Map<String, BankAccount> accounts = new ConcurrentHashMap<>();

    static {
        loadAccounts();
    }

    /**
     * Adds a new account to the system and saves the updated account list to disk.
     *
     * @param account the {@link BankAccount} to be added
     */
    public static synchronized void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        saveAccounts();
    }

    /**
     * Returns all stored accounts as a list.
     *
     * @return list of all {@link BankAccount}s
     */
    public static List<BankAccount> getAccounts() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Returns a filtered list of accounts matching a specific subclass of {@link BankAccount}.
     *
     * @param type the class type to filter accounts by
     * @return list of matching account types
     * @param <T> subclass of BankAccount
     */
    @SuppressWarnings("unchecked")
    public static <T extends BankAccount> List<T> getAccounts(Class<T> type) {
        return accounts.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the account number to search for
     * @return an {@link Optional} containing the account if found
     */
    public static Optional<BankAccount> getAccountByNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    /**
     * Updates an existing account and persists changes.
     *
     * @param updatedAccount the updated {@link BankAccount}
     */
    public static synchronized void updateAccount(BankAccount updatedAccount) {
        accounts.put(updatedAccount.getAccountNumber(), updatedAccount);
        saveAccounts();
    }

    /**
     * Searches for accounts by account number or account holder name.
     *
     * @param query the search string
     * @return list of matching {@link BankAccount}s
     */
    public static List<BankAccount> searchAccounts(String query) {
        String lowerQuery = query.toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getAccountNumber().contains(query) ||
                        a.getAccountHolderName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Applies monthly interest to all investment accounts and saves changes.
     */
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

    /**
     * Reloads account data from disk.
     */
    public static synchronized void reloadAccounts() {
        loadAccounts();
    }

    /**
     * Saves all account data to the accounts file.
     */
    private static synchronized void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(new ArrayList<>(accounts.values()));
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    /**
     * Loads account data from disk into memory.
     */
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
