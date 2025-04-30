package Group2BankSystem.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() { super("Insufficient funds"); }
    public InsufficientFundsException(double balance, double amount) {
        super(String.format("Balance: %.2f, Required: %.2f", balance, amount));
    }
}