package com.support.utils;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;


public class SnackBarUtil {

    public static Snackbar showSnackBar(View view, String message, @StringRes int actionLabel, View.OnClickListener clickListener) {
        Snackbar snackbar = Snackbar.make(view, message.trim(), Snackbar.LENGTH_LONG);
        if (clickListener != null) {
            snackbar.setAction(actionLabel, clickListener);
        }
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackBar(View view, String message, String actionLabel, View.OnClickListener clickListener) {
        Snackbar snackbar = Snackbar.make(view, message.trim(), Snackbar.LENGTH_LONG);
        if (clickListener != null) {
            snackbar.setAction(actionLabel, clickListener);
        }
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message.trim(), Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackBar(View view, @StringRes int messageID) {
        Snackbar snackbar = Snackbar.make(view, ResourceUtils.getString(messageID).trim(), Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }
}
