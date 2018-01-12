package com.support.utils;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultiPartUtil {
    /**
     * Converts File into Multipart Object
     *
     * @param fileParameter parameter name which goes into APIs
     * @return {@link okhttp3.MultipartBody.Part}
     */
    public static MultipartBody.Part getMultiPartFile(String fileParameter, File file) {
        if (file != null) {
            String type = FileChooserUtil.getMimeType(file.getAbsolutePath());
            RequestBody body = RequestBody.create(MediaType.parse(type), file);
            return MultipartBody.Part.createFormData(fileParameter, file.getName(),
                    body);
        }
        return null;
    }

    /**
     * Converts File into Multipart Object
     *
     * @param fileParameter parameter name which goes into APIs
     * @param filepath      Absolute path of the file
     * @return {@link okhttp3.MultipartBody.Part}
     */
    public static MultipartBody.Part getMultiPartFile(String fileParameter, String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            File file = new File(filepath);
            try {
                return getMultiPartFile(fileParameter, file);
            } catch (Exception e) {
                AppLog.log(false, "MultiPartUtil " + "getMultiPartFile: ", e);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @return RequestBody of given String
     */
    public static RequestBody getMultipartString(String string) {
        if (string == null) string = "";
        return RequestBody.create(MultipartBody.FORM, string);
    }
}
