package Group2BankSystem.exceptions;
/**
 * Exception thrown when transaction is attempted with insufficient funds.
 * <p>
 * This occurs when the requested amount exceeds the available balance.
 * </p>
 */
 public class InsufficientFundsException extends Exception {
    private final double available;
    private final double requested;
    /**
     * Constructs a new InsufficientFundsException with a detailed message
     * including the available & requested amounts.
     *
     * @param available The current available balance
     * @param requested The amount attempted to be withdrawn or transferred
     */
    public InsufficientFundsException(double available, double requested) {
        super(String.format("Insufficient funds. Available: %.2f, Requested: %.2f", available, requested));
        this.available = available;
        this.requested = requested;
    }
}