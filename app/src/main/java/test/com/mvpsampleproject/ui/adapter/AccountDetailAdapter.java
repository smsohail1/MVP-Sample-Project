package test.com.mvpsampleproject.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammad.sohail on 5/9/2018.
 */

public class AccountDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<com.tcs.pickupapp.data.room.model.Booking> accountDetails;
    private Context adapterContext;
    IAccountDetailAdapter adapter;
    ViewGroup parent;


    public AccountDetailAdapter(List<com.tcs.pickupapp.data.room.model.Booking> accountDetailList, IAccountDetailAdapter adapter) {
        this.accountDetails = accountDetailList;
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.adapterContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_detail_row_item, parent, false);
        AccountDetailsViewHolder accountDetailsViewHolder = new AccountDetailsViewHolder(v);
        return accountDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        com.tcs.pickupapp.data.room.model.Booking accountDetail = accountDetails.get(position);
        if (holder instanceof AccountDetailsViewHolder) {
            AccountDetailsViewHolder accountDetailsViewHolder = (AccountDetailsViewHolder) holder;
            accountDetailsViewHolder.txtConsignmentNo.setText(accountDetail.getCnNumber());

            if (position % 2 == 0) {
                accountDetailsViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_dark));
            } else {
                accountDetailsViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_light));
            }
        }
    }

    public void addAll(List<com.tcs.pickupapp.data.room.model.Booking> accountDetail) {
        int currentListSize = this.accountDetails.size();
        this.accountDetails.addAll(accountDetail);
        notifyItemRangeInserted(currentListSize, accountDetail.size());
        notifyDataSetChanged();
    }

    public void addBulkCN(List<com.tcs.pickupapp.data.room.model.Booking> accountDetail) {
        // int currentListSize = this.accountDetails.size();
        this.accountDetails.addAll(accountDetail);
        // notifyItemRangeInserted(currentListSize, accountDetail.size());
        notifyDataSetChanged();
    }

    public void addSingleBooking(com.tcs.pickupapp.data.room.model.Booking accountDetail) {
        boolean isFound = false;
        for(com.tcs.pickupapp.data.room.model.Booking booking : accountDetails){
            if(booking.getCnNumber().equals(accountDetail.getCnNumber())){
                isFound = true;
                break;
            }
        }
        if(!isFound) {
            this.accountDetails.add(0, accountDetail);
            notifyDataSetChanged();
            //notifyItemInserted(this.accountDetails.size());
        }
    }

    public void removeAll() {
        this.accountDetails.clear();
        notifyDataSetChanged();
    }

    public class AccountDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.linearParent)
        protected LinearLayout linearParent;
        @BindView(R.id.txtConsignmentNo)
        public TextView txtConsignmentNo;


        public AccountDetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapter.onItemClick(accountDetails.get(getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return accountDetails.size();
    }


    public interface IAccountDetailAdapter {
        void onItemClick(com.tcs.pickupapp.data.room.model.Booking booking);
    }
}