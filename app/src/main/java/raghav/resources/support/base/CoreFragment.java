package raghav.resources.support.base;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import io.reactivex.disposables.CompositeDisposable;

public abstract class CoreFragment extends Fragment {
    private CompositeDisposable compositeDisposable;

    /**
     * this method disables all click through calling setListeners(false)
     */
    public void showProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        setListeners(false);
    }

    /**
     * this method enables all click through calling setListeners(true)
     */
    public void hideProgressBar(ProgressBar progressBar) {
        setListeners(true);
        progressBar.setVisibility(View.GONE);
    }

    public abstract void setListeners(boolean state);

    public CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }
}
