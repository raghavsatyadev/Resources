package raghav.resources.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.support.Constants;
import com.support.base.CoreActivity;

import raghav.resources.R;

public class SplashActivity extends CoreActivity<SplashActivity> {

    private boolean isTimerFinished = false;
    private boolean isStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaults(this, R.layout.activity_splash);
    }

    @Override
    public void createReference() {

        new Handler().postDelayed(() -> {
            isTimerFinished = true;
            startNewActivity();
        }, Constants.Other.SPLASH_COUNTER);
    }

    private void startNewActivity() {
        if (!isStopped && isTimerFinished) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        isStopped = true;
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
        startNewActivity();
    }

    @Override
    protected void setListeners(boolean state) {

    }
}
