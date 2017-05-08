package raghav.resources.support.retrofit.network;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;

    public static Retrofit getClient() {
        if (retrofit == null) {


            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WebService.BaseLink + WebService.Version)
                    .client(getOKHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static OkHttpClient getOKHttpClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .retryOnConnectionFailure(true)
                    .addInterceptor(interceptor)
                    .readTimeout(60, TimeUnit.SECONDS);

//        Code for basic Authentication
            if (!TextUtils.isEmpty(WebService.API_USERNAME) && !TextUtils.isEmpty(WebService.API_PASSWORD)) {
                String credentials = WebService.API_USERNAME + ":" + WebService.API_PASSWORD;
                final String basic =
                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", basic)
                                .header("Accept", "application/json")
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
            }
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * call this method to cancel all the calls of retrofit
     * Example
     * 1. In CoreActivity in onDestroy(); method
     * 2. In CoreFragment in onDestroyView(); method
     */
    public static void cancelAll() {
        getOKHttpClient().dispatcher().cancelAll();
    }

    public interface WebService {
        String Address = "IP Address";
        String BaseLink = "http://" + Address + "other url part";
        String Version = "";
        String API_USERNAME = null;
        String API_PASSWORD = null;
    }
}