package com.support.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ImplicitIntentUtils {
    public static void callPhone(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public static void emailTo(String emailID, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                emailID, null));
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void openGoogleMaps(String keyword, Context context) {
        try {
            String mapString = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(keyword, "utf-8");
            openBrowser(mapString, context);
        } catch (UnsupportedEncodingException e) {
            AppLog.log(false, "ImplicitIntentUtils: " + "openGoogleMaps: ", e);
        }
    }

    public static void openWhatsapp(String phoneNumberWithCountryCode, Context context) {
        if (phoneNumberWithCountryCode.contains("+")) {
            phoneNumberWithCountryCode = phoneNumberWithCountryCode
                    .replaceAll("\\+", "")
                    .replaceAll(" ", "")
                    .replaceAll("\\.", "")
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "");
        }
        openBrowser("https://api.whatsapp.com/send?phone=" + phoneNumberWithCountryCode, context);
    }

    public static void openBrowser(String url, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
