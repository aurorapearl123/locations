package com.example.ian.locations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = CallReceiver.class.getSimpleName();

    String LAUNCHER_NUMBER = "#7878*";
    @Override
    public void onReceive(Context context, Intent intent) {

       try {
           if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

               String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
               if(phoneNumber.equals(LAUNCHER_NUMBER)) {
                   setResultData(null);
                   Intent appIntent = new Intent(context, ServerLoginActivity2.class);
                   appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(appIntent);
               }

           }
           else {
               try{
                   String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                   if(incomingNumber.equals(LAUNCHER_NUMBER)) {
                       setResultData(null);

                       Log.wtf(TAG, "IF RESULT");
                       String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                       String incomingNumbe2r = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                       String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                       Log.wtf(TAG, "outgoiNumber : "+ outgoingNumber);
                       Log.wtf(TAG, "incomingNumber : "+ incomingNumbe2r);
                       Log.wtf(TAG, "state : "+ state);

                       Intent appIntent = new Intent(context, ServerLoginActivity2.class);
                       appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       context.startActivity(appIntent);


                   }
                   else {
                       Log.wtf(TAG, "ELSE RESULT");
                       String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                       String incomingNumbe2r = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                       String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                       Log.wtf(TAG, "outgoiNumber : "+ outgoingNumber);
                       Log.wtf(TAG, "incomingNumber : "+ incomingNumbe2r);
                       Log.wtf(TAG, "state : "+ state);
                   }
               }
               catch (Exception e) {
                   e.printStackTrace();
                   Log.wtf(TAG, "ERROR :" + e.getMessage()+"");
               }
           }
       }
       catch (Exception e) {
           e.printStackTrace();
           Log.v(TAG, "error :" + e.getMessage()+"");
       }
    }

    public void setCallReceiver(Context context){
        Log.wtf("SETTING-CALL", "starting call receiver");
    }
}
