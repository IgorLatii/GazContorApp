package api;

import api.models.DataUploadRequest;
import api.models.LoginRequest;
import api.models.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<ServerResponse> login(@Body LoginRequest loginRequest);

    @POST("uploadData")
    Call<ServerResponse> uploadData(@Body DataUploadRequest request);
}


