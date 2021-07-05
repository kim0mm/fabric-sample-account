package org.hyperledger.fabric.samples.accountbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public final class AccountBalanceTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAccountResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> accountList;

        MockAccountResultsIterator() {
            super();

            accountList = new ArrayList<KeyValue>();

            accountList.add(new MockKeyValue("account1",
                    "{ \"accountID\": \"account1\", \"balance\": 5, \"publicKey\": \"Tomoko\"}"));
            accountList.add(new MockKeyValue("account2",
                    "{ \"accountID\": \"account2\", \"balance\": 5,\"publicKey\": \"Brad\"}"));
            accountList.add(new MockKeyValue("account3",
                    "{ \"accountID\": \"account3\", \"balance\": 10,\"publicKey\": \"Jin Soo\"}"));
            accountList.add(new MockKeyValue("account4",
                    "{ \"accountID\": \"account4\", \"balance\":10,\"publicKey\": \"Max\"}"));
            accountList.add(new MockKeyValue("account5",
                    "{ \"accountID\": \"account5\", \"balance\": 15,\"publicKey\": \"Adrian\"}"));
            accountList.add(new MockKeyValue("account6",
                    "{ \"accountID\": \"account6\", \"balance\": 15,\"publicKey\": \"Michel\"}"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return accountList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        AccountBalance contract = new AccountBalance();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeGetBalanceTransaction {

        @Test
        public void whenAccountExists() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1"))
                    .thenReturn("{ \"accountID\": \"account1\", \"balance\": 5.0, \"publicKey\": \"Tomoko\"}");

            Double balance = contract.GetBalance(ctx, "account1");

            assertThat(balance).isEqualTo(5.0);
        }

        @Test
        public void whenAccountDoesNotExist() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.GetBalance(ctx, "account1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Account account1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ACCOUNT_NOT_FOUND".getBytes());
        }
    }

    @Test
    void invokeInitLedgerTransaction() {
        AccountBalance contract = new AccountBalance();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        contract.InitLedger(ctx);

        InOrder inOrder = inOrder(stub);
        inOrder.verify(stub).putStringState("acc1", "{\"accountID\":\"acc1\",\"balance\":5000.0,\"publicKey\":\"pk1\"}");
        inOrder.verify(stub).putStringState("acc2", "{\"accountID\":\"acc2\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc3", "{\"accountID\":\"acc3\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc4", "{\"accountID\":\"acc4\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc5", "{\"accountID\":\"acc5\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc6", "{\"accountID\":\"acc6\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc7", "{\"accountID\":\"acc7\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc8", "{\"accountID\":\"acc8\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");
        inOrder.verify(stub).putStringState("acc9", "{\"accountID\":\"acc9\",\"balance\":8000.0,\"publicKey\":\"pk2\"}");

    }

    @Nested
    class TransferCoinsTransaction {

        @Test
        public void whenAccountExists() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1"))
                    .thenReturn("{ \"accountID\": \"account1\", \"balance\": 5, \"publicKey\": \"Tomoko\"}");
            when(stub.getStringState("account2"))
                    .thenReturn("{ \"accountID\": \"account2\", \"balance\": 10, \"publicKey\": \"Tomoko\"}");
            Double remainingBalance = contract.TransferCoins(ctx, "account1", "account2", 1.5);

            assertThat(remainingBalance).isEqualTo(3.5);
        }

        @Test
        public void whenFromAccountDoesNotExist() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1")).thenReturn("");
            when(stub.getStringState("account2")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferCoins(ctx, "account1", "account2", 2.0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("From account account1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ACCOUNT_NOT_FOUND".getBytes());
        }

        @Test
        public void whenToAccountDoesNotExist() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1")).thenReturn("{ \"accountID\": \"account1\", \"balance\": 5, \"publicKey\": \"Tomoko\"}");
            when(stub.getStringState("account2")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferCoins(ctx, "account1", "account2", 2.0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("To account account2 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ACCOUNT_NOT_FOUND".getBytes());
        }

        @Test
        public void whenInsufficientAmount() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1")).thenReturn("{ \"accountID\": \"account1\", \"balance\": 5.0, \"publicKey\": \"Tomoko\"}");
            when(stub.getStringState("account2")).thenReturn("{ \"accountID\": \"account2\", \"balance\": 5.0, \"publicKey\": \"Tomoko\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferCoins(ctx, "account1", "account2", 6.0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Insufficient account balance 5.0 for transfer 6.0");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INSUFFICIENT_BALANCE".getBytes());
        }

        @Test
        public void whenInvalidAmount() {
            AccountBalance contract = new AccountBalance();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("account1")).thenReturn("{ \"accountID\": \"account1\", \"balance\": 5, \"publicKey\": \"Tomoko\"}");
            when(stub.getStringState("account2")).thenReturn("{ \"accountID\": \"account2\", \"balance\": 5, \"publicKey\": \"Tomoko\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferCoins(ctx, "account1", "account2", -6.0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Tranfer amount must be larger than zero, received -6.0");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_TRANSFER_AMOUNT".getBytes());
        }
    }
}
