package test.com.mvpsampleproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.Dimension;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by umair.irshad on 4/3/2018.
 */

public class DimensionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Dimension> dimensions;
    private OnClickListener listener;
    public DecimalFormat decimalFormat;

    public DimensionAdapter(List<Dimension> dimensions, OnClickListener listener){
        this.dimensions = dimensions;
        this.listener = listener;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dimension_list_item_1,parent,false);
        DimensionViewHolder holder = new DimensionViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Dimension dimension = this.dimensions.get(position);
        ((DimensionViewHolder)holder).bind(dimension,listener);
        if(holder instanceof DimensionViewHolder){
            DimensionViewHolder dimensionViewHolder = (DimensionViewHolder) holder;


            if(dimension.getDimensionWeight() != null){
                dimensionViewHolder.textDimensionWeight.setText(dimension.getDimensionWeight());
            }

            if(dimension.getVolumetricWeight() != null){
                dimensionViewHolder.textVolumetricWeight.setText(decimalFormat.format(Double.parseDouble(dimension.getVolumetricWeight())));
            }

            if(dimension.getTotalVolumetricWeight() != null){
                dimensionViewHolder.textTotalVolumetricWeight.setText(decimalFormat.format(Double.parseDouble(dimension.getTotalVolumetricWeight())));
            }

            if(dimension.getTotalDimensionWeight() != null){
                dimensionViewHolder.textTotalDimensionWeight.setText(decimalFormat.format(Double.parseDouble(dimension.getTotalDimensionWeight())));
            }

            if(dimension.getActualWeight() != null){
                dimensionViewHolder.txtActualWeight.setText(decimalFormat.format(Double.parseDouble(dimension.getActualWeight())));

            }

            if(dimension.getPieces() != null){
                dimensionViewHolder.textPieces.setText(dimension.getPieces());
            }

            if(dimension.getLength() != null){
                dimensionViewHolder.txtLength.setText(dimension.getLength());
            }

            if(dimension.getWidth() != null){
                dimensionViewHolder.txtWidth.setText(dimension.getWidth());
            }

            if(dimension.getHeight() != null){
                dimensionViewHolder.txtHeight.setText(dimension.getHeight());
            }

        }
    }

    public void addAll(List<Dimension> list){
        dimensions.clear();
        dimensions.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Dimension dimension){
        dimensions.add(dimension);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dimensions.size();
    }

    public class DimensionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textDimensionWeight)
        protected TextView textDimensionWeight;
        @BindView(R.id.textVolumetricWeight)
        protected TextView textVolumetricWeight;
        @BindView(R.id.textTotalVolumetricWeight)
        protected TextView textTotalVolumetricWeight;
        @BindView(R.id.textTotalDimensionWeight)
        protected TextView textTotalDimensionWeight;
        @BindView(R.id.txtActualWeight)
        protected TextView txtActualWeight;
        @BindView(R.id.textPieces)
        protected TextView textPieces;
        @BindView(R.id.txtLength)
        protected TextView txtLength;
        @BindView(R.id.txtWidth)
        protected TextView txtWidth;
        @BindView(R.id.txtHeight)
        protected TextView txtHeight;
        @BindView(R.id.cancel)
        protected ImageView cancel;

        public DimensionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(final Dimension item, final OnClickListener listener) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnClickListener{
       void onItemClick(Dimension dimension);
    }
}
