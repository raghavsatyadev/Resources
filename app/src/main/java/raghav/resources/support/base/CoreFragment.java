package raghav.resources.support.base;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import raghav.resources.support.retrofit.network.ApiClient;

public abstract class CoreFragment extends Fragment {
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

    @Override
    public void onDestroyView() {
        ApiClient.cancelAll();
        super.onDestroyView();
    }
}
