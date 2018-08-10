package com.example.ian.locations.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.ian.locations.LoginActivity;
import com.example.ian.locations.adapter.DBAdapter;

import java.util.ArrayList;

public class User
{
    public int id;
    public String username;
    public String password;

    // table AREA
    public static final String TABLE_NAME = "user";
    // fields of AREA
    public static final String _ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CREATED_AT = "created_at";

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    //create TABLE AREA
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + USERNAME 		    	+ " VARCHAR(50) 	NOT NULL, "
            + PASSWORD 		    	+ " VARCHAR(10) 	NOT NULL, "
            + CREATED_AT + " DATETIME		NOT NULL 		DEFAULT (datetime('now','localtime'))"
            + " ); ";
    //drop table area
    public static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLE_NAME;

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(USERNAME, this.username);
        values.put(PASSWORD, this.password);
        return values;
    }

    public long add()
    {
        return DBAdapter.add(TABLE_NAME, USERNAME, getContentValues());
    }

    public User getRecordByPassword(String value)
    {
        User user = null;
        Cursor cursor = DBAdapter.searchUser(value);
        //Log.wtf("RESULT", cursor.getString(cursor.getColumnIndex(USERNAME))+"");
        if (cursor.moveToNext()){
           // do {

                // Passing values
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));

                Log.wtf("INSIDE", cursor.getString(cursor.getColumnIndex(PASSWORD))+"");
                // Do something Here with values
            //} while(cursor.moveToNext());
        }

//        while (cursor.moveToFirst()) {
//            user = new User();
//            Log.wtf("INSIDE", cursor.getString(cursor.getColumnIndex(USERNAME))+"");
//            user.username = cursor.getString(cursor.getColumnIndex(USERNAME));
//            user.password = cursor.getString(cursor.getColumnIndex(PASSWORD));
//        }
        return user;
    }

    public ArrayList<User> getAllUsers()
    {
        ArrayList<User> userArrayList = new ArrayList<>();
        User user = null;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        while (cursor.moveToNext())
        {
            user = new User();
            user.id = cursor.getInt(cursor.getColumnIndex(_ID));
            user.username = cursor.getString(cursor.getColumnIndex(USERNAME));
            user.password = cursor.getString(cursor.getColumnIndex(PASSWORD));
            userArrayList.add(user);

        }


        return userArrayList;
    }


}
