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

public class AccountHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<com.tcs.pickupapp.data.rest.response.AccountDetail> accountDetails;
    private Context adapterContext;
    IAccountHistoryAdapter adapter;
    ViewGroup parent;


    public AccountHistoryAdapter(List<com.tcs.pickupapp.data.rest.response.AccountDetail> accountDetailList, IAccountHistoryAdapter adapter) {
        this.accountDetails = accountDetailList;
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.adapterContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_history_row_item, parent, false);
        AccountDetailsViewHolder accountDetailsViewHolder = new AccountDetailsViewHolder(v);
        return accountDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        com.tcs.pickupapp.data.rest.response.AccountDetail accountDetail = accountDetails.get(position);
        if (holder instanceof AccountDetailsViewHolder) {
            AccountDetailsViewHolder deletedSequenceViewHolder = (AccountDetailsViewHolder) holder;
            deletedSequenceViewHolder.txtConsignmentNo.setText(accountDetail.getCNNumber());
            deletedSequenceViewHolder.txtProduct.setText(accountDetail.getProduct());
            deletedSequenceViewHolder.txtService.setText(accountDetail.getServiceName());
            if (position % 2 == 0) {
                deletedSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_dark));
            } else {
                deletedSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_light));
            }
        }
    }

    public void addAll(List<com.tcs.pickupapp.data.rest.response.AccountDetail> accountDetail) {
        int currentListSize = this.accountDetails.size();
        this.accountDetails.clear();
        this.accountDetails.addAll(accountDetail);
        notifyItemRangeInserted(currentListSize, accountDetail.size());
        notifyDataSetChanged();
    }


    public class AccountDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.linearParent)
        protected LinearLayout linearParent;
        @BindView(R.id.txtConsignmentNo)
        public TextView txtConsignmentNo;
        @BindView(R.id.txtProduct)
        public TextView txtProduct;
        @BindView(R.id.txtService)
        public TextView txtService;


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


    public interface IAccountHistoryAdapter {
        void onItemClick(com.tcs.pickupapp.data.rest.response.AccountDetail accountDetail);
    }
}
