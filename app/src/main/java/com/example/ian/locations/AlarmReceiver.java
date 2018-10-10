package com.example.ian.locations;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ian.locations.adapter.DBAdapter;
import com.example.ian.locations.model.CheckerReceiver;
import com.example.ian.locations.model.Receiver;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.SEND_SMS;
import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {


    // format 24hre ex. 12:12 , 17:15
    private static String HOUR_FORMAT = "hh:mm a";

    LocationManager locationManager;

    static final int REQUEST_LOCATION = 1;

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.wtf("START-BOOT", "START ALARM");
       // Toast.makeText(context, "runnig me", Toast.LENGTH_LONG).show();

        //get location
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.test();


        //String now = getCurrentHour();
        Log.wtf("RUNNINT-10", "I'm running in 2 minutes");
        //getLocation(context);

        checkLastReceiverSendCreatedAt(context);




//        String start = "09:00";
//        String end = "09:10";
//        Log.wtf("CHECK-TIME-09", now + " between " + start + "-" + end + "?");
//        Log.wtf("CHECK-RESULT-09", isHourInInterval(now, start, end) + "");
//
//        String three_pm_start = "03:00";
//        String three_pm_end = "03:10";
//        Log.wtf("CHECK-TIME-03", now + " between " + three_pm_start + "-" + three_pm_end + "?");
//        Log.wtf("CHECK-RESULT-03", isHourInInterval(now, three_pm_start, three_pm_end) + "");
//
//        String seven_pm_start = "07:00";
//        String seven_pm_end = "07:10";
//        Log.wtf("CHECK-TIME-07", now + " between " + seven_pm_start + "-" + seven_pm_end + "?");
//        Log.wtf("CHECK-RESULT-07", isHourInInterval(now, seven_pm_start, seven_pm_end) + "");


    }

    public void getChecker(String message, Context context) {



        //Log.wtf("TEST-1", "calling function");
        //String message = "test message";
        Receiver receiver = new Receiver();
        List<Receiver> receiverList = receiver.getAllReceiverByUserId(1 + "");

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
                    // Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
                } else {
                    //Log.wtf("INSERTED", id + "");
                    String phone = receiver.getPhoneByPosition(cursor_position);
                    //SEND SMS
                    sendSms(phone, message, context);
                    // Log.wtf("SEND-PHONE-NULL", phone+" sending");

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

            if (increment_position < receiver1.getCursorCount()) {
                // Log.wtf("dakora", increment_position+"");
                //IF INCREMENT POSITION IS GREATER THAN POSITION GET THE FIRST POSITION THEN INSERT FIRST POSITION
                //Log.wtf("insert", "heere");
                //GET PHONE RECEIVER BASE ON POSITION

                //SEND SMS
//                Receiver receiver2 = new Receiver();
//                String phone = receiver2.getPhoneByPosition(increment_position);
//
//
//                //Log.wtf("SEND-PHONE-dakora", phone+" sending");
//
//                //Log.wtf("MY-POSITION", increment_position+"");
//                sendSms(phone, message, context);
//                //INSERT POSITION CHECKERS

                CheckerReceiver checkerReceiver_add = new CheckerReceiver();
                checkerReceiver_add.setCursor_position(increment_position);
                long id = checkerReceiver_add.add();

                if (id == 0) {
                    //Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
                } else {

                    //SEND SMS
                    Receiver receiver2 = new Receiver();
                    String phone = receiver2.getPhoneByPosition(increment_position);
                    //Log.wtf("SEND-PHONE-dakora", phone + " sending");

                    //Log.wtf("MY-POSITION", increment_position + "");
                    sendSms(phone, message, context);
                    //INSERT POSITION CHECKERS

                    //increment_position = 0;
                }


                //increment_position = 0;

            } else {
                //Log.wtf("INSERT-ZERO", "zero-inserted");
                insertPositionReceiverChecker(0);
                Receiver receiver2 = new Receiver();
                String phone = receiver2.getPhoneByPosition(0);
                increment_position--;
                //Log.wtf("SEND-PHONE", phone+" sending");
                //Log.wtf("MY-POSITION", increment_position+"");
                //SEND SMS
                sendSms(phone, message, context);

            }


        }
    }

    public void sendSms(String phonenumber, String message, Context context) {
        SmsManager manager = SmsManager.getDefault();

        PendingIntent piSend = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);

        int length = message.length();

        if (length > 50) {
            ArrayList<String> messagelist = manager.divideMessage(message);

            manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        } else {
            manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
        }
    }

    public void insertPositionReceiverChecker(int position) {
        //int cursor_position = receiver.getCursorPosition(receiverList.get(0).getId()+"");
        //Log.wtf("CURSOR-POSITION", cursor_position+"");
        CheckerReceiver checkerReceiver_add = new CheckerReceiver();
        checkerReceiver_add.setCursor_position(position);
        long id = checkerReceiver_add.add();
        if (id == 0) {
            Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
        } else {
            Log.wtf("INSERTED", id + "");
        }
    }


    public void getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String result = "2|"+latitude+"|"+longitude;
            Log.wtf("THE-LOCATION", result+"");
            getChecker(result, context);

            // if message length is too long messages are divided
        }
        else
        {
            //Toast.makeText(context, "No location",Toast.LENGTH_LONG).show();
        }
    }

    public void checkLastReceiverSendCreatedAt(Context context)
    {
        CheckerReceiver checkerReceiver = new CheckerReceiver();
        String created_at = checkerReceiver.getLastRecordCreatedAt();
        // Log.wtf("CREATED_AT_DATA", created_at+"");
        //check the last record if already 30 minutes then add



        if(!created_at.isEmpty()) {

            //Log.wtf("TIME-START", created_at+"");

            //parseTime("2018-08-02 01:51:19");
            try {
//
                //Log.wtf("databasecreatedat", created_at+"");
                org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime dt = formatter.parseDateTime(created_at);

                //current date
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String current_date_format = s.format(new Date());
                //Log.wtf("CURRENT-DATE", current_date_format+"");


                org.joda.time.format.DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime currentTime = formatter2.parseDateTime(current_date_format);

                boolean result = Minutes.minutesBetween(new DateTime(dt), new DateTime(currentTime))
                        .isGreaterThan(Minutes.minutes(30));

                if(result) {
                    //Log.wtf("HALA-true", "checking time "+result+"");

                    Log.wtf("SEND---------", "SENDING ME PLEASE to already 30 minutes");
                    //sendReceiver();
                    getLocation(context);
                }
                else {
                    Log.wtf("HALA", "checking time not yet 30 minutes "+result+"");
                }

            } catch(Exception e) { //this generic but you can control another types of exception
                // look the origin of excption
                // Log.wtf("ERROR_KA", e.toString()+"");
            }




            //hoursAgo(created_at);
        }
        else {

            getLocation(context);
            Log.wtf("empty", "empty cursor for created_at");
        }

    }

    public void setAlarm(Context context) {


        Log.wtf("RUNNINT-10", "I'm running in 2 minutes set me");
        int interval = 5000; // 5 seconds
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), interval, alarmIntent);

        ComponentName receiver = new ComponentName(context, StartMyServiceAtBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }
}