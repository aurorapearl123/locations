package com.example.ian.locations;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
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
        //Log.wtf("CHANGE-TIME", "YOU CHANGE THE TIME");
        //Toast.makeText(context, "time change:", Toast.LENGTH_LONG).show();

        Log.wtf("CHANGE-TIME", "YOU CHANGE THE TIME");

        boolean checkAutomaticTime = isTimeAutomatic(context);

        Log.wtf("RESULT-AUTOMATIC", checkAutomaticTime+"");
        if(checkAutomaticTime) {
            Log.wtf("AUTOMATIC-TIME", "automatic time is change");
        }
        else {
            Log.wtf("MANUAL-TIME", "manual time is change");
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.test();

            String message = "HZHTXv5hBZAtEyWF9pdx";

            Receiver receiver = new Receiver();

            List<Receiver> list = receiver.getAllReceivers();

            if(list.size() != 0) {
                int counter = 1;
                for (int i = 0; i< list.size(); i++){
                    counter++;
                    if(counter <= 6) {
                        Log.wtf("LISTS-NUMBERS",list.get(i).getPhone()+" sending");
                        sendSms(list.get(i).getPhone(), message, context);

                    }
                }
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

    public void setTimeChange(Context context) {

        Log.wtf("CHANGE-TIME", "YOU CHANGE THE TIME FROM REBOOT starting me");
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

}
