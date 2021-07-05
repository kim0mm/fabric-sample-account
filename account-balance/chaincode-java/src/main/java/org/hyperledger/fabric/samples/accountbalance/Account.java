package org.hyperledger.fabric.samples.accountbalance;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Account {

    @Property()
    private final String accountID;

    @Property()
    private final Double balance;

    @Property()
    private final String publicKey;

    public String getAccountID() {
        return accountID;
    }

    public Double getBalance() {
        return balance;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Account(@JsonProperty("accountID") final String accountID, @JsonProperty("balance") final Double balance,
           @JsonProperty("publicKey") final String publicKey) {
        this.accountID = accountID;
        this.balance = balance;
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Account other = (Account) obj;

        return Objects.deepEquals(
                new String[] {getAccountID(), getPublicKey()},
                new String[] {other.getAccountID(), other.getPublicKey()})
                &&
                Objects.deepEquals(
                new Double[] {getBalance()},
                new Double[] {other.getBalance()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountID(), getBalance(), getPublicKey());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [accountID=" + accountID + ", balance="
                + balance + ", publicKey=" + publicKey + "]";
    }
}
