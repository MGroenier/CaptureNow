package local.ebc.capturenow_android_rest.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Martijn on 18/12/2016.
 */

public class ServiceGenerator {

//    public static final String API_BASE_URL = "http://172.15.3.38:8081/api/";
//    public static final String API_BASE_URL = "http://10.0.2.2:8081/api/";
    public static final String API_BASE_URL = "http://10.20.0.243:8081/api/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
