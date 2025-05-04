package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

/**
 * Abstract class representing a Credit Card Account in a banking system.
 * This class extends the BankAccount class and provides functionality
 * specific to credit card accounts, including credit limits and transaction handling.
 */
public abstract class CreditCardAccount extends BankAccount {

    /** The maximum amount that can be charged to the credit card. */
    private double creditLimit;

    /**
     * Constructs a CreditCardAccount with the specified account number,
     * account holder name, and credit limit.
     *
     * @param accountNumber the unique identifier for the account
     * @param accountHolderName the name of the account holder
     * @param creditLimit the maximum amount that can be charged to the account
     * @throws InvalidAmountException if the credit limit is less than 5000
     */
    public CreditCardAccount(String accountNumber, String accountHolderName, double creditLimit)
            throws InvalidAmountException {
        super(accountNumber, accountHolderName, 0); // Initial balance is set to 0
        if (creditLimit < 5000) throw new InvalidAmountException("Minimum credit limit: 5000");
        this.creditLimit = creditLimit;
        this.accountType = "Credit Card Account"; // Type of account
    }

    /**
     * Charges the specified amount to the credit card account.
     *
     * @param amount the amount to charge
     * @throws TransactionLimitException if the amount exceeds the available credit
     * @throws AccountClosedException if the account is closed
     */
    public void charge(double amount) throws TransactionLimitException, AccountClosedException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount > getAvailableCredit()) {
            throw new TransactionLimitException(getAvailableCredit());
        }

        balance += amount; // Update the balance with the charged amount
        updateLastModifiedDate(); // Update the last modified date
        TransactionManager.addTransaction(accountNumber, "CREDIT_CHARGE", amount, "Credit charge");
    }

    /**
     * Makes a payment towards the credit card account.
     *
     * @param amount the amount to pay
     * @throws AccountClosedException if the account is closed
     * @throws InvalidAmountException if the payment amount is not positive
     */
    public void makePayment(double amount) throws AccountClosedException, InvalidAmountException {
        if (!isActive) throw new AccountClosedException(accountNumber);
        if (amount <= 0) throw new InvalidAmountException("Payment amount must be positive");

        balance -= amount; // Deduct the payment amount from the balance
        updateLastModifiedDate(); // Update the last modified date
        TransactionManager.addTransaction(accountNumber, "PAYMENT", -amount, "Credit payment");
    }

    /**
     * Returns the available credit on the account, which is the credit limit
     * minus the current balance.
     *
     * @return the available credit
     */
    public double getAvailableCredit() {
        return creditLimit - balance;
    }

    /**
     * Returns the credit limit of the account.
     *
     * @return the credit limit
     */
    public double getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the active status of the account.
     *
     * @param active true to activate the account, false to deactivate
     */
    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Validates whether there are sufficient funds for a transaction.
     * This method is overridden but does not perform any validation
     * for credit card accounts as they operate on credit.
     *
     * @param amount the amount to validate
     * @throws InsufficientFundsException this method does not throw this exception
     */
    @Override
    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
        // No validation needed for credit card accounts
    }

    /**
     * Transfers the specified amount to the target bank account.
     *
     * @param target the target bank account to transfer to
     * @param amountValue the amount to transfer
     * @return true if the transfer is successful
     */
    @Override
    public abstract boolean transfer(BankAccount target, double amountValue);
}
