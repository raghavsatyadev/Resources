package raghav.resources.support.utils;

import android.app.ProgressDialog;
import android.content.Context;

import raghav.resources.R;

public class ProgressUtils {
    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progress = new ProgressDialog(context, R.style.CustomProgressDialog);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage("Please Wait ...");
        progress.setCancelable(false);
        return progress;
    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
