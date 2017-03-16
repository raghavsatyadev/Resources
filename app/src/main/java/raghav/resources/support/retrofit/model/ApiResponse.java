package raghav.resources.support.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public int status;

}
