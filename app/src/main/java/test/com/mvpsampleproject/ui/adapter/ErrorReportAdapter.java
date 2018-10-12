package test.com.mvpsampleproject.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.ErrorReport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
public class ErrorReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ErrorReport> reports;
    private List<ErrorReport> reportsBackup;
    private Context adapterContext;

    public ErrorReportAdapter(List<ErrorReport> reports){
        this.reports = reports;

        reportsBackup = new ArrayList<>();
        for(ErrorReport report : reports){
            reportsBackup.add(report);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.adapterContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_report_item,parent,false);
        ErrorReportViewHolder errorReportViewHolder = new ErrorReportViewHolder(v);
        return errorReportViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ErrorReport report = reports.get(position);
        if(holder instanceof ErrorReportViewHolder){
            ErrorReportViewHolder errorReportViewHolder = (ErrorReportViewHolder) holder;
            errorReportViewHolder.txtCustomerName.setText(report.getCustomerName());
            errorReportViewHolder.txtCustomerNumber.setText(report.getCustomerNumber());
            errorReportViewHolder.txtConsignmentNumber.setText(report.getCnNumber());
            errorReportViewHolder.txtErrorMessage.setText(report.getErrorMessage());
            if(position % 2 == 0){
                errorReportViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext,R.color.grey_light_report_item_bg_dark));
            }else {
                errorReportViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext,R.color.grey_light_report_item_bg_light));
            }
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public List<ErrorReport> getAllReports(){
        return reports;
    }

    public void addAll(List<ErrorReport> reports){
        this.reports.clear();
        this.reports.addAll(reports);
        notifyDataSetChanged();
    }

    public void clear(){
        reports.clear();
        notifyDataSetChanged();
    }

    public List<ErrorReport> getFilteredReportsByCNNumber(String consignmentNumber){
        if(consignmentNumber == null){
            return reportsBackup;
        }
        if(consignmentNumber.equals("")){
            return reportsBackup;
        }
        List<ErrorReport> fileteredReports = new ArrayList<>();
        for(ErrorReport report : reportsBackup){
            if(report.getCnNumber().startsWith(consignmentNumber)){
                fileteredReports.add(report);
            }
        }
        return fileteredReports;
    }

    public class ErrorReportViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.linearParent)
        protected LinearLayout linearParent;
        @BindView(R.id.cardViewParent)
        protected CardView cardViewParent;
        @BindView(R.id.txtCustomerName)
        protected TextView txtCustomerName;
        @BindView(R.id.txtCustomerNumber)
        protected TextView txtCustomerNumber;
        @BindView(R.id.txtConsignmentNumber)
        protected TextView txtConsignmentNumber;
        @BindView(R.id.txtErrorMessage)
        protected TextView txtErrorMessage;

        public ErrorReportViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
