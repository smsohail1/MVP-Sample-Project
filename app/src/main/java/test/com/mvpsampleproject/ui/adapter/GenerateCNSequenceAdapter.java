package test.com.mvpsampleproject.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.Booking;
import com.tcs.pickupapp.data.room.model.DeletedCNSequence;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.ui.generatecnsequence.GenerateCNSequenceMVP;
import com.tcs.pickupapp.ui.generatecnsequence.GenerateCNSequenceModel;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdul.ahad on 02-Apr-18.
 */

public class GenerateCNSequenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<GenerateSequence> generateSequences;
    IGenerateCNSequenceAdapter adapter;
    ViewGroup parent;
    private GenerateCNSequenceMVP.Model model;
    private GenerateCNSequenceMVP.View view;
    private Context adapterContext;

    protected Utils utils;
    private Dialog dialog;

    public GenerateCNSequenceAdapter(List<GenerateSequence> generateSequences, IGenerateCNSequenceAdapter adapter,
                                     GenerateCNSequenceMVP.Model model, GenerateCNSequenceMVP.View view) {
        this.generateSequences = generateSequences;
        this.adapter = adapter;
        this.model = model;
        this.view = view;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        this.adapterContext = parent.getContext();
        utils = new Utils(parent.getContext());
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cn_sequence_row_item, parent, false);
        GenerateSequenceViewHolder generateSequenceViewHolder = new GenerateSequenceViewHolder(parent.getContext(), v);
        return generateSequenceViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final GenerateSequence generateSequence = generateSequences.get(position);
        if (holder instanceof GenerateSequenceViewHolder) {
            final GenerateSequenceViewHolder generateSequenceViewHolder = (GenerateSequenceViewHolder) holder;
            generateSequenceViewHolder.cnFrom.setText(generateSequence.getCN_from());
            generateSequenceViewHolder.cnTo.setText(generateSequence.getCN_to());
            generateSequenceViewHolder.deleteCN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAlertDialog("Delete " + generateSequence.getCN_from() + " - " + generateSequence.getCN_to()
                            , "Do you want to delete this sequence?", generateSequence);

                }
            });
            if (position % 2 == 0) {
                generateSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_dark));
            } else {
                generateSequenceViewHolder.linearParent.setBackgroundColor(ContextCompat.getColor(adapterContext, R.color.grey_light_report_item_bg_light));
            }
        }
    }

    public void addAll(List<GenerateSequence> generateSequences) {
        int currentListSize = this.generateSequences.size();
        this.generateSequences.addAll(generateSequences);
        notifyItemRangeInserted(currentListSize, generateSequences.size());
    }

    public void remove(GenerateSequence generateSequence) {
        generateSequences.remove(generateSequence);
        notifyDataSetChanged();
    }

    public class GenerateSequenceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.linearParent)
        protected LinearLayout linearParent;
        @BindView(R.id.cnFrom)
        public TextView cnFrom;
        @BindView(R.id.cnTo)
        public TextView cnTo;
        @BindView(R.id.deleteCN)
        public LinearLayout deleteCN;
        public Context context;


        public GenerateSequenceViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //itemView.setOnClickListener(this);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            //adapter.onItemClick(generateSequences.get(getLayoutPosition()));
        }
    }


    @Override
    public int getItemCount() {
        return generateSequences.size();
    }

    public interface IGenerateCNSequenceAdapter {
        void onItemClick(GenerateSequence generateSequence);
    }


    private void openAlertDialog(String title, String message, final GenerateSequence generateSequence) {

        dialog = new Dialog(adapterContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_cancel_dialog);

        Button submit = dialog.findViewById(R.id.submit);
        Button cancel = dialog.findViewById(R.id.cancel);
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        txtMessage.setText("" + message);
        txtTitle.setText("" + title);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.fetchUsedConsignmentRecord(generateSequence.cn_from,
                        generateSequence.cn_to,
                        new GenerateCNSequenceModel.IGetUsedCN() {
                            @Override
                            public void onUsedCnReceived(List<Booking> bookings) {
                                if (bookings != null && bookings.size() == 0) {
                                    DeletedCNSequence deletedCNSequence = new DeletedCNSequence(generateSequence.cn_from,
                                            generateSequence.cn_to,
                                            utils.getCurrentDateTime(AppConstants.DATE_TIME_FORMAT_ONE));
                                    model.deleteCNSequence(deletedCNSequence, generateSequence, new GenerateCNSequenceModel.IDeleteCN() {
                                        @Override
                                        public void onDeleteCNSuccess(boolean status) {
                                            if (status) {
                                                remove(generateSequence);
                                            }
                                        }

                                        @Override
                                        public void onErrorReceived(Exception ex) {
                                            view.showToastShortTime(ex.getMessage());
                                        }
                                    });
                                    //  return;
                                } else {
                                    view.showToastShortTime("Consignment no can't delete\nReason:Consignment no already used");
                                    utils.playErrorToneAndVibrate(adapterContext);
                                }
                            }

                            @Override
                            public void onErrorReceived(Exception ex) {
                                view.showToastShortTime(ex.getMessage());
                            }
                        });
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

        /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        model.fetchUsedConsignmentRecord(generateSequence.cn_from,
                                generateSequence.cn_to,
                                new GenerateCNSequenceModel.IGetUsedCN() {
                                    @Override
                                    public void onUsedCnReceived(List<Booking> bookings) {
                                        if (bookings != null && bookings.size() == 0) {
                                            DeletedCNSequence deletedCNSequence = new DeletedCNSequence(generateSequence.cn_from,
                                                    generateSequence.cn_to,
                                                    utils.getCurrentDateTime(AppConstants.DATE_TIME_FORMAT_ONE));
                                            model.deleteCNSequence(deletedCNSequence, generateSequence);
                                            remove(generateSequence);
                                            return;
                                        } else {
                                            view.showToastShortTime("Cn# can't delete\nReason:Cn# already used");
                                        }
                                    }

                                    @Override
                                    public void onErrorReceived(Exception ex) {
                                        view.showToastShortTime(ex.getMessage());
                                    }
                                });


//                        DeletedCNSequence deletedCNSequence = new DeletedCNSequence(generateSequence.cn_from,
//                                generateSequence.cn_to,
//                                utils.getCurrentDateTime(AppConstants.DATE_TIME_FORMAT_ONE));
//                        model.deleteCNSequence(deletedCNSequence, generateSequence);
//                        remove(generateSequence);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();*/
    }


}