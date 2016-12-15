package raghav.resources.support.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import raghav.resources.R;

public class ButtonPlus extends AppCompatButton {
    private static Typeface typeface = null;
    private static String customFont = "";

    public ButtonPlus(Context context) {
        super(context);
    }

    public ButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeFace(context, attrs);
    }

    public ButtonPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomTypeFace(context, attrs);
    }

    private void setCustomTypeFace(Context context, AttributeSet set) {
        if (set != null && !isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(set, R.styleable.WidgetPlus);
            String fontLink = a.getString(R.styleable.WidgetPlus_textFont);
            a.recycle();
            if (fontLink != null) {
                if (!customFont.equals(fontLink)) {
                    customFont = fontLink;
                    try {
                        typeface = Typeface.createFromAsset(context.getAssets(), "font/" + customFont);
                    } catch (Exception e) {
                        return;
                    }
                }
                setTypeface(typeface);
            }
        }
    }
}
