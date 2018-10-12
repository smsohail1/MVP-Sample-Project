package test.com.mvpsampleproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by umair.irshad on 4/18/2018.
 */

public class AutocompleteCustomArrayAdapter extends ArrayAdapter<CustomerInformation> {

    private List<CustomerInformation> customerInformationList, dummyList;
    private List<CustomerInformation> backupCusInfo = new ArrayList<>();
    private int resource;

    public AutocompleteCustomArrayAdapter(Context context, int resource, List<CustomerInformation> customerInformationList) {
        super(context, resource, customerInformationList);
        this.resource = resource;
        this.customerInformationList = new ArrayList<>(customerInformationList);
        this.backupCusInfo = new ArrayList<>(customerInformationList);
        dummyList = customerInformationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutoCompleteViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
            viewHolder = new AutoCompleteViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AutoCompleteViewHolder) convertView.getTag();
        }

        viewHolder.textViewItem.setText(getItem(position).getCustomerNumber());

        return convertView;
    }

    public void addAll(List<CustomerInformation> list) {
        customerInformationList.clear();
        customerInformationList.addAll(list);
        notifyDataSetChanged();
    }

    public List<CustomerInformation> filter(CharSequence c) {
        int length = c.length();
        customerInformationList.clear();
        if (length != 0) {
            for (int i = 0; i < backupCusInfo.size(); i++) {
                if (backupCusInfo.get(i).getCustomerNumber().toLowerCase().contains(c.toString().toLowerCase())) {
                    customerInformationList.add(backupCusInfo.get(i));
                }
            }
        } else {
            customerInformationList.addAll(backupCusInfo);
        }
        return customerInformationList;
    }

    /*@Override
    public synchronized Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence c) {
            Log.d("filter-","performFiltering");

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterFromList(c);
            filterResults.count = filterFromList(c).size();
            return filterResults;
                *//*if (c != null) {
                    int length = c.length();
                    customerInformationList.clear();
                    FilterResults filterResults = null;
                    if (length != 0) {
                        for (int i = 0; i < backupCusInfo.size(); i++) {
                            if (backupCusInfo.get(i).getCustomerNumber().toLowerCase().contains(c.toString().toLowerCase())) {
                                customerInformationList.add(backupCusInfo.get(i));
                            }
                        }
                        filterResults = new FilterResults();
                        filterResults.values = customerInformationList;
                        return filterResults;
                    } else{
                        return new FilterResults();
                    }
                } else {
                    return new FilterResults();
                }*//*
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d("filter-","publishResults");
            try {
                List<CustomerInformation> c = (ArrayList<CustomerInformation>) results.values;
                if (c != null && c.size() > 0) {
                    for (CustomerInformation cust : c) {
                        add(cust);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };*/

    private List<CustomerInformation> filterFromList(CharSequence c) {
        if (c != null) {
            int length = c.length();
            customerInformationList.clear();
            if (length != 0) {
                for (int i = 0; i < backupCusInfo.size(); i++) {
                    if (backupCusInfo.get(i).getCustomerNumber().toLowerCase().contains(c.toString().toLowerCase())) {
                        customerInformationList.add(backupCusInfo.get(i));
                    }
                }
            } else{
                customerInformationList.addAll(backupCusInfo);
            }
        }
        return customerInformationList;
    }


    /*@Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_listitem, parent, false);

        return new AutoCompleteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomerInformation customerInformation = this.customerInformationList.get(position);
        if(holder instanceof AutoCompleteViewHolder){
            if(customerInformation.getCustomerNumber() != null){
                ((AutoCompleteViewHolder) holder).textViewItem.setText(customerInformation.getCustomerNumber());
            }
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    */

    public class AutoCompleteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewItem)
        protected TextView textViewItem;

        public AutoCompleteViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}