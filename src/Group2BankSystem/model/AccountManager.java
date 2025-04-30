package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final List<BankAccount> accounts = Collections.synchronizedList(new ArrayList<>());
    private static final Object fileLock = new Object();

    static {
        loadAccounts();
    }

    public static List<BankAccount> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public static void addAccount(BankAccount account) {
        synchronized(accounts) {
            accounts.add(account);
            saveAccounts();
        }
    }

    public static boolean deleteAccount(String accountNumber) {
        synchronized(accounts) {
            boolean removed = accounts.removeIf(a -> a.getAccountNumber().equals(accountNumber));
            if (removed) saveAccounts();
            return removed;
        }
    }

    public static Optional<BankAccount> getAccountByNumber(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    public static boolean updateAccount(BankAccount updatedAccount) {
        synchronized(accounts) {
            Optional<BankAccount> existing = accounts.stream()
                    .filter(a -> a.getAccountNumber().equals(updatedAccount.getAccountNumber()))
                    .findFirst();

            if (existing.isPresent()) {
                accounts.remove(existing.get());
                accounts.add(updatedAccount);
                saveAccounts();
                return true;
            }
            return false;
        }
    }

    public static List<BankAccount> searchAccounts(String query) {
        String lowerQuery = query.toLowerCase();
        return accounts.stream()
                .filter(a -> a.getAccountNumber().contains(query) ||
                        a.getAccountHolderName().toLowerCase().contains(lowerQuery) ||
                        a.getAccountType().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        synchronized(fileLock) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
                List<BankAccount> loaded = (List<BankAccount>) ois.readObject();
                accounts.clear();
                accounts.addAll(loaded);
            } catch (FileNotFoundException e) {
                System.out.println("No existing accounts file. Starting fresh.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading accounts: " + e.getMessage());
                accounts.clear();
            }
        }
    }

    public static void saveAccounts() {
        synchronized(fileLock) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
                oos.writeObject(new ArrayList<>(accounts));
            } catch (IOException e) {
                System.err.println("Error saving accounts: " + e.getMessage());
            }
        }
    }

    public static void applyMonthlyInterest() {
        synchronized(accounts) {
            accounts.stream()
                    .filter(a -> a instanceof InvestmentAccount)
                    .map(a -> (InvestmentAccount)a)
                    .forEach(account -> {
                        try {
                            account.applyMonthlyInterest();
                        } catch (AccountClosedException e) {
                            System.out.println("Skipping closed account: " + account.getAccountNumber());
                        }
                    });
            saveAccounts();
        }
    }
}