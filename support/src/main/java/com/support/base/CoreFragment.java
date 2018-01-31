package com.support.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.support.utils.AppLog;

import io.reactivex.disposables.CompositeDisposable;

public abstract class CoreFragment<T extends CoreFragment> extends Fragment {
    private CompositeDisposable compositeDisposable;
    private CoreActivity coreActivity;

    public CoreActivity getCoreActivity() {
        return coreActivity;
    }

    public void showProgressBar() {
        coreActivity.showProgressBar();
    }

    public void disableScreen(boolean disable) {
        coreActivity.disableScreen(disable);
    }

    public void hideProgressBar() {
        coreActivity.hideProgressBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutRes(), container, false);
        setCoreActivity();
        setBackButton();
        changeTitle(setTitle());
        setHasOptionsMenu(true);
        createReference(inflate);
        setListeners(true);
        return inflate;
    }

    private void setCoreActivity() {
        FragmentActivity activity = getCoreFragment().getActivity();
        if (activity instanceof CoreActivity) {
            coreActivity = (CoreActivity) activity;
        }
    }

    public void setBackButton() {
        coreActivity.setBackButton(isBackEnabled());
    }

    public void changeTitle(String s) {
        coreActivity.changeTitle(s);
    }

    protected abstract void createReference(View view);

    protected abstract String setTitle();

    protected abstract boolean isBackEnabled();

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract T getCoreFragment();

    protected abstract void setListeners(boolean state);

    @Override
    public void onDestroyView() {
        setListeners(false);
        super.onDestroyView();
    }

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
            AppLog.log(false, "CoreFragment: " + "cancelCalls: ");
        }
    }
}
