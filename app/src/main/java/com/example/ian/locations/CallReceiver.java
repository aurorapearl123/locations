package com.example.ian.locations;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";

    private static boolean incomingFlag = false;

    private static String incoming_number = null;
    String LAUNCHER_NUMBER = "#7878*";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

            incomingFlag = false;

            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if(phoneNumber.equals(LAUNCHER_NUMBER)) {
                setResultData(null);
                Intent appIntent = new Intent(context, ServerLoginActivity2.class);
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(appIntent);
            }

            //Log.i(TAG, "call OUT:" + phoneNumber);
            //Toast.makeText(context, "call OUT:" + phoneNumber, Toast.LENGTH_LONG).show();


        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {

                case TelephonyManager.CALL_STATE_RINGING:

                    incomingFlag = true;

                    incoming_number = intent.getStringExtra("incoming_number");

                   // Log.i(TAG, "RINGING :" + incoming_number);
                    //Toast.makeText(context, "RINGING :" + incoming_number, Toast.LENGTH_LONG).show();

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:

                    if (incomingFlag) {

                        //Log.i(TAG, "incoming ACCEPT :" + incoming_number);
                        //Toast.makeText(context, "incoming ACCEPT :" + incoming_number, Toast.LENGTH_LONG).show();

                    }

                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    if (incomingFlag) {

                        //Log.i(TAG, "incoming IDLE");
                        //Toast.makeText(context, "incoming IDLE", Toast.LENGTH_LONG).show();


                    }

                    break;

            }

        }
    }

    public void setCallReceiver(Context context){
        Log.wtf("SETTING-CALL", "starting call receiver");
    }
}
