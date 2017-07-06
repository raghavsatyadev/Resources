package raghav.resources.support.retrofit.network;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import raghav.resources.support.retrofit.model.AndroidVersionModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
    Observable<AndroidVersionModel> postMethod(
            @Field("some_field_1") String some_field_1,
            @Field("some_field_2") String some_field_2);

    /**
     * normal GET Method
     *
     * @return Observable with single JSON Object
     */
    @GET("volley/person_object.json")
    Observable<AndroidVersionModel> getSingleObject();

    /**
     * normal GET Method
     *
     * @return Observable with JSON Array
     */
    @GET("volley/person_array.json")
    Observable<List<AndroidVersionModel>> getListObject();

    @GET("volley/person_object.json")
    Call<AndroidVersionModel> getCall();

    /**
     * For sending file in multipart call MultiPartUtil.getMultipartFile(File file);
     * for sending string in RequestBody call MultiPartUtil.getMultipartString(String string)
     * for uploading files with other parameters
     *
     * @param someInteger random integer
     * @param someString  random String
     * @param file        random file
     */
    @POST("update-profile")
    Observable<AndroidVersionModel> multipart(
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
    Observable<AndroidVersionModel> customGetURl(
            @Path("URL") String URL);

    enum ApiNames {
        login, profile, forgotPassword
    }

}
