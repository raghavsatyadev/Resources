package raghav.resources.ui.adapter;

import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.support.retrofit.model.ContactInfoModel;
import com.support.utils.GenRecyclerAdapter;
import com.support.utils.ResourceUtils;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;

import java.util.ArrayList;

import raghav.resources.R;


public class ContactInfoAdapter extends GenRecyclerAdapter<ContactInfoAdapter.DataObjectHolder, ContactInfoModel> implements FlexibleDividerDecoration.ColorProvider {
    @ColorInt
    private int listDivider;

    public ContactInfoAdapter(ArrayList<ContactInfoModel> contactInfoModels) {
        super(contactInfoModels);
        listDivider = ResourceUtils.getColor(R.color.colorAccent);
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_activity_list, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        ContactInfoModel contactInfoModel = getItem(position);
        if (contactInfoModel != null) {
            holder.txtNameVal.setText(contactInfoModel.getName());
            holder.txtEmailVal.setText(contactInfoModel.getEmail());
            ContactInfoModel.PhoneBean phone = contactInfoModel.getPhone();
            holder.txtPhoneHomeVal.setText(phone.getHome());
            holder.txtPhoneMobileVal.setText(phone.getMobile());
        }
    }

    @Override
    public int dividerColor(int position, RecyclerView parent) {
        return listDivider;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatTextView txtNameVal;
        private AppCompatTextView txtEmailVal;
        private AppCompatTextView txtPhoneHomeVal;
        private AppCompatTextView txtPhoneMobileVal;

        DataObjectHolder(View itemView) {
            super(itemView);
            txtNameVal = itemView.findViewById(R.id.txt_name_val);
            txtEmailVal = itemView.findViewById(R.id.txt_email_val);
            txtPhoneHomeVal = itemView.findViewById(R.id.txt_phone_home_val);
            txtPhoneMobileVal = itemView.findViewById(R.id.txt_phone_mobile_val);
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}
