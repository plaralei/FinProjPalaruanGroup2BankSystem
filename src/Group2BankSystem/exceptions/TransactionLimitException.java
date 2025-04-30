package Group2BankSystem.exceptions;

public class TransactionLimitException extends Exception {
    public TransactionLimitException() { super("Transaction limit exceeded"); }
    public TransactionLimitException(double limit) {
        super(String.format("Limit: %.2f", limit));
    }
}