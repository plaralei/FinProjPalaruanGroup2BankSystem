package Group2BankSystem.exceptions;

public class AccountClosedException extends Exception {
    public AccountClosedException(String accountNumber) {
        super("Account " + accountNumber + " is closed");
    }
}