package Group2BankSystem.exceptions;
/**
 * Exception thrown when transaction amount is invalid.
 * <p>
 * This may occur if the amount is negative, zero, or otherwise violates
 * business rules defined by the system.
 * </p>
 *
 * Examples of invalid amounts might include:
 * <ul>
 *     <li>Negative deposit or withdrawal values</li>
 *     <li>Amounts exceeding account balance</li>
 *     <li>Incorrect formatting or null inputs</li>
 * </ul>
 */
 public class InvalidAmountException extends Exception {
    /**
     * Constructs a new InvalidAmountException with a specified detail message.
     *
     * @param message A description of why the amount is considered invalid
     */
    public InvalidAmountException(String message) {
        super("Invalid amount: " + message);
    }
}