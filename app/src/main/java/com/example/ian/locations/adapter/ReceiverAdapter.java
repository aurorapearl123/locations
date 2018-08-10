package com.example.ian.locations.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ian.locations.HomePageActivity;
import com.example.ian.locations.R;
import com.example.ian.locations.model.CheckerReceiver;
import com.example.ian.locations.model.Receiver;

import java.io.DataOutputStream;
import java.util.List;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.MyViewHolder> {

    private Context mContext;
    private List<Receiver> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView phone, status;
        public ImageView thumbnail, trash, send_test;

        public MyViewHolder(View view) {
            super(view);
            phone = (TextView) view.findViewById(R.id.title);
            status = (TextView) view.findViewById(R.id.count);
           // thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            trash = (ImageView) view.findViewById(R.id.id_trash);
            send_test = (ImageView) view.findViewById(R.id.id_test);
        }
    }


    public ReceiverAdapter(Context mContext, List<Receiver> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Receiver receiver = albumList.get(position);
        holder.phone.setText(receiver.getPhone()+"");
        final String send_phone = receiver.getPhone();
        if(receiver.getStatus() == null) {
            holder.status.setText("Message Not Delivered");
        }
        else
        {
            holder.status.setText("Message Delivered ");
        }

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alertDelete("32132");
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setIcon(R.drawable.warning)
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("Delete", null) // null to override the onClick
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btnPositive.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                //showToast("Dialog not dismissed!");
                                //remove from database
                                //REMOVE CHECKER
                                //get position
                                int id = receiver.getId();
                                if(id == 0) {
                                    Log.wtf("MESSAGE-DELETE","delete last inserted");
                                    receiver.delete(receiver.getLastInserdId());
                                }
                                else{
                                    receiver.delete(id);
                                }

                                int position = receiver.getCursorPosition(id+"");
                                CheckerReceiver checkerReceiver = new CheckerReceiver();
                                checkerReceiver.removeCheckerByReceiverId(position);
                                //receiver.delete(receiver.getId());
                                notifyDataSetChanged();
                                albumList.remove(receiver);
                                alertDialog.dismiss();

                            }
                        });


                        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        btnNegative.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                // dismiss once everything is ok
                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                // don't forget to show it
                alertDialog.show();
            }
        });

        //manual test
        holder.send_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setIcon(R.drawable.warning)
                        .setMessage("Do you want to send?")
                        .setPositiveButton("Send", null) // null to override the onClick
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btnPositive.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                if(mContext instanceof HomePageActivity){
                                    ((HomePageActivity)mContext).prepareSms(send_phone);
                                }

                               alertDialog.dismiss();

                            }
                        });


                        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        btnNegative.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                // dismiss once everything is ok
                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                // don't forget to show it
                alertDialog.show();
            }
        });


    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
