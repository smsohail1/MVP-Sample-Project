package test.com.mvpsampleproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcs.pickupapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<com.tcs.pickupapp.data.room.model.Booking> bookingLists;
    IBookingList adapter;
    Context context;

    public BookingListAdapter(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists, IBookingList adapter) {
        this.bookingLists = bookingLists;
        this.adapter = adapter;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_list_item, parent, false);
        BookingListAdapter.BookingListViewHolder generateSequenceViewHolder = new BookingListAdapter.BookingListViewHolder(v);
        return generateSequenceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        com.tcs.pickupapp.data.room.model.Booking bookingList = bookingLists.get(position);
        if (holder instanceof BookingListAdapter.BookingListViewHolder) {
            BookingListAdapter.BookingListViewHolder bookingListViewHolder = (BookingListAdapter.BookingListViewHolder) holder;
            bookingListViewHolder.textViewAccountNumber.setText(bookingList.getCustomerNumber());
            bookingListViewHolder.textViewConsignment.setText(bookingList.getCnNumber());
            bookingListViewHolder.textViewWeight.setText(bookingList.getWeight());
            bookingListViewHolder.textViewPieces.setText(bookingList.getPieces());
            bookingListViewHolder.textViewTransmissionStatus.setText(bookingList.getTransmitStatus());
            Glide.with(context)
                    .load(bookingList.getImage())
                    .asBitmap()
                    .into(bookingListViewHolder.imageViewConsignmentImage);
        }
    }

    public void addAll(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
        int currentListSize = this.bookingLists.size();
        this.bookingLists.addAll(bookingList);
        notifyItemRangeInserted(currentListSize, bookingList.size());
    }

    public class BookingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textViewAccountNumber)
        public TextView textViewAccountNumber;
        @BindView(R.id.textViewConsignment)
        public TextView textViewConsignment;
        @BindView(R.id.textViewWeight)
        public TextView textViewWeight;
        @BindView(R.id.textViewPieces)
        public TextView textViewPieces;
        @BindView(R.id.textViewTransmissionStatus)
        public TextView textViewTransmissionStatus;
        @BindView(R.id.imageViewConsignmentImage)
        public ImageView imageViewConsignmentImage;

        public BookingListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapter.onItemClick(bookingLists.get(getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return bookingLists.size();
    }

    public interface IBookingList {
        void onItemClick(com.tcs.pickupapp.data.room.model.Booking bookingList);
    }
}
