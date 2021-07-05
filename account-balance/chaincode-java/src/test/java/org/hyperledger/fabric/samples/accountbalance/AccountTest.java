package org.hyperledger.fabric.samples.accountbalance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AccountTest {
    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Account account = new Account("account1", 20.0, "Guy");

            assertThat(account).isEqualTo(account);
        }

        @Test
        public void isSymmetric() {
            Account accountA = new Account("account1", 20.0, "Guy");
            Account accountB = new Account("account1", 20.0, "Guy");

            assertThat(accountA).isEqualTo(accountB);
            assertThat(accountB).isEqualTo(accountA);
        }

        @Test
        public void isTransitive() {
            Account accountA = new Account("account1", 20.0, "Guy");
            Account accountB = new Account("account1", 20.0, "Guy");
            Account accountC = new Account("account1", 20.0, "Guy");
            assertThat(accountA).isEqualTo(accountB);
            assertThat(accountB).isEqualTo(accountC);
            assertThat(accountA).isEqualTo(accountC);
        }

        @Test
        public void handlesInequality() {
            Account accountA = new Account("account1", 20.0, "Guy");
            Account accountB = new Account("account2", 23.0, "Guy");

            assertThat(accountA).isNotEqualTo(accountB);
        }

        @Test
        public void handlesOtherObjects() {
            Account accountA = new Account("account1", 20.0, "Guy");
            String accountB = "not a account";

            assertThat(accountA).isNotEqualTo(accountB);
        }

        @Test
        public void handlesNull() {
            Account account = new Account("account1", 20.0, "Guy");

            assertThat(account).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesAccount() {
        Account account = new Account("account1", 20.0, "Guy");
        assertThat(account.toString()).isNotEqualTo("Account@e04f6c53 [accountID=account1, balance=20.0, publicKey=Guy]");
    }
}
