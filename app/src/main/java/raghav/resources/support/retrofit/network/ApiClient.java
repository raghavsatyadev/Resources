package raghav.resources.support.retrofit.network;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {


            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WebService.BaseLink + WebService.Version).client(getOKHttpClient().build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static OkHttpClient.Builder getOKHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS);

//        Code for basic Authentication
        if (WebService.API_USERNAME != null && WebService.API_PASSWORD != null) {
            String credentials = WebService.API_USERNAME + ":" + WebService.API_PASSWORD;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            okHttpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }
        return okHttpClient;
    }

    public interface WebService {
        String Address = "IP Address";
        String BaseLink = "http://" + Address + "other url part";
        String Version = "";
        String SuccessStatus = "ok";
        String API_USERNAME = null;
        String API_PASSWORD = null;
    }
}