package Group2BankSystem.exceptions;
/**
 * Exception thrown when operation is attempted on a closed account.
 * <p>
 * This exception is used when an account that has been marked as closed
 * is accessed, either for transactions or other operations.
 * </p>
 */
 public class AccountClosedException extends Exception {
    /**
     * Constructs a new AccountClosedException with a message that includes
     * the specified closed account number.
     *
     * @param accountNumber The closed account number
     */
    public AccountClosedException(String accountNumber) {
        super("Account " + accountNumber + " is closed");
    }
}