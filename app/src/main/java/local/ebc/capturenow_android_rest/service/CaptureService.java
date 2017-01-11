package local.ebc.capturenow_android_rest.service;

import java.util.List;

import local.ebc.capturenow_android_rest.model.Capture;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Martijn on 06/12/2016.
 */

public interface CaptureService {

    // Retrieve all existing Captures.
    @GET("captures")
    Call<List<Capture>> listCaptures();

    // Retrieve latest capture.
    @GET("captures/latest")
    Call<Capture> getLatestCapture();

    // Create a new Capture
    @Multipart
    @POST("captures")
    Call<ResponseBody> createCapture(@Part MultipartBody.Part file,
                                     @Part("title") RequestBody title,
                                     @Part("longitude") RequestBody longitude,
                                     @Part("latitude") RequestBody latitude,
                                     @Part("timestamp") RequestBody timestamp
                                     );

}
