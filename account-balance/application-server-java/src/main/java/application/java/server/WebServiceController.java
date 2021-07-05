package application.java.server;

import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebServiceController {
   @RequestMapping(value = "/get-balance", method = RequestMethod.POST)
   public ResponseEntity<Object> getBalance(@RequestBody Request req) {
      try (Gateway gateway = FabricNetwork.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("account");
         byte[] result;
         result = contract.evaluateTransaction("GetBalance", req.getParams().get("accountId").toString());
			System.out.println("result: " + new String(result));
         return new ResponseEntity<>(String.format("Balance is %s", new String(result)), HttpStatus.OK);

      } catch(Exception e){
			System.err.println(e);
         return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

		}
   }

   @RequestMapping(value = "/send", method = RequestMethod.POST)
   public ResponseEntity<Object> send(@RequestBody Request req) {
      try (Gateway gateway = FabricNetwork.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("account");
         byte[] result;
         result = contract.evaluateTransaction(
            "TransferCoins", req.getParams().get("fromAccountId").toString(), 
            req.getParams().get("toAccountId").toString(), 
            req.getParams().get("amount").toString());
			System.out.println("result: " + new String(result));
         return new ResponseEntity<>(String.format("Transfer Success, remaining balance is %s", new String(result)), HttpStatus.OK);

      } catch(Exception e){
			System.err.println(e);
         return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);

		}
   }
}
