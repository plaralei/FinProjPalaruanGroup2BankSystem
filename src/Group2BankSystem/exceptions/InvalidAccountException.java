package Group2BankSystem.exceptions;
/**
 * Exception thrown when operation references an invalid or non-existent account number.
 * <p>
 * This may occur if the account number is not found in the system,
 * is incorrectly formatted, or has been closed or deactivated.
 * </p>
 *
 * Examples of when this might be thrown:
 * <ul>
 *     <li>Attempting to access an account that doesn't exist</li>
 *     <li>Providing a malformed account number</li>
 *     <li>Using an account that has been closed</li>
 * </ul>
 */
 public class InvalidAccountException extends Exception {
    /**
     * Constructs a new InvalidAccountException with a message that includes
     * the specified invalid account number.
     *
     * @param accountNumber The account number that caused the exception
     */
    public InvalidAccountException(String accountNumber) {
        super("Invalid account number: " + accountNumber);
    }
}