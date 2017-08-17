package com.support.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtil {
    private Context context;
    private InputMethodManager inputManager = null;

    private KeyBoardUtil(Context context) {
        this.context = context;
    }

    public static void hideKeyBoard(View v) {
        getInstance(v.getContext()).hideSoftKeyboard(v);
    }

    public static KeyBoardUtil getInstance(Context context) {
        return new KeyBoardUtil(context);
    }

    public void hideSoftKeyboard(View v) {
        if (context != null) {
            inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
