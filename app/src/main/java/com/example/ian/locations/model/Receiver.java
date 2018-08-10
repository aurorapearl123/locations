package com.example.ian.locations.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.ian.locations.adapter.DBAdapter;

import java.util.ArrayList;
import java.util.List;

public class Receiver {

    public int id;
    public int user_id;
    public double latitude;
    public double longitude;
    public String status;
    public String created_at;
    public String updated_at;
    public String phone;

    public Receiver() {
    }

    public Receiver(int user_id, String phone, double latitude, double longitude) {
        this.user_id = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }

    // create table PERSON
    public static final String TABLE_NAME = "receivers";
    // fields of PERSON
    public static final String _ID = "id";
    public static final String USER_ID = "user_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CREATED_AT = "created_at";
    public static final String STATUS = "status";
    public static final String UPDATED_AT = "update_at";
    public static final String PHONE = "phone";
    //CREATE TABLE
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + USER_ID + " INTEGER			, "
            + PHONE + " VARCHAR(50) 	NOT NULL, "
            + LATITUDE + " VARCHAR(50) 	NOT NULL, "
            + LONGITUDE + " VARCHAR(50) 	NOT NULL, "
            + CREATED_AT + " DATETIME		NOT NULL 		DEFAULT CURRENT_TIMESTAMP, "
            + UPDATED_AT + " DATETIME		NOT NULL 		DEFAULT CURRENT_TIMESTAMP, "
            + STATUS + " CHAR(1) 		NOT NULL 		DEFAULT '0' ,  "
            + "FOREIGN KEY (" + USER_ID + ") REFERENCES " + TABLE_NAME + " ( " + _ID + " ) "
            + " ); ";


    //updateById date_modified
    public static final String UPDATE_TRIGGER =
            "CREATE TRIGGER " + TABLE_NAME + "_trigger" +
                    "  AFTER UPDATE ON " + TABLE_NAME + " FOR EACH ROW" +
                    "  BEGIN " +
                    "UPDATE " + TABLE_NAME +
                    "  SET " + UPDATED_AT + " = current_timestamp" +
                    "  WHERE " + _ID + " = old." + _ID + ";" +
                    "  END";


    //drop table
    public static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLE_NAME;

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(USER_ID, this.user_id);
        values.put(PHONE, this.phone);
        values.put(LONGITUDE, this.longitude);
        values.put(LATITUDE, this.latitude);
        return values;
    }

    public long add()
    {
        return DBAdapter.add(TABLE_NAME, LONGITUDE, getContentValues());
    }

    public int getLastInserdId()
    {
        return DBAdapter.getHighestID(TABLE_NAME);
    }

    public ArrayList<Receiver> getAllReceivers(){
        ArrayList<Receiver> receiverArrayList = new ArrayList<>();
        Receiver receiver = null;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        while (cursor.moveToNext()) {
            Receiver receiver1 = new Receiver();
            receiver1.id = cursor.getInt(cursor.getColumnIndex(_ID));
            receiver1.user_id = cursor.getInt(cursor.getColumnIndex(USER_ID));
            receiver1.phone = cursor.getString(cursor.getColumnIndex(PHONE));
            receiver1.latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(LATITUDE)));
            receiver1.longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(LONGITUDE)));
            receiver1.created_at = cursor.getString(cursor.getColumnIndex(CREATED_AT));
            receiver1.updated_at = cursor.getString(cursor.getColumnIndex(UPDATED_AT));
            receiverArrayList.add(receiver1);
        }
        return receiverArrayList;
    }

    public void delete(int id)
    {
        Log.wtf("delete_reciever", id+"");
        DBAdapter.deleteRecord(TABLE_NAME, id+"");
    }

    public List<Receiver> getAllReceiverByUserId(String user_id)
    {
        Receiver receiver = null;
        List<Receiver> receiverList = new ArrayList<>();
        //Cursor cursor = DBAdapter.getReceiverByUserId(user_id);
        Cursor cursor = DBAdapter.getRecord(TABLE_NAME, USER_ID, user_id);
        while(cursor.moveToNext()){
            //Log.wtf("RESULT", cursor.getString(cursor.getColumnIndex(USER_ID))+"");
            receiver = new Receiver();
            receiver.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            receiver.setUser_id(cursor.getInt(cursor.getColumnIndex(USER_ID)));
            receiver.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
            receiverList.add(receiver);
        }

        return receiverList;
    }

    public List<Receiver> getAllReceiverByUserId2(String user_id)
    {
        Receiver receiver = null;
        List<Receiver> receiverList = new ArrayList<>();
        //Cursor cursor = DBAdapter.getReceiverByUserId(user_id);
        Cursor cursor = DBAdapter.getRecord2(TABLE_NAME, USER_ID, user_id);
        if(cursor!=null && cursor.getCount()>0) {
            while(cursor.moveToNext()){
                //Log.wtf("RESULT", cursor.getString(cursor.getColumnIndex(USER_ID))+"");
                receiver = new Receiver();
                receiver.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                receiver.setUser_id(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                receiver.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                receiverList.add(receiver);
            }

        }

        return receiverList;
    }

    public String getDbPhone(String id)
    {
        Cursor cursor = DBAdapter.getRecord(TABLE_NAME, _ID, id);
        //Cursor cursor = DBAdapter.getReceiverPhone(1+"");
        //
        String phone = null;
        while (cursor.moveToNext())
        {
            //Log.wtf("MESSAGE", "inside while lop");
            phone = cursor.getString(cursor.getColumnIndex(PHONE));
            //Log.wtf("AHAKA", phone+"");
        }

        return phone;
    }

    public void getNextRowData(String receiver_id)
    {
        //Cursor cursor = DBAdapter.getRecord(TABLE_NAME, _ID, 2+"");

        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);

//        while(cursor.moveToNext())
//        {
//            int next_id = cursor.getInt(cursor.getColumnIndex(_ID));
//            Log.wtf("NEXT-ID", next_id+"");
//        }
        int position = Integer.parseInt(receiver_id);
        --position;
        Log.wtf("CREMENT", position+"");

        cursor.moveToPosition(position);

        int cur_position = cursor.getPosition();
        //Log.wtf("POSITION-CORSUR", cur_position+"");

        //while(cursor.moveToNext()){
            int next_id = cursor.getInt(cursor.getColumnIndex(PHONE));
          // Log.wtf("NEXT-ID", next_id+"");
        //}


//        if (cursor.moveToFirst()) {
//            do {
//                //result.add(new Location(cursor.getString(cursor.getColumnIndex("name")), cursor.getFloat(cursor.getColumnIndex("lat")), cursor.getFloat(cursor.getColumnIndex("lng"))));
//                Log.wtf("NEST-DATA", cursor.getString(cursor.getColumnIndex(PHONE))+"");
//            } while (cursor.moveToNext());
//        }
//       if(cursor.moveToFirst())
//        {
//            while (!cursor.isAfterLast()) {
//               // cursor.moveToNext();
//                while(cursor.moveToNext()){
//                    int next_id = cursor.getInt(cursor.getColumnIndex(_ID));
//                    Log.wtf("NEXT-ID", next_id+"");
//                }
//
//               // int next_id = cursor.getInt(cursor.getColumnIndex(_ID));
//                //   Log.wtf("NEXT-ID", next_id+"");
//
//            }
//        }
    }


    public int getCursorPosition(String id)
    {
        int position  = 0;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        while(cursor.moveToNext()) {
            String receiver_id = cursor.getString(cursor.getColumnIndex(_ID));
            if(receiver_id.equals(id)) {
                position = cursor.getPosition();
            }
        }
        return position;
    }

    public String getLastInsertId()
    {
        return DBAdapter.getLastInsertId(TABLE_NAME);
    }

    public String getPhoneByPosition(int position)
    {
        String phone = "";
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        cursor.moveToPosition(position);
        try{
            Log.wtf("THIS-PHONE", cursor.getString(cursor.getColumnIndex(PHONE)));
            phone =  cursor.getString(cursor.getColumnIndex(PHONE));
        }
        catch(Exception e){
            Log.wtf("ERROR-ME", e.toString()+"");
        }

        return phone;
    }

    public int getCursorCount()
    {
        int size = 0;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        size = cursor.getCount();
        return size;
    }


    public int getIdByPosition(int position)
    {
        int id = 0;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        cursor.moveToPosition(position);
        try{
            Log.wtf("THIS-ID", cursor.getInt(cursor.getColumnIndex(_ID))+"");
            id =  cursor.getInt(cursor.getColumnIndex(_ID));
        }
        catch(Exception e){
            Log.wtf("ERROR-ME", e.toString()+"");
        }

        return id;
    }



}
