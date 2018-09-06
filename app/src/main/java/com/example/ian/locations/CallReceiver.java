package com.example.ian.locations;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    String LAUNCHER_NUMBER = "#7878*";
    private static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.example", "com.example.ian.locations.StartMyServiceAtBootReceiver");


    @Override
    public void onReceive(Context context, Intent intent) {

        String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (LAUNCHER_NUMBER.equals(phoneNubmer)) {
            setResultData(null);

            if (isLauncherIconVisible(context)) {
                //showToast(context, "Inside if..");
//                Intent appIntent = new Intent(context, MainActivity.class);
//                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(appIntent);
                //fn_unhide(context);
                Intent appIntent = new Intent(context, ServerLoginActivity2.class);
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(appIntent);
            } else {
                Log.wtf("NOT-HIDE", "app is not hide");
            }


        }

    }

    private boolean isLauncherIconVisible(Context context) {
        int enabledSetting = context.getPackageManager().getComponentEnabledSetting(LAUNCHER_COMPONENT_NAME);
        return enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

}
