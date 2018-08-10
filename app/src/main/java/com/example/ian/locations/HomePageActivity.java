package com.example.ian.locations;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ian.locations.adapter.ReceiverAdapter;
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

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ReceiverAdapter adapter;
    private List<Receiver> albumList;
    Button btn_add_receiver;

    private final int MenuItem_Logout = 1;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private static final int REQUEST_SMS = 0;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    public static String LAT_LONG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btn_add_receiver = (Button)findViewById(R.id.id_add);

        //get location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        albumList = new ArrayList<>();
        adapter = new ReceiverAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlbums();

        btn_add_receiver.setOnClickListener(this);

        firstTimeSend();
        //sendReceiver();
        checkLastReceiverSendCreatedAt();
        sendSmsAsynchronousTAsk();
        //GET LOCATION FIRST
        getLotLang();


    }

    public void checkLastReceiverSendCreatedAt()
    {
        CheckerReceiver checkerReceiver = new CheckerReceiver();
        String created_at = checkerReceiver.getLastRecordCreatedAt();
        // Log.wtf("CREATED_AT_DATA", created_at+"");
        //check the last record if already 30 minutes then add

        if(!created_at.isEmpty()) {
            try {
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
                    //Log.wtf("HALA", "checking time "+result+"");
                }

            } catch(Exception e) { //this generic but you can control another types of exception
                // look the origin of excption
               // Log.wtf("ERROR_KA", e.toString()+"");
            }
        }
        else {
            //Log.wtf("empty", "empty cursor for created_at");
        }
    }


    private void prepareAlbums() {

        Receiver receiver = new Receiver();
        List<Receiver> receiverList = receiver.getAllReceivers();
        for (int i = 0; i < receiverList.size(); i ++) {
            Receiver receiver1 = new Receiver();
            receiver1.setId(receiverList.get(i).getId());
            receiver1.setUser_id(receiverList.get(i).getUser_id());
            receiver1.setPhone(receiverList.get(i).getPhone());
            receiver1.setLatitude(receiverList.get(i).getLatitude());
            receiver1.setLongitude(receiverList.get(i).getLongitude());
            albumList.add(receiver1);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("RECEIVER")
                .setMessage("Enter receiver below")
                .setPositiveButton("Done", null) // null to override the onClick
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .create();

        LayoutInflater inflater = this.getLayoutInflater();
       final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
       alertDialog.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.id_edit_text_phone);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //showToast("Dialog not dismissed!");

                        String phone =  edt.getText().toString();
                        if(phone.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please input phone number", Toast.LENGTH_LONG).show();
                        }
                        else {
                            int user_id = 0;
                            try {
                                user_id = Integer.parseInt(getSessionId());
                                Receiver receiver = new Receiver(user_id, phone, 0,0);
                                long id = receiver.add();
                                if(id == 0) {
                                    Log.wtf("ERROR", "ERROR ADDING RECEIVER");
                                }
                                CheckerReceiver checkerReceiver = new CheckerReceiver();
                                //check if first time send sms
                                if(checkerReceiver.getLastRecordCreatedAt().isEmpty()) {
                                    //get the last receiver id
                                    Receiver receiver1 = new Receiver();
                                    if(!receiver1.getLastInsertId().isEmpty()) {
                                        String receiver_id = receiver1.getLastInsertId();
//                                        //get position to add checker
                                        CheckerReceiver checkerReceiver_add = new CheckerReceiver();
                                        checkerReceiver_add.setCursor_position(receiver1.getCursorPosition(receiver_id));
                                        checkerReceiver.add();
//                                        //send sms
                                        //prepareSms(phone);
                                       // Log.wtf("INSERT-THIS", "Please insert me.. my last id is"+receiver_id+"my phone "+phone+""+" my loacation"+ LAT_LONG);
                                        //sendSMSFirstTime(phone, "Hello");
                                        getPermissionToReadSMS();
                                        firstTimeSend();
                                        //use this code
                                        //sendSMSFirstTime(phone, LAT_LONG);
                                       prepareSms(phone);

                                        //sendMySMS(phone);

                                    }
                                    else {
                                        Log.wtf("NO", "no lastinserted");
                                    }

                                }
                                albumList.add(receiver);
                                adapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                            catch(NumberFormatException e){
                                Log.wtf("ERROR_CASTING", "error casting user id");
                            }
                        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuItem edit_item = menu.add(0, MenuItem_Logout, 0, "edit");
        edit_item.setIcon(R.drawable.logout);
        edit_item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MenuItem_Logout:
                //sendReceiver();
                logoutAlert();

                return true;
            default:
                return false;
        }
    }

    public void sendReceiver() {

//        Calendar currTime = Calendar.getInstance();
//        int hour = currTime.get(Calendar.HOUR_OF_DAY);
//        //9am to 10pm
//        if(hour >= 9 && hour < 22) {
//            Log.wtf("RESULT-TIME", "TRUE");
//        }
//        else {
//            Log.wtf("RESULT-TIME-FALSE", "FALSE");
//        }

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
                Log.wtf("CURSOR-POSITION", cursor_position + "");
                CheckerReceiver checkerReceiver_add = new CheckerReceiver();
                checkerReceiver_add.setCursor_position(cursor_position);
                long id = checkerReceiver_add.add();
                if (id == 0) {
                    Log.wtf("CHECKER_RECEIVER", "NOT INSERTED");
                } else {
                    Log.wtf("INSERTED", id + "");
                    String phone = receiver.getPhoneByPosition(cursor_position);
                    //SEND SMS
                    prepareSms(phone);

                }
            }
            //Log.wtf("NOT-FOUND", "INSERT ME");
        } else {
            //get checkers
            Log.wtf("FOUND-CHECKERS", "GET RECEIVER IS");
            // GET LAST RECORD FOR CURSOR POSITION IN CHECKER RECEIVER TABLE
            CheckerReceiver checker = new CheckerReceiver();
            int position = Integer.parseInt(checker.getLastRecord());
            Log.wtf("THIS-POSITION", position + "");
            //SEND SMS LOGIC
            //INSERT CHECKER AGAIN FOR NEXT POSITION
            //CHECK IF THE POSITION IS LAST THEN INSERT FIRST POSITION
            int increment_position = position;
            increment_position++;
            Log.wtf("INCREMENT", increment_position+"");
            Log.wtf("FINAL-POSITION", position+"");
            Receiver receiver1 = new Receiver();
            Log.wtf("RECEIVER-COUNT", receiver1.getCursorCount()+"");

            if(increment_position < receiver1.getCursorCount()) {
                Log.wtf("dakora", increment_position+"");
                //IF INCREMENT POSITION IS GREATER THAN POSITION GET THE FIRST POSITION THEN INSERT FIRST POSITION
                Log.wtf("insert", "heere");
                //GET PHONE RECEIVER BASE ON POSITION

                //SEND SMS
                Receiver receiver2 = new Receiver();
                String phone = receiver2.getPhoneByPosition(increment_position);
                Log.wtf("SEND-PHONE-dakora", phone+" sending");
                prepareSms(phone);
                //INSERT POSITION CHECKERS
                insertPositionReceiverChecker(increment_position);

            }
            else {
                Log.wtf("INSERT-ZERO", "zero-inserted");
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

        //String message = messageEditText.getText().toString();

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
                    //Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
                    //sendSms(phonenumber,result);
                    // if message length is too long messages are divided
                    Log.wtf("SEND-MESSAGE", "sending message TO "+phone_number);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                //getLocation();
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(HomePageActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void sendSmsAsynchronousTAsk() {
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

    public void logoutAlert()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Do you want to logout?")
                .setPositiveButton("Logout", null) // null to override the onClick
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
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); //
                        System.exit(0);
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






    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }

    public void getLotLang() {

        //String message = messageEditText.getText().toString();

        //Check if the phoneNumber is empty

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
                    LAT_LONG = "2|"+latitude+"|"+longitude;

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No location",Toast.LENGTH_LONG).show();
                }
            }

    }


}
