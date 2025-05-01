package Group2BankSystem.model;

import Group2BankSystem.exceptions.*;

public class AccountFactory {
    private static int accountCounter = 202500000;

    public static BankAccount createAccount(String accountHolderName, double initialAmount, String accountType)
            throws InvalidAmountException {
        String accountNumber = generateAccountNumber();

        switch (accountType) {
            case "Checking Account":
                return new CheckingAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance + getOverdraftLimit() < amount) {
                            throw new InsufficientFundsException(balance + getOverdraftLimit(), amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
            case "Investment Account":
                return new InvestmentAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return balance * 0.05; // Example fixed interest
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - amount < minimumBalance) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
            case "Credit Card Account":
                return new CreditCardAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        try {
                            charge(amount);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean payCard(double amount) {
                        try {
                            makePayment(amount);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
            default:
                return new BankAccount(accountNumber, accountHolderName, initialAmount) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - amount < minimumBalance) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
        }
    }

    public static BankAccount convertAccount(BankAccount oldAccount, String newType)
            throws InvalidAmountException, AccountConversionException {

        if (!isValidConversion(oldAccount.getAccountType(), newType)) {
            throw new AccountConversionException("Invalid account conversion");
        }

        try {
            return switch (newType) {
                case "Checking Account" -> new CheckingAccount(
                        oldAccount.getAccountNumber(),
                        oldAccount.getAccountHolderName(),
                        oldAccount.getBalance()
                ) {
                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance + getOverdraftLimit() < amount) {
                            throw new InsufficientFundsException(balance + getOverdraftLimit(), amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
                case "Investment Account" -> new InvestmentAccount(
                        oldAccount.getAccountNumber(),
                        oldAccount.getAccountHolderName(),
                        oldAccount.getBalance()
                ) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return balance * 0.05;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - minimumBalance < amount) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
                case "Credit Card Account" -> new CreditCardAccount(
                        oldAccount.getAccountNumber(),
                        oldAccount.getAccountHolderName(),
                        oldAccount.getBalance()
                ) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        try {
                            charge(amount);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean payCard(double amount) {
                        try {
                            makePayment(amount);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
                default -> new BankAccount(
                        oldAccount.getAccountNumber(),
                        oldAccount.getAccountHolderName(),
                        oldAccount.getBalance()
                ) {
                    @Override
                    public boolean encashCheck(double amount) throws InsufficientFundsException, AccountClosedException, InvalidAmountException {
                        return false;
                    }

                    @Override
                    public boolean chargeToCard(double amount) {
                        return false;
                    }

                    @Override
                    public boolean payCard(double amount) {
                        return false;
                    }

                    @Override
                    public double computeMonthlyInterest() {
                        return 0;
                    }

                    @Override
                    public void setActive(boolean active) {
                        this.isActive = active;
                    }

                    @Override
                    protected void validateSufficientFunds(double amount) throws InsufficientFundsException {
                        if (balance - minimumBalance < amount) {
                            throw new InsufficientFundsException(balance - minimumBalance, amount);
                        }
                    }

                    @Override
                    public boolean transfer(BankAccount target, double amountValue) {
                        return false;
                    }
                };
            };
        } catch (InvalidAmountException e) {
            throw new AccountConversionException("Conversion failed: " + e.getMessage());
        }
    }

    private static boolean isValidConversion(String oldType, String newType) {
        return !oldType.equals("Credit Card Account") || newType.equals("Bank Account");
    }

    private static String generateAccountNumber() {
        accountCounter++;
        return String.valueOf(accountCounter);
    }

    public static class AccountConversionException extends Exception {
        public AccountConversionException(String message) {
            super(message);
        }
    }
}
