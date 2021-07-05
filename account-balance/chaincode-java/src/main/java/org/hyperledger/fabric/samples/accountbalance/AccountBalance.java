package org.hyperledger.fabric.samples.accountbalance;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "account",
        info = @Info(
                title = "Account Balance",
                description = "The hyperlegendary account balance",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "lijiaqi.datju@gmail.com",
                        name = "Kay Li",
                        url = "https://hyperledger.example.com")))
@Default
public final class AccountBalance implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AccountBalanceErrors {
        ACCOUNT_ALREADY_EXISTS,
        ACCOUNT_NOT_FOUND,
        INSUFFICIENT_BALANCE,
        INVALID_TRANSFER_AMOUNT
    }

    /**
     * Creates some initial account balance on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateAccount(ctx, "acc1", 5000.0, "pk1");
        CreateAccount(ctx, "acc2", 8000.0, "pk2");
        CreateAccount(ctx, "acc3", 8000.0, "pk2");
        CreateAccount(ctx, "acc4", 8000.0, "pk2");
        CreateAccount(ctx, "acc5", 8000.0, "pk2");
        CreateAccount(ctx, "acc6", 8000.0, "pk2");
        CreateAccount(ctx, "acc7", 8000.0, "pk2");
        CreateAccount(ctx, "acc8", 8000.0, "pk2");
        CreateAccount(ctx, "acc9", 8000.0, "pk2");

    }

    /**
     * Creates a new account on the ledger.
     *
     * @param ctx the transaction context
     * @param accountID the ID of the new account
     * @param balance the balance of the new account
     * @param publicKey the public key for the new account
     * @return the created account
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Account CreateAccount(final Context ctx, final String accountID, final Double balance, final String publicKey) {
        ChaincodeStub stub = ctx.getStub();

        if (AccountExists(ctx, accountID)) {
            String errorMessage = String.format("Account %s already exists", accountID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.ACCOUNT_ALREADY_EXISTS.toString());
        }

        Account account = new Account(accountID, balance, publicKey);
        String accountJSON = genson.serialize(account);
        stub.putStringState(accountID, accountJSON);

        return account;
    }

    /**
     * Retrieves the balance of the account ID from the ledger.
     *
     * @param ctx the transaction context
     * @param accountID the ID of the account
     * @return the account balance found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Double GetBalance(final Context ctx, final String accountID) {
        ChaincodeStub stub = ctx.getStub();
        String accountJSON = stub.getStringState(accountID);

        if (accountJSON == null || accountJSON.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", accountID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account account = genson.deserialize(accountJSON, Account.class);
        return account.getBalance();
    }

    /**
     * Checks the existence of the account on the ledger
     *
     * @param ctx the transaction context
     * @param accountID the ID of the new account
     * @return boolean indicating the existence of the account
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AccountExists(final Context ctx, final String accountID) {
        ChaincodeStub stub = ctx.getStub();
        String accountJSON = stub.getStringState(accountID);

        return (accountJSON != null && !accountJSON.isEmpty());
    }

    /**
     * Sends some coins from one account to another account on the ledger.
     *
     * @param ctx the transaction context
     * @param fromAccountID the ID of the account being debited
     * @param toAccountID the ID of the account being credited
     * @param amount the amount being transfered
     * @return the updated account balance
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Double TransferCoins(final Context ctx, final String fromAccountID, final String toAccountID, final Double amount) {
        ChaincodeStub stub = ctx.getStub();
        String fromAccountJSON = stub.getStringState(fromAccountID);
        String toAccountJSON = stub.getStringState(toAccountID);

        if (fromAccountJSON == null || fromAccountJSON.isEmpty()) {
            String errorMessage = String.format("From account %s does not exist", fromAccountID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.ACCOUNT_NOT_FOUND.toString());
        }
        if (toAccountJSON == null || toAccountJSON.isEmpty()) {
            String errorMessage = String.format("To account %s does not exist", toAccountID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.ACCOUNT_NOT_FOUND.toString());
        }
        if (amount <= 0) {
            String errorMessage = String.format("Tranfer amount must be larger than zero, received %s", amount);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.INVALID_TRANSFER_AMOUNT.toString());
        }
        Account fromAccount = genson.deserialize(fromAccountJSON, Account.class);
        Account toAccount = genson.deserialize(toAccountJSON, Account.class);

        Double fromAccountBalance = fromAccount.getBalance();
        if (fromAccountBalance < amount) {
            String errorMessage = String.format("Insufficient account balance %s for transfer %s", fromAccountBalance, amount);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountBalanceErrors.INSUFFICIENT_BALANCE.toString());
        }
        Account newFromAccount = new Account(fromAccount.getAccountID(), fromAccount.getBalance() - amount, fromAccount.getPublicKey());
        Account newToAccount = new Account(toAccount.getAccountID(), toAccount.getBalance() + amount, toAccount.getPublicKey());

        String newFromAccountJSON = genson.serialize(newFromAccount);
        String newToAccountJSON = genson.serialize(newToAccount);

        stub.putStringState(fromAccountID, newFromAccountJSON);
        stub.putStringState(toAccountID, newToAccountJSON);

        return fromAccount.getBalance() - amount;
    }

}
