package com.example.ian.locations;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    AlarmReceiver alarmReceiver = new AlarmReceiver();

    TimeChangedReceiver timeChangedReceiver = new TimeChangedReceiver();

    CallReceiver callReceiver = new CallReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            alarmReceiver.setAlarm(context);
            timeChangedReceiver.setTimeChange(context);
            callReceiver.setCallReceiver(context);

        }
    }

}
