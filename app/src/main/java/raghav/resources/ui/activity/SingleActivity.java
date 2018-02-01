package raghav.resources.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;

import com.support.base.CoreActivity;
import com.support.retrofit.model.ContactInfoModel;
import com.support.retrofit.network.ApiClient;
import com.support.retrofit.network.ObserverUtil;
import com.support.retrofit.network.SingleCallback;
import com.support.retrofit.network.WebserviceBuilder;
import com.support.utils.AppLog;

import raghav.resources.R;

public class SingleActivity extends CoreActivity<SingleActivity> implements SingleCallback {

    private AppCompatTextView txtNameVal;
    private AppCompatTextView txtEmailVal;
    private AppCompatTextView txtPhoneHomeVal;
    private AppCompatTextView txtPhoneMobileVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaults(this, R.layout.activity_single, "Single", true, true);
    }

    private void callAPI() {
        ObserverUtil
                .subscribeToSingle(ApiClient.getClient().create(WebserviceBuilder.class).getSingleObject()
                        , getCompositeDisposable(), WebserviceBuilder.ApiNames.single, this);
    }

    @Override
    public void createReference() {
        txtNameVal = findViewById(R.id.txt_name_val);
        txtEmailVal = findViewById(R.id.txt_email_val);
        txtPhoneHomeVal = findViewById(R.id.txt_phone_home_val);
        txtPhoneMobileVal = findViewById(R.id.txt_phone_mobile_val);
        callAPI();
    }

    @Override
    protected void setListeners(boolean state) {

    }

    @Override
    public void onSingleSuccess(Object o, WebserviceBuilder.ApiNames apiNames) {
        switch (apiNames) {
            case single:
                ContactInfoModel contactInfoModel = (ContactInfoModel) o;
                if (contactInfoModel != null) {
                    txtNameVal.setText(contactInfoModel.getName());
                    txtEmailVal.setText(contactInfoModel.getEmail());
                    ContactInfoModel.PhoneBean phone = contactInfoModel.getPhone();
                    txtPhoneHomeVal.setText(phone.getHome());
                    txtPhoneMobileVal.setText(phone.getMobile());
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        cancelCalls();
        super.onDestroy();
    }

    @Override
    public void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames) {
        AppLog.log(false, "SingleActivity " + "onFailure: ", throwable);
    }
}
