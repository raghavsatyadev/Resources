package com.support.base;

import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.support.R;
import com.support.utils.ResourceUtils;
import com.support.widgets.TextViewPlus;

import io.reactivex.disposables.CompositeDisposable;

public abstract class CoreActivity extends AppCompatActivity {
    Toolbar toolbar = null;
    private CompositeDisposable compositeDisposable;
    private CoreActivity coreActivity;

    /**
     * apply fonts on toolbar title
     *
     * @param toolbar {@link Toolbar}
     */
    public static void applyFontForToolbarTitle(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(CoreApp.getInstance().getAssets(), "font/" + ResourceUtils.getString(R.string.default_font));
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, @StringRes int title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID) {
        setDefaults(coreActivity, layoutRes, ResourceUtils.getString(title), backgroundDrawable, backgroundID, false, false);
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, @StringRes int title, boolean isBackEnabled,
                            boolean isToolBarEnabled) {
        setDefaults(coreActivity, layoutRes, ResourceUtils.getString(title), 0, 0, isBackEnabled, isToolBarEnabled);
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, String title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID) {
        setDefaults(coreActivity, layoutRes, title, backgroundDrawable, backgroundID, false, false);
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, String title, boolean isBackEnabled,
                            boolean isToolBarEnabled) {
        setDefaults(coreActivity, layoutRes, title, 0, 0, isBackEnabled, isToolBarEnabled);
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes) {
        setDefaults(coreActivity, layoutRes, null, 0, 0, false, false);
    }

    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, @StringRes int title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID, boolean isBackEnabled, boolean isToolBarEnabled) {
        setDefaults(coreActivity, layoutRes, ResourceUtils.getString(title), backgroundDrawable, backgroundID, isBackEnabled, isToolBarEnabled);
    }

    /**
     * @param layoutRes          id of layout file
     * @param title              activity title in String or @StringRes
     * @param backgroundDrawable background image drawable
     * @param backgroundID       background image resource id
     * @param isBackEnabled      enable going back into previous activity with toolbar back button
     * @param isToolBarEnabled   enable toolbar
     */
    public void setDefaults(CoreActivity coreActivity, @LayoutRes int layoutRes, String title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID, boolean isBackEnabled, boolean isToolBarEnabled) {
        this.coreActivity = coreActivity;
        coreActivity.setContentView(layoutRes);

        setScreenBackground(backgroundID, backgroundDrawable);

        setToolBar(isToolBarEnabled, isBackEnabled, title);

        createReference();
        setListeners(true);
    }

    /**
     * setting the toolbar
     *
     * @param isToolBarEnabled true if toolbar must be enabled
     * @param isBackEnabled    true for enabling back button
     * @param title            title title in {@link String} format
     */
    private void setToolBar(boolean isToolBarEnabled, boolean isBackEnabled, String title) {
        if (isToolBarEnabled) {
            toolbar = (Toolbar) coreActivity.findViewById(R.id.tool_bar);
            coreActivity.setSupportActionBar(toolbar);

            ActionBar actionBar = coreActivity.getSupportActionBar();
            assert actionBar != null;
            setBackButton(actionBar, isBackEnabled);
            enableTitle(title, actionBar);
            formatTitle();
        }
    }

    /**
     * enabling the title on {@link ActionBar}
     *
     * @param title title in {@link String} format
     */
    private void enableTitle(String title, ActionBar actionBar) {
        if (title != null) {
            actionBar.setDisplayShowTitleEnabled(false);

            changeTitle(title);
        }
    }

    /**
     * set font and color of title
     */
    private void formatTitle() {
        if (toolbar != null) {
            toolbar.setTitleTextColor(ResourceUtils.getColor(R.color.tool_bar_text_color));
            applyFontForToolbarTitle(toolbar);
        }
    }

    /**
     * set back button of toolbar
     *
     * @param actionBar     {@link ActionBar}
     * @param isBackEnabled true for enabling back button
     */
    private void setBackButton(ActionBar actionBar, boolean isBackEnabled) {
        if (isBackEnabled) {
            try {
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (toolbar != null)
                    toolbar.setNavigationIcon(ResourceUtils.getDrawable(R.drawable.ic_left_arrow));
            } catch (NullPointerException ignored) {

            }
        }
    }

    /**
     * setting background of activity
     *
     * @param backgroundID       ID of background layout
     * @param backgroundDrawable ID of background drawable
     */
    private void setScreenBackground(@IdRes int backgroundID, @DrawableRes int backgroundDrawable) {
        if (backgroundDrawable != 0 && backgroundID != 0)
            Glide.with(this).load(backgroundDrawable).centerCrop().dontAnimate()
                    .into((ImageView) findViewById(backgroundID));
    }

    /**
     * to set title in the middle of toolbar
     *
     * @param title in {@link String}
     */
    private void changeTitleTV(String title) {
        if (toolbar != null && title != null) {
            TextViewPlus toolBarTitle = (TextViewPlus) toolbar.findViewById(R.id.txt_tool_bar_title);
            toolBarTitle.setTextColor(ResourceUtils.getColor(R.color.tool_bar_text_color));
            toolBarTitle.setText(title);
        }
    }

    /**
     * to set title in the middle of toolbar
     *
     * @param titleRes ID of string resource
     */
    public void changeTitleTV(@StringRes int titleRes) {
        String title = ResourceUtils.getString(titleRes);
        changeTitleTV(title);
    }

    /**
     * set title of toolbar in default position
     *
     * @param title in {@link String}
     */
    public void changeTitle(String title) {
        if (toolbar != null && !TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }
    }

    /**
     * set title of toolbar in default position
     *
     * @param titleRes ID of string resource
     */
    public void changeTitle(@StringRes int titleRes) {
        String title = ResourceUtils.getString(titleRes);
        changeTitle(title);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    /**
     * @return {@link Toolbar}
     */
    public Toolbar getToolBar() {
        return toolbar;
    }

    public abstract void createReference();

    /**
     * enables or disables listeners
     *
     * @param state true for enabling the listeners
     */
    protected abstract void setListeners(boolean state);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                coreActivity.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * get {@link CompositeDisposable} for RXJava
     *
     * @return {@link CompositeDisposable}
     */
    public CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    /**
     * call this in {@link android.app.Activity}.onDestroy()
     */
    public void cancelCalls() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
