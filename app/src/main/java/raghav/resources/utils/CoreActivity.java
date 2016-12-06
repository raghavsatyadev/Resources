package raghav.resources.utils;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import raghav.resources.R;
import raghav.resources.utils.widgets.TextViewPlus;


public abstract class CoreActivity extends AppCompatActivity {
    Toolbar toolbar = null;

    public void setDefaults(@LayoutRes int layoutRes, @StringRes int title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID) {
        setDefaults(layoutRes, ResourceUtils.getString(title), backgroundDrawable, backgroundID, false, false);
    }

    public void setDefaults(@LayoutRes int layoutRes, @StringRes int title, boolean isBackEnabled,
                            boolean isToolBarEnabled) {
        setDefaults(layoutRes, ResourceUtils.getString(title), 0, 0, isBackEnabled, isToolBarEnabled);
    }

    public void setDefaults(@LayoutRes int layoutRes, String title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID) {
        setDefaults(layoutRes, title, backgroundDrawable, backgroundID, false, false);
    }

    public void setDefaults(@LayoutRes int layoutRes, String title, boolean isBackEnabled,
                            boolean isToolBarEnabled) {
        setDefaults(layoutRes, title, 0, 0, isBackEnabled, isToolBarEnabled);
    }

    public void setDefaults(@LayoutRes int layoutRes) {
        setDefaults(layoutRes, null, 0, 0, false, false);
    }

    public void setDefaults(@LayoutRes int layoutRes, @StringRes int title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID, boolean isBackEnabled, boolean isToolBarEnabled) {
        setDefaults(layoutRes, ResourceUtils.getString(title), backgroundDrawable, backgroundID, isBackEnabled, isToolBarEnabled);
    }

    /**
     * @param layoutRes          id of layout file
     * @param title              activity title in String or @StringRes
     * @param backgroundDrawable background image drawable
     * @param backgroundID       background image resource id
     * @param isBackEnabled      enable going back into previous activity with toolbar back button
     * @param isToolBarEnabled   enable toolbar
     */
    public void setDefaults(@LayoutRes int layoutRes, String title, @DrawableRes int backgroundDrawable, @IdRes int backgroundID, boolean isBackEnabled, boolean isToolBarEnabled) {
        setContentView(layoutRes);

        if (backgroundDrawable != 0 && backgroundID != 0)
            Glide.with(this).load(backgroundDrawable).centerCrop().dontAnimate()
                    .into((ImageView) findViewById(backgroundID));

        if (isToolBarEnabled) {
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            if (isBackEnabled) {
                try {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                } catch (NullPointerException ignored) {

                }
            }
            if (title != null && !title.isEmpty()) {
                actionBar.setDisplayShowTitleEnabled(false);

                changeTitleTV(title);
//                toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
            }
        }
        createReference();
    }

    private void changeTitleTV(String title) {
        if (toolbar != null && title != null) {
            TextViewPlus toolBarTitle = (TextViewPlus) toolbar.findViewById(R.id.txt_tool_bar_title);
            toolBarTitle.setTextColor(ResourceUtils.getColor(R.color.tool_bar_text_color));
            toolBarTitle.setText(title);
        }
    }

    public void changeTitleTV(@StringRes int titleRes) {
        String title = ResourceUtils.getString(titleRes);
        changeTitleTV(title);
    }

    public void changeTitle(String title) {
        if (toolbar != null && title != null) {
            toolbar.setTitle(title);
            toolbar.setTitleTextColor(ResourceUtils.getColor(R.color.tool_bar_text_color));
        }
    }

    public void changeTitle(@StringRes int titleRes) {
        String title = ResourceUtils.getString(titleRes);
        changeTitle(title);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T _findViewById(int viewId) {
        return (T) findViewById(viewId);
    }

    public Toolbar getToolBar() {
        return toolbar;
    }

    public abstract void createReference();

    /**
     * @param state it displays if listeners are enable or not, call this method in onResume() & onPause()
     */
    protected abstract void setListeners(boolean state);

    @Override
    protected void onResume() {
        super.onResume();
        setListeners(true);
    }

    @Override
    protected void onPause() {
        setListeners(false);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
