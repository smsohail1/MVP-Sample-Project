package test.com.mvpsampleproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.home.model.HomeItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shahrukh.malik on 09, April, 2018
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<HomeItem> items;
    private IHomeAdapter iHomeAdapter;

    public HomeAdapter(List<HomeItem> items, IHomeAdapter iHomeAdapter){
        this.items = items;
        this.iHomeAdapter = iHomeAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false);
        HomeViewHolder homeViewHolder = new HomeViewHolder(v);
        return homeViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeItem homeItem = items.get(position);
        if(holder instanceof HomeViewHolder){
            HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
            homeViewHolder.imgHomeItem.setImageResource(homeItem.getImgResId());
            homeViewHolder.txtHomeItem.setText(homeItem.getNameResId());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.imgHomeItem)
        protected ImageView imgHomeItem;
        @BindView(R.id.txtHomeItem)
        protected TextView txtHomeItem;

        public HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iHomeAdapter.onHomeItemClick(items.get(getLayoutPosition()));
        }
    }

    public interface IHomeAdapter{
        void onHomeItemClick(HomeItem homeItem);
    }
}














