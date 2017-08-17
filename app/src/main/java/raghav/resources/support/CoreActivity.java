package raghav.resources.support;

/**
 * Created by raghav on 17/8/17.
 */

public abstract class CoreActivity extends com.support.base.CoreActivity {
    abstract public void createReference();

    @Override
    abstract protected void setListeners(boolean state);
}
