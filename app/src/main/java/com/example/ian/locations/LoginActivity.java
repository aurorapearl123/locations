package com.example.ian.locations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.example.ian.locations.adapter.DBAdapter;
import com.example.ian.locations.model.Receiver;
import com.example.ian.locations.model.User;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txt_password;
    Button btn_login;
    public static final String MY_PREFS_NAME = "USER_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_login);

        txt_password = (EditText) findViewById(R.id.id_password);
        btn_login = (Button) findViewById(R.id.id_button_login);


        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.test();

        btn_login.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        String password = txt_password.getText().toString();

        String address = DebugDB.getAddressLog();
       // Log.wtf("ADDRESS", address+"");

        if(password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please input password", Toast.LENGTH_LONG).show();
        }
        else
        {
            addUser(password);
        }
    }

    public void addUser(String password)
    {

        addUserData(password);

    }

    public void addUserData(String password)
    {

        //add user
        User user = new User();
        User user1_super_amin = (User) user.getRecordByPassword("superadmin123");
        if(user1_super_amin == null) {
            //String username = user1.getUsername();
            //Toast.makeText(getApplicationContext(),"Password not found", Toast.LENGTH_LONG).show();
            User user_super_admin = new User("admin", "superadmin123");
            user_super_admin.add();
        }

        //login
        int user_id = 0;
        User user_login = new User();
        User user1 = (User) user_login.getRecordByPassword("superadmin123");
        user_id = user1.getId();
        setUserIdSession(user_id);
        String demo = "demo123";
        String manila = "manila123";
        String jsp = "jsp123";
        String migs = "migs123";
        String benit = "benit123";
        String mnldc = "mnldc123";
        if(password.equals(manila)) {
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"Password not found", Toast.LENGTH_LONG).show();
        }



    }

    public void addReceiver(int user_id)
    {
        Receiver receiver1 = new Receiver(user_id, "09177776718",10.3434835, 123.9191917);
        long id_r = receiver1.add();
//        if (id == 0) {
//            Log.wtf("receiver", "Error Can't Be added!..");
//        } else {
//            Log.wtf("receiver", id_r + "");
//        }
        Receiver receiver2 = new Receiver(user_id, "09173200619", 10.3434835, 123.9191917);
        receiver2.add();
//        Receiver receiver3 = new Receiver(user_id, "09177776718",10.3434835, 123.9191917);
//        receiver3.add();
//        Receiver receiver4 = new Receiver(user_id,"09177776718", 10.3434835, 123.9191917);
//        receiver4.add();
//        Receiver receiver5 = new Receiver(user_id,"09177776718", 10.3434835, 123.9191917);
//        receiver5.add();
//
        Receiver receiver = new Receiver();
        ArrayList<Receiver> receiverArrayList = receiver.getAllReceivers();
        Log.wtf("RECEIVER", receiverArrayList.toString()+"");
    }

    public void setUserIdSession(int user_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("user_id", user_id+"");
        editor.apply();
    }

}
