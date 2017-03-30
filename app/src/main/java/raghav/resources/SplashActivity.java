package raghav.resources;

import android.os.Bundle;

import raghav.resources.support.base.CoreActivity;

public class SplashActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaults(R.layout.activity_splash);
    }

    @Override
    public void createReference() {

    }

    @Override
    protected void setListeners(boolean state) {

    }
}
