package raghav.resources.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.support.Constants;

import raghav.resources.R;
import raghav.resources.support.CoreActivity;

public class SplashActivity extends CoreActivity {

    private boolean isTimerFinished = false;
    private boolean isStopped = false;
    private SplashActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setDefaults(activity, R.layout.activity_splash);
    }

    @Override
    public void createReference() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTimerFinished = true;
                startNewActivity();
            }
        }, Constants.Other.SPLASH_COUNTER);
    }

    private void startNewActivity() {
        if (!isStopped && isTimerFinished) {
            startActivity(new Intent(activity, MainActivity.class));
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
