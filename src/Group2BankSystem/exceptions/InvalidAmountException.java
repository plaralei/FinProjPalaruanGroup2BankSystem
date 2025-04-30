package Group2BankSystem.exceptions;

public class InvalidAmountException extends Exception {
    public InvalidAmountException() { super("Invalid amount"); }
    public InvalidAmountException(String message) { super(message); }
    public InvalidAmountException(double min, double max) {
        super(String.format("Amount must be between %.2f and %.2f", min, max));
    }
}