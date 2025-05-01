package Group2BankSystem.exceptions;

public class TransactionLimitException extends Exception {
    public TransactionLimitException(double limit) {
        super(String.format("Transaction exceeds limit of %.2f", limit));
    }
}