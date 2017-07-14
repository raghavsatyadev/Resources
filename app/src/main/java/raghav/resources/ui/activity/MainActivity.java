package raghav.resources.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import raghav.resources.R;
import raghav.resources.support.base.CoreActivity;

public class MainActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaults(R.layout.activity_main, "", false, true);
    }

    public void openListActivity(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void openSingleActivity(View view) {
        startActivity(new Intent(this, SingleActivity.class));
    }

    @Override
    public void createReference() {

    }

    @Override
    protected void setListeners(boolean state) {

    }
}
