package test.com.mvpsampleproject.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.GroupedReport;
import com.tcs.pickupapp.ui.statistics.StatisticsFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by umair.irshad on 4/6/2018.
 */

public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final StatisticsFragment.OnItemClickListener listener;
    private List<GroupedReport> statisticsReport;
    private Context context;
    private int selected;
    private int selectedPosition,position;

    public StatisticsAdapter(List<GroupedReport> statisticsReport, StatisticsFragment.OnItemClickListener listener) {
        this.statisticsReport = statisticsReport;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_list_item, parent, false);

        return new CustomerAckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        final GroupedReport report = this.statisticsReport.get(position);
        if (holder instanceof CustomerAckViewHolder) {

            if(selectedPosition==position)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.grey_light_report_item_bg_dark));
            else
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));

            if (report.getCustomerName() != null) {
                ((CustomerAckViewHolder) holder).txtCustomerName.setText((position+1)+". "+report.getCustomerName());
            }

            if (report.getCustomerNumber() != null) {
                ((CustomerAckViewHolder) holder).txtCustomerNumber.setText(report.getCustomerNumber());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition=position;
                    notifyDataSetChanged();
                    listener.onItemClick(report);
//                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.grey_light_report_item_bg_dark));


                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return statisticsReport.size();
    }

    public class CustomerAckViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtCustomerNumber)
        protected TextView txtCustomerNumber;

        @BindView(R.id.txtCustomerName)
        protected TextView txtCustomerName;

        private View view;


        public CustomerAckViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }

    private void resetColor() {

    }

}
