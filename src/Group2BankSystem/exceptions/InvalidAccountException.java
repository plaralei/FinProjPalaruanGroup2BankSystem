package Group2BankSystem.exceptions;

public class InvalidAccountException extends Exception {
    public InvalidAccountException(String accountNumber) {
        super("Invalid account number: " + accountNumber);
    }
}