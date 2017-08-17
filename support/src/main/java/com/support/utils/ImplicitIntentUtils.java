package com.support.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

    public static void openBrowser(String url, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
