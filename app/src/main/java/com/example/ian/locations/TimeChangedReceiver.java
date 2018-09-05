package com.example.ian.locations;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.ian.locations.adapter.DBAdapter;
import com.example.ian.locations.model.CheckerReceiver;
import com.example.ian.locations.model.Receiver;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TimeChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("CHANGE-TIME", "YOU CHANGE THE TIME");

        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.test();

        String message = "HZHTXv5hBZAtEyWF9pdx";

       Receiver receiver = new Receiver();

       List<Receiver> list = receiver.getAllReceivers();

       if(list.size() != 0) {
           //if(list.size() <= 5 ) {
                int counter = 1;
               for (int i = 0; i< list.size(); i++){
                   counter++;
                   if(counter <= 6) {
                       Log.wtf("LISTS-NUMBERS",list.get(i).getPhone()+" sending");
                       sendSms(list.get(i).getPhone(), message, context);

                   }
               }
           //}

       }



//        String[] test_data = new String[]{
//                "09177776718",
//                "09173200619",
//                "09088809507",
//                "09128510821",
//                "09223007096"
//        };
//
//        for (int i = 0; i< test_data.length; i++){
//            //sendSms(String phonenumber,String message, Context context)
//            Log.wtf("THE-NUMBERS",test_data[i]+" sending");
//            //sendSms(test_data[i], message, context);
//        }

//        sendSms("09177776718", message, context);
//        sendSms("09173200619", message, context);
//        sendSms("09088809507", message, context);
//        sendSms("09128510821", message, context);
//        sendSms("09223007096", message, context);
        //getChecker(context);
    }

    public void getChecker(Context context)
    {



        //Log.wtf("TEST-1", "calling function");
        String message = "HZHTXv5hBZAtEyWF9pdx";
        Receiver receiver = new Receiver();
        List<Receiver> receiverList = receiver.getAllReceiverByUserId(1+"");

        CheckerReceiver checkerReceiver = new CheckerReceiver();
        CheckerReceiver checkerReceiver1 = (CheckerReceiver) checkerReceiver.getAllRecord();


        //insert checker
        if (checkerReceiver1 == null) {
            if (receiverList.size() != 0) {
                //CHECKER EMPTY
                //Log.wtf("cursor_position", receiverList.get(0).getId()+"");
                //SAVE CURSOR
                int cursor_position = receiver.getCursorPosition(receiverList.get(0).getId() + "");
                // Log.wtf("CURSOR-POSITION", cursor_position + "");
                CheckerReceiver checkerReceiver_add = new CheckerReceiver();
                checkerReceiver_add.setCursor_position(cursor_position);
                long id = checkerReceiver_add.add();
                if (id == 0) {
                    Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
                } else {
                    //Log.wtf("INSERTED", id + "");
                    String phone = receiver.getPhoneByPosition(cursor_position);
                    //SEND SMS
                    sendSms(phone, message, context);
                    Log.wtf("SEND-PHONE-NULL", phone+" sending");

                }
            }
            //Log.wtf("NOT-FOUND", "INSERT ME");
        } else {
            //get checkers
            // Log.wtf("FOUND-CHECKERS", "GET RECEIVER IS");
            // GET LAST RECORD FOR CURSOR POSITION IN CHECKER RECEIVER TABLE
            CheckerReceiver checker = new CheckerReceiver();
            int position = Integer.parseInt(checker.getLastRecord());
            //Log.wtf("THIS-POSITION", position + "");
            //SEND SMS LOGIC
            //INSERT CHECKER AGAIN FOR NEXT POSITION
            //CHECK IF THE POSITION IS LAST THEN INSERT FIRST POSITION
            int increment_position = position;
            increment_position++;
            //Log.wtf("INCREMENT", increment_position+"");
            //Log.wtf("FINAL-POSITION", position+"");
            Receiver receiver1 = new Receiver();
            //Log.wtf("RECEIVER-COUNT", receiver1.getCursorCount()+"");

            if(increment_position < receiver1.getCursorCount()) {
                // Log.wtf("dakora", increment_position+"");
                //IF INCREMENT POSITION IS GREATER THAN POSITION GET THE FIRST POSITION THEN INSERT FIRST POSITION
                //Log.wtf("insert", "heere");
                //GET PHONE RECEIVER BASE ON POSITION

                CheckerReceiver checkerReceiver_add = new CheckerReceiver();
                checkerReceiver_add.setCursor_position(increment_position);
                long id = checkerReceiver_add.add();

                if (id == 0) {
                    Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
                } else {

                    //SEND SMS
                    Receiver receiver2 = new Receiver();
                    String phone = receiver2.getPhoneByPosition(increment_position);
                    Log.wtf("SEND-PHONE-dakora", phone + " sending");

                    Log.wtf("MY-POSITION", increment_position + "");
                    sendSms(phone, message, context);
                    //INSERT POSITION CHECKERS

                    //increment_position = 0;
                }

            }
            else {
                //Log.wtf("INSERT-ZERO", "zero-inserted");
                insertPositionReceiverChecker(0);
                Receiver receiver2 = new Receiver();
                String phone = receiver2.getPhoneByPosition(0);
                increment_position--;
                Log.wtf("SEND-PHONE-ZERO", phone+" sending");
                Log.wtf("MY-POSITION", increment_position+"");
                //SEND SMS
                sendSms(phone, message, context);

            }


        }
    }

    public void sendSms(String phonenumber,String message, Context context)
    {

        SmsManager manager = SmsManager.getDefault();

        PendingIntent piSend = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);

        int length = message.length();

        if(length > 50)
        {
            ArrayList<String> messagelist = manager.divideMessage(message);

            manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        }
        else
        {
            manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
        }
    }

    public void insertPositionReceiverChecker(int position)
    {
        //int cursor_position = receiver.getCursorPosition(receiverList.get(0).getId()+"");
        //Log.wtf("CURSOR-POSITION", cursor_position+"");
        CheckerReceiver checkerReceiver_add = new CheckerReceiver();
        checkerReceiver_add.setCursor_position(position);
        long id = checkerReceiver_add.add();
        if(id == 0)
        {
            Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
        }
        else
        {
            Log.wtf("INSERTED", id+"");
        }
    }

    public String getSessionId(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("user_id", null);
        //Toast.makeText(getApplicationContext(), "USER ID"+restoredText, Toast.LENGTH_SHORT).show();
        if (restoredText != null) {
            //String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            //int idName = prefs.getInt("idName", 0); //0 is the default value.
            //Log.wtf("SESSION_ERROR", "USER ID NOT FOUND");
        }

        return restoredText;
    }

    public void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message , Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showlOG(String log, String message) {
        Log.wtf(log, message);
    }

}
