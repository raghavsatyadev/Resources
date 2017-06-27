package raghav.resources.support.retrofit;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import raghav.resources.support.utils.AppLog;

public class MultiPartUtil {
    public static MultipartBody.Part getMultiPartImage(String fileParameter, File file) {
        if (file != null)
            return MultipartBody.Part.createFormData(fileParameter, file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));
        return null;
    }

    public static MultipartBody.Part getMultiPartImage(String fileParameter, String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            File file = new File(filepath);
            try {
                return getMultiPartImage(fileParameter, file);
            } catch (Exception e) {
                AppLog.log(false, "ApiHelper " + "getMultiPartImage: ", e);
                return null;
            }
        } else {
            return null;
        }

    }

    public static RequestBody getMultipartString(String string) {
        return RequestBody.create(MediaType.parse("text/plain"), string);
    }
}
