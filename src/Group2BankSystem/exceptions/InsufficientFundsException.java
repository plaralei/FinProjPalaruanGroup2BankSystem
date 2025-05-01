package Group2BankSystem.exceptions;

public class InsufficientFundsException extends Exception {
    private final double available;
    private final double requested;

    public InsufficientFundsException(double available, double requested) {
        super(String.format("Insufficient funds. Available: %.2f, Requested: %.2f", available, requested));
        this.available = available;
        this.requested = requested;
    }
}