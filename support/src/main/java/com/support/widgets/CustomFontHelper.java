package com.support.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.support.R;
public class CustomFontHelper {
    private static Typeface typeface = null;
    private static String customFont = "";

    public static void setCustomTypeFace(ButtonPlus buttonPlus, Context context, AttributeSet set) {
        typeface = getTypeface(context, set);
        if (typeface != null) {
            buttonPlus.setTypeface(typeface);
        }
    }

    public static void setCustomTypeFace(TextViewPlus textViewPlus, Context context, AttributeSet set) {
        typeface = getTypeface(context, set);
        if (typeface != null) {
            textViewPlus.setTypeface(typeface);
        }
    }

    public static void setCustomTypeFace(EditTextPlus editTextPlus, Context context, AttributeSet set) {
        typeface = getTypeface(context, set);
        if (typeface != null) {
            editTextPlus.setTypeface(typeface);
        }
    }

    private static Typeface getTypeface(Context context, AttributeSet set) {
        if (set != null) {
            TypedArray typedArray = context.obtainStyledAttributes(set, R.styleable.WidgetPlus);
            String fontLink = typedArray.getString(R.styleable.WidgetPlus_textFont);
            typedArray.recycle();
            if (TextUtils.isEmpty(fontLink)) {
                fontLink = context.getString(R.string.default_font);
            }
            if (!customFont.equals(fontLink)) {
                customFont = fontLink;
                try {
                    typeface = Typeface.createFromAsset(context.getAssets(), "font/" + customFont);
                } catch (Exception e) {
                    return null;
                }
            }
            return typeface;
        } else return null;
    }
}
