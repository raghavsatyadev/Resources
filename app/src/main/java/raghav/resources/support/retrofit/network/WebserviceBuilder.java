package raghav.resources.support.retrofit.network;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import raghav.resources.support.retrofit.model.ApiResponse;
import raghav.resources.support.retrofit.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebserviceBuilder {

    /**
     * normal POST method
     *
     * @param some_field_1
     * @param some_field_2
     * @return
     */
    @FormUrlEncoded
    @POST("postMethodURLLastPart")
    Call<LoginResponse> postMethod(
            @Field("some_field_1") String some_field_1,
            @Field("some_field_2") String some_field_2);

    /**
     * normal GET Method
     *
     * @param field
     * @return
     */
    @GET("getMethodURLLastPart")
    Call<LoginResponse> getMethod(
            @Query("some_field") String field
    );

    /**
     * For sending file in multipart call ApiHelper.getMultipartFile(File file);
     * for sending string in RequestBody call ApiHelper.getMultipartString(String string)
     * for uploading files with other parameters
     *
     * @param someInteger random integer
     * @param someString  random String
     * @param file        random file
     */
    @POST("update-profile")
    Call<ApiResponse> multipart(
            @Part("someInteger") int someInteger,
            @Part("someString") RequestBody someString,
            @Part MultipartBody.Part file);

    /**
     * for using custom Sub URL
     *
     * @param URL Custom Sub URL
     * @return
     */
    //    for custom get URL
    @GET("getsubmenu/{URL}")
    Call<ApiResponse> customGetURl(
            @Path("URL") String URL);

}
