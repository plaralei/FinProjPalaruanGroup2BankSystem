package Group2BankSystem.exceptions;

public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super("Invalid amount: " + message);
    }
}