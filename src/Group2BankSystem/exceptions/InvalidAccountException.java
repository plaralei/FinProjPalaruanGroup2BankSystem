package Group2BankSystem.exceptions;

public class InvalidAccountException extends Exception {
    public InvalidAccountException() { super("Invalid account"); }
    public InvalidAccountException(String accountNumber) {
        super("Invalid account: " + accountNumber);
    }
}