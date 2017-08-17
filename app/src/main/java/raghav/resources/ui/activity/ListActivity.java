package raghav.resources.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.support.base.CoreActivity;
import com.support.retrofit.model.ContactInfoModel;
import com.support.retrofit.network.ApiClient;
import com.support.retrofit.network.ListCallback;
import com.support.retrofit.network.ObserverUtil;
import com.support.retrofit.network.WebserviceBuilder;
import com.support.utils.AppLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import raghav.resources.R;
import raghav.resources.ui.adapter.ContactInfoAdapter;

public class ListActivity extends CoreActivity implements ListCallback {

    private ListActivity activity;
    private ContactInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setDefaults(activity, R.layout.activity_list, "", true, true);
    }

    @Override
    public void createReference() {

        RecyclerView listContact = (RecyclerView) findViewById(R.id.list_contact);
        listContact.setHasFixedSize(false);
        listContact.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new ContactInfoAdapter(activity, new ArrayList<ContactInfoModel>());
        listContact.setAdapter(adapter);
        listContact.addItemDecoration(new HorizontalDividerItemDecoration.Builder(activity)
                .colorProvider(adapter)
                .build());

        callAPI();
    }

    @Override
    protected void setListeners(boolean state) {

    }

    private void callAPI() {
        Observable<List<ContactInfoModel>> listObject = ApiClient.getClient().create(WebserviceBuilder.class).getListObject();
        ObserverUtil
                .subscribeToList(listObject
                        , getCompositeDisposable(), WebserviceBuilder.ApiNames.list, this);
    }

    @Override
    public void onListNext(Object object, WebserviceBuilder.ApiNames apiNames) {
        switch (apiNames) {
            case list:
                ContactInfoModel contactInfoModel = (ContactInfoModel) object;
                adapter.addItem(contactInfoModel);
                break;
        }
    }

    @Override
    public void onFailure(Throwable throwable, WebserviceBuilder.ApiNames apiNames) {
        AppLog.log(false, "ListActivity " + "onFailure: ", throwable);
    }

    @Override
    protected void onDestroy() {
        cancelCalls();
        super.onDestroy();
    }

    @Override
    public void onListComplete(WebserviceBuilder.ApiNames apiNames) {

    }
}
