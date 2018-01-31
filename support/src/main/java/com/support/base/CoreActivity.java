package com.support.base;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.support.R;
import com.support.utils.AppLog;
import com.support.utils.KeyBoardUtil;
import com.support.utils.ResourceUtils;

import io.reactivex.disposables.CompositeDisposable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public abstract class CoreActivity<T extends CoreActivity> extends AppCompatActivity {
    private final static String APP_THEME_NAME = ResourceUtils.getThemeName(R.style.AppTheme);
    private Toolbar toolbar = null;
    private CompositeDisposable compositeDisposable;
    private T activity;
    private LinearLayout progressBar;
    private Bundle savedInstanceState;
    private ActionBar actionBar;
    private boolean isThemeDefault;

    /**
     * apply fonts on toolbar title
     *
     * @param toolbar {@link Toolbar}
     */
    public void applyFontForToolbarTitle(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTextAppearance(activity, R.style.ToolBarTitleFont);
                    break;
                }
            }
        }
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public void setSavedInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
    }

    public void setDefaults(T coreActivity, @LayoutRes int layoutRes, @StringRes int title) {
        setDefaults(coreActivity, layoutRes, ResourceUtils.getString(title), false, false);
    }

    public void setDefaults(T coreActivity, @LayoutRes int layoutRes, @StringRes int title, boolean isBackEnabled,
                            boolean isToolBarEnabled) {
        setDefaults(coreActivity, layoutRes, ResourceUtils.getString(title), isBackEnabled, isToolBarEnabled);
    }

    public void setDefaults(T coreActivity, @LayoutRes int layoutRes, String title) {
        setDefaults(coreActivity, layoutRes, title, false, false);
    }

    public void setDefaults(T coreActivity, @LayoutRes int layoutRes) {
        setDefaults(coreActivity, layoutRes, null, false, false);
    }

    public T getActivity() {
        return activity;
    }

    /**
     * @param layoutRes        id of layout file
     * @param title            activity title in String or @StringRes
     * @param isBackEnabled    enable going back into previous activity with toolbar back button
     * @param isToolBarEnabled enable toolbar
     */
    public void setDefaults(T activity, @LayoutRes int layoutRes, String title, boolean isBackEnabled, boolean isToolBarEnabled) {
        this.activity = activity;
        activity.setContentView(layoutRes);
        isThemeDefault = ResourceUtils
                .getThemeName(activity, getTheme())
                .equals(APP_THEME_NAME);
        setToolBar(isToolBarEnabled, isBackEnabled, title);
        setProgressBarColor();
        createReference();
        setListeners(true);
    }

    private void setProgressBarColor() {
        LinearLayout linearLayout = getProgressBar();
        if (linearLayout != null) {
            MaterialProgressBar progressBar = linearLayout.findViewById(R.id.progress_bar);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    isThemeDefault ? ResourceUtils.getColor(R.color.colorPrimary) : ResourceUtils.getColor(R.color.colorAccent),
                    PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void hideProgressBar() {
        setProgressBar(false);
    }

    public void showProgressBar() {
        setProgressBar(true);
    }

    public LinearLayout getProgressBar() {
        return activity.findViewById(R.id.box_progress_bar);
    }

    public void setProgressBar(boolean showProgressBar) {
        if (progressBar == null) progressBar = getProgressBar();
        if (progressBar != null)
            progressBar.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);

        disableScreen(showProgressBar);
    }

    public void disableScreen(boolean disable) {
        if (disable) {
            if (progressBar != null)
                KeyBoardUtil.hideKeyBoard(progressBar);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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
            toolbar = activity.findViewById(R.id.tool_bar);

            activity.setSupportActionBar(toolbar);

            actionBar = activity.getSupportActionBar();
            assert actionBar != null;
            setBackButton(isBackEnabled);
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
            applyFontForToolbarTitle(toolbar);
        }
    }

    /**
     * set back button of toolbar
     *
     * @param isBackEnabled true for enabling back button
     */
    public void setBackButton(boolean isBackEnabled) {
        try {
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(isBackEnabled);
        } catch (NullPointerException ignored) {
            AppLog.log(false, "CoreActivity: " + "setBackButton: ", ignored);
        }
    }

    /**
     * set title of toolbar in default position
     *
     * @param title in {@link String}
     */
    public void changeTitle(String title) {
        if (toolbar != null && title != null) {
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
                activity.onBackPressed();
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
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        if (compositeDisposable.isDisposed()) compositeDisposable = new CompositeDisposable();
        return compositeDisposable;
    }

    public void cancelCalls() {
        hideProgressBar();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
