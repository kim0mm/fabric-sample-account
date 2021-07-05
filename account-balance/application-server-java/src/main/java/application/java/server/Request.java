package application.java.server;

import java.util.Map;

public class Request {
    private int id;
    private String timestamp;
    private Map<String, Object> params;
    private String publicKey;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
     }

     public String getTimestamp() {
         return this.timestamp;
     }

     public void setTimestamp(String timestamp) {
         this.timestamp = timestamp;
     }

    public Map<String, Object> getParams() {
         return this.params;
     }

    public void setParams(Map<String, Object> params) {
         this.params = params;
     }

     public String getPublicKey() {
         return this.publicKey;
     }

     public void setPublicKey(String publicKey) {
         this.publicKey = publicKey;
     }

     public String getSignature() {
        String sig = "";
        return sig;
     }
}
