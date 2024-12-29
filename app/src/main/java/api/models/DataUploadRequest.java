package api.models;

public class DataUploadRequest {
    private String contractNumber;
    private String meterNumber;
    private String meterReading;
    private String photoBase64;

    public DataUploadRequest(String contractNumber, String meterNumber, String meterReading, String photoBase64) {
        this.contractNumber = contractNumber;
        this.meterNumber = meterNumber;
        this.meterReading = meterReading;
        this.photoBase64 = photoBase64;
    }
}
