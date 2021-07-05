package application.java.server;

// import java.nio.file.Path;
// import java.nio.file.Paths;
// import org.hyperledger.fabric.gateway.Contract;
// import org.hyperledger.fabric.gateway.Gateway;
// import org.hyperledger.fabric.gateway.Network;
// import org.hyperledger.fabric.gateway.Wallet;
// import org.hyperledger.fabric.gateway.Wallets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		try {
			// EnrollAdmin.main(null);
			// RegisterUser.main(null);
			SpringApplication.run(App.class, args);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		// try (Gateway gateway = FabricNetwork.connect()) {

		// 	// get the network and contract
		// 	Network network = gateway.getNetwork("mychannel");
		// 	Contract contract = network.getContract("account");

		// 	byte[] result;

		// 	System.out.println("Submit Transaction: InitLedger creates the initial set of accounts on the ledger.");
		// 	contract.submitTransaction("InitLedger");

		// 	System.out.println("\n");
		// 	System.out.println("Submit Transaction: CreateAccount account99");
		// 	contract.submitTransaction("CreateAccount", "account99", "100", "Tom");

		// 	System.out.println("\n");
		// 	System.out.println("Evaluate Transaction: GetBalance account99");
		// 	result = contract.evaluateTransaction("GetBalance", "account99");
		// 	System.out.println("result: " + new String(result));

		// 	System.out.println("\n");
		// 	System.out.println("Evaluate Transaction: AccountExists account99");
		// 	result = contract.evaluateTransaction("AccountExists", "account99");
		// 	System.out.println("result: " + new String(result));

		// 	System.out.println("\n");
		// 	System.out.println("Submit Transaction: TransferCoins 10 from accountID account99 > accountID account1");
		// 	contract.submitTransaction("TransferCoins", "account99", "acc1", "10");

		// 	System.out.println("\n");
		// 	System.out.println("Evaluate Transaction: GetBalance account99");
		// 	result = contract.evaluateTransaction("GetBalance", "account99");
		// 	System.out.println("result: " + new String(result));
		// }
		// catch(Exception e){
		// 	System.err.println(e);
		// }

	}
}
