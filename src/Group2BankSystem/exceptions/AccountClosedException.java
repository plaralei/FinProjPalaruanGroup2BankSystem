package Group2BankSystem.exceptions;

public class AccountClosedException extends Exception {
    public AccountClosedException() { super("Account is closed"); }
    public AccountClosedException(String accountNumber) {
        super("Account closed: " + accountNumber);
    }
}