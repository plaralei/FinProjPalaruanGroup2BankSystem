package Group2BankSystem.exceptions;
/**
 * Exception thrown when transaction amount exceeds the allowed limit.
 * <p>
 * This exception is typically used to prevent transactions that go beyond
 * a predefined maximum value set by the system or user policy.
 * </p>
 */
 public class TransactionLimitException extends Exception {
    /**
     * Constructs a new TransactionLimitException with a detailed message
     * that includes the specified transaction limit.
     *
     * @param limit Maximum allowed transaction amount
     */
    public TransactionLimitException(double limit) {
        super(String.format("Transaction exceeds limit of %.2f", limit));
    }
}