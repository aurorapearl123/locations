package com.example.ian.locations;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    double latitude  = 0;
    double longitude = 0;
    static final int REQUEST_LOCATION = 1;
    EditText text_phone_number;
    Button btn_send;

    private static final int REQUEST_SMS = 0;
    private static final int REQ_PICK_CONTACT = 2 ;

    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    LocationManager locationManager;

    private TextView sendStatusTextView;
    private TextView deliveryStatusTextView;

    public static String id = "test_channel_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_phone_number = (EditText) findViewById(R.id.id_phone_number);
        btn_send = (Button) findViewById(R.id.id_button_send);

        sendStatusTextView = (TextView) findViewById(R.id.message_status_text_view);
        deliveryStatusTextView = (TextView) findViewById(R.id.delivery_status_text_view);

        //get location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn_send.setOnClickListener(this);
        //getLocation();
        //sendSms("09268818030","hello there");
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void sendSms(String phoneNumber, String message) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "sms:" + phoneNumber ) );
//        intent.putExtra( "sms_body", message );
//        startActivity(intent);


    }

//    private void sendSmsProvider()
//    {
//
//                PendingIntent piSent = PendingIntent.getBroadcast(getBaseContext(), 0, new Intent("in.wptrafficanalyzer.sent") , 0);
// 
//               
//                PendingIntent piDelivered = PendingIntent.getBroadcast(getBaseContext(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
// 
//                // Getting an instance of SmsManager to sent sms message from the application*/
//                SmsManager smsManager = SmsManager.getDefault();
// 
//                // Sending the Sms message to the intended party */
//                smsManager.sendTextMessage(phone_number, null, "testing", piSent, piDelivered);
//    }

    public void getLocation(String phonenumber)
    {
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
               String ducua = "09177776718";
               String test = "09268818030";
                sendSms(phonenumber,result);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No location",Toast.LENGTH_LONG).show();
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

    public void getLatLang()
    {
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
    }

    @Override
    public void onClick(View view) {

        //Toast.makeText(getApplicationContext(), "No locatifdsafon",Toast.LENGTH_LONG).show();
        //Log.wtf("DATA", "HELLO WORLD");
            //getLocation(phone_number);

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
                sendMySMS();
            }



            //Log.wtf("DATA", "HELLO WORLD"+phone_number+"");
            //Toast.makeText(getApplicationContext(), phone_number, Toast.LENGTH_LONG).show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void onResume() {
        super.onResume();
        sentStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                sendStatusTextView.setText(s);

            }
        };
        deliveredStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                deliveryStatusTextView.setText(s);
                text_phone_number.setText("");
                //messageEditText.setText("");
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    public void sendMySMS() {

        String phone_number = text_phone_number.getText().toString();
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
                    String ducua = "09177776718";
                    String test = "09268818030";
                    //sendSms(phonenumber,result);
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

    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }



    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, REQUEST_SMS);
    }

}
