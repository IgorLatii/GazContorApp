package api.models;

public class LoginRequest {
    private String contractNumber;
    private String meterNumber;

    public LoginRequest(String contractNumber, String meterNumber) {
        this.contractNumber = contractNumber;
        this.meterNumber = meterNumber;
    }
}
