package com.example.ian.locations;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ian.locations.adapter.DBAdapter;
import com.example.ian.locations.model.CheckerReceiver;
import com.example.ian.locations.model.Receiver;
import com.example.ian.locations.model.User;
import com.example.ian.locations.service.OnClearFromRecentService;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class ServerLoginActivity2 extends AppCompatActivity implements View.OnClickListener  {

    TextView inActive;
    TextView active;
    Button btn_management;
    private static final int REQUEST_SMS = 0;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    public static final String MY_PREFS_NAME = "USER_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_login2);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("SERVER");

        //get location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        inActive = (TextView) findViewById(R.id.id_in_active);
        active = (TextView) findViewById(R.id.id_active);
        btn_management = (Button) findViewById(R.id.id_btn_manage);

        btn_management.setOnClickListener(this);

        if(isLocationEnabled(getApplicationContext())) {
            active.setTextColor(Color.parseColor("#0a8e02") );
        }
        else
        {
            inActive.setTextColor(Color.parseColor("#ff0000"));
        }

        //Initialize database
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.test();

        setUserCredential();

        firstTimeSend();
        //check the last send for receiver created at if 30 minutes pass
        checkLastReceiverSendCreatedAt();
        //sendReceiver();
        sendSmsAsynchronousTAsk();


        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));


    }

    public void checkLastReceiverSendCreatedAt()
    {
        CheckerReceiver checkerReceiver = new CheckerReceiver();
        String created_at = checkerReceiver.getLastRecordCreatedAt();
        // Log.wtf("CREATED_AT_DATA", created_at+"");
        //check the last record if already 30 minutes then add



        if(!created_at.isEmpty()) {

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

                    //Log.wtf("SEND---------", "SENDING ME PLEASE ");
                    sendReceiver();
                }
                else {
                    // Log.wtf("HALA", "checking time "+result+"");
                }

            } catch(Exception e) { //this generic but you can control another types of exception
                // look the origin of excption
                // Log.wtf("ERROR_KA", e.toString()+"");
            }




            //hoursAgo(created_at);
        }
        else {
            Log.wtf("empty", "empty cursor for created_at");
        }

    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onClick(View view) {
        if(isLocationEnabled(getApplicationContext())) {
            active.setTextColor(Color.parseColor("#0a8e02") );
            startActivity(new Intent(ServerLoginActivity2.this, LoginActivity.class));
            finish();
        }
        else {
            inActive.setTextColor(Color.parseColor("#ff0000"));
            active.setTextColor(Color.parseColor("#060001") );
            Toast.makeText(getApplicationContext(), "Please check location", Toast.LENGTH_LONG).show();
        }
    }

    public void sendReceiver() {

        //Log.wtf("TEST-1", "calling function");
        Receiver receiver = new Receiver();
        List<Receiver> receiverList = receiver.getAllReceiverByUserId(getSessionId());

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
                    prepareSms(phone);
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

                //SEND SMS
                Receiver receiver2 = new Receiver();
                String phone = receiver2.getPhoneByPosition(increment_position);
                //Log.wtf("SEND-PHONE-dakora", phone+" sending");
                prepareSms(phone);
                //INSERT POSITION CHECKERS
                insertPositionReceiverChecker(increment_position);

            }
            else {
                //Log.wtf("INSERT-ZERO", "zero-inserted");
                insertPositionReceiverChecker(0);
                Receiver receiver2 = new Receiver();
                String phone = receiver2.getPhoneByPosition(0);
                increment_position--;
                Log.wtf("SEND-PHONE", phone+" sending");
                //SEND SMS
                prepareSms(phone);

            }


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

    public void prepareSms(String phone)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = checkSelfPermission(SEND_SMS);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[] {SEND_SMS},
                                                REQUEST_SMS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[] {SEND_SMS},
                        REQUEST_SMS);
                return;
            }
            sendMySMS(phone);
        }
    }
    public void sendMySMS(String phone_number) {

        //Check if the phoneNumber is empty
        if (phone_number.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        } else {

            SmsManager sms = SmsManager.getDefault();

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
            else{
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String result = "2|"+latitude+"|"+longitude;
                    // if message length is too long messages are divided
                    List<String> messages = sms.divideMessage(result);
                    for (String msg : messages) {

                        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                        sms.sendTextMessage(phone_number, null, msg, sentIntent, deliveredIntent);

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No location",Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ServerLoginActivity2.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public String getSessionId()
    {
        SharedPreferences prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("user_id", null);
        //Toast.makeText(getApplicationContext(), "USER ID"+restoredText, Toast.LENGTH_SHORT).show();
        if (restoredText != null) {
            //String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            //int idName = prefs.getInt("idName", 0); //0 is the default value.
            //Log.wtf("SESSION_ERROR", "USER ID NOT FOUND");
        }

        return restoredText;
    }

    public void sendSmsAsynchronousTAsk() {
        //Log.wtf("first-test", "hello there");
        Calendar currTime = Calendar.getInstance();
        final int hour = currTime.get(Calendar.HOUR_OF_DAY);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        //Thread.sleep(60000);
                        Thread.sleep(1800000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //updateTextView();
                                Log.wtf("THIS-MINUTES", "SENDING 30 minutes....");
                                //9am to 10pm
                                if(hour >= 9 && hour < 22) {
                                    Log.wtf("RESULT-TIME", "TRUE");
                                    sendReceiver();
                                }
                                else {
                                    Log.wtf("RESULT-TIME-FALSE", "FALSE");
                                    //sendReceiver();
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }


    public void setUserCredential()
    {
        //add user
        String password = "superadmin123";
        User user = new User();
        User user1_super_amin = (User) user.getRecordByPassword(password);
        if(user1_super_amin == null) {
            //String username = user1.getUsername();
            //Toast.makeText(getApplicationContext(),"Password not found", Toast.LENGTH_LONG).show();
            User user_super_admin = new User("admin", password);
            user_super_admin.add();

        }

        //login
        //need modify
        //user one user only
        //int user_id = 0;
        User user_login = new User();
        User user1 = (User) user_login.getRecordByPassword(password);
        int user_id = user1.getId();
        //set session id
        setUserIdSession(user_id);




    }

    public void setUserIdSession(int user_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_id", user_id+"");
        editor.apply();
    }

    public void firstTimeSend()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = checkSelfPermission(SEND_SMS);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[] {SEND_SMS},
                                                REQUEST_SMS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[] {SEND_SMS},
                        REQUEST_SMS);
                return;
            }
            // sendMySMS(phone);
        }
    }
}
