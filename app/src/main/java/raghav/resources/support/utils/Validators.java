package raghav.resources.support.utils;

import android.text.TextUtils;
import android.util.Patterns;


public class Validators {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        return phone.length() > 9 && Patterns.PHONE.matcher(phone).matches();
    }
}
