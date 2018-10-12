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
import com.tcs.pickupapp.data.room.model.DeletedCNSequence;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdul.ahad on 02-Apr-18.
 */

public class DeletedCNSequenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DeletedCNSequence> deletedCNSequences;
    IDeletedCNSequenceAdapter adapter;
    private Context adapterContext;

    public DeletedCNSequenceAdapter(List<DeletedCNSequence> deletedCNSequences, IDeletedCNSequenceAdapter adapter) {
        this.deletedCNSequences = deletedCNSequences;
        this.adapter = adapter;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.adapterContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cn_sequence_delete_row_item, parent, false);
        DeletedSequenceViewHolder deletedSequenceViewHolder = new DeletedSequenceViewHolder(v);
        return deletedSequenceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DeletedCNSequence deleteSequence = deletedCNSequences.get(position);
        if(holder instanceof DeletedSequenceViewHolder){
            DeletedSequenceViewHolder deletedSequenceViewHolder = (DeletedSequenceViewHolder) holder;
            deletedSequenceViewHolder.cnFrom.setText(deleteSequence.getCN_from());
            deletedSequenceViewHolder.cnTo.setText(deleteSequence.getCN_to());
            deletedSequenceViewHolder.deleteText.setText(deleteSequence.getDeleted_Date());
//            deletedSequenceViewHolder.productLayout.setVisibility(View.INVISIBLE);
            if(position % 2 == 0){
                deletedSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext,R.color.grey_light_report_item_bg_dark));
            }else {
                deletedSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext,R.color.grey_light_report_item_bg_light));
            }
        }
    }

    public void addAll(List<DeletedCNSequence> deleteSequence){
        int currentListSize = this.deletedCNSequences.size();
        this.deletedCNSequences.addAll(deleteSequence);
        notifyItemRangeInserted(currentListSize, deleteSequence.size());
    }

    public class DeletedSequenceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.linearParent)
        protected LinearLayout linearParent;
        @BindView(R.id.cnFrom)
        public TextView cnFrom;
        @BindView(R.id.cnTo)
        public TextView cnTo;
        @BindView(R.id.deleteText)
        public TextView deleteText;
//        @BindView(R.id.productLayout)
//        public LinearLayout productLayout;


        public DeletedSequenceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapter.onItemClick(deletedCNSequences.get(getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return deletedCNSequences.size();
    }

    public interface IDeletedCNSequenceAdapter {
        void onItemClick(DeletedCNSequence generateSequence);
    }
}
