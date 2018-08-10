package com.example.ian.locations.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.ian.locations.adapter.DBAdapter;

import java.util.ArrayList;
import java.util.List;

public class CheckerReceiver
{
    public int id;
    public int cursor_position;

    public CheckerReceiver() {
    }

    public CheckerReceiver(int id, int receiver_id) {
        this.id = id;
        this.cursor_position = receiver_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCursor_position() {
        return cursor_position;
    }

    public void setCursor_position(int cursor_position) {
        this.cursor_position = cursor_position;
    }

    @Override
    public String toString() {
        return "CheckerReceiver{" +
                "id=" + id +
                ", cursor_position=" + cursor_position +
                '}';
    }

    // create table PERSON
    public static final String TABLE_NAME = "checker_receiver";
    // fields of PERSON
    public static final String _ID = "id";
    public static final String CURSOR_POSITION = "cursor_position";
    public static final String CREATED_AT = "created_at";
    //CREATE TABLE
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + CURSOR_POSITION + " INTEGER			, "
            + CREATED_AT + " DATETIME		NOT NULL 		DEFAULT (datetime('now','localtime')) "
            + " ); ";

    //drop table
    public static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLE_NAME;

    public CheckerReceiver getAllRecord()
    {
        CheckerReceiver checkerReceiver = null;
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        while (cursor.moveToNext())
        {
            checkerReceiver = new CheckerReceiver();
            //Log.wtf("FOUND", cursor.getInt(cursor.getColumnIndex("cursor_position"))+"");
        }

        //return found;
        return checkerReceiver;
    }

    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(CURSOR_POSITION, this.cursor_position);
        return values;
    }

    public List<CheckerReceiver> getListAllRecord(){
        List<CheckerReceiver> checkerReceiverList = new ArrayList<>();
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        while (cursor.moveToNext()){
            CheckerReceiver checkerReceiver = new CheckerReceiver();
            checkerReceiver.setCursor_position(cursor.getInt(cursor.getColumnIndex(CURSOR_POSITION)));
            checkerReceiver.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            checkerReceiverList.add(checkerReceiver);
        }
        return checkerReceiverList;
    }

    public long add()
    {
        return DBAdapter.add(TABLE_NAME, CURSOR_POSITION, getContentValues());
    }

    public int getCheckerReceiverReceiverId(String receiver_id)
    {
        int id = 0;
        Cursor cursor = DBAdapter.getRecord(TABLE_NAME, CURSOR_POSITION, receiver_id);
        while(cursor.moveToNext())
        {
            Log.wtf("RECEIVER-ID", cursor.getInt(cursor.getColumnIndex(CURSOR_POSITION))+"");
        }

        return id;
    }

    public String getLastRecord()
    {
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
       cursor.moveToPosition(cursor.getCount() -1);
        //Log.wtf("LAST-POSITION", cursor.getString(cursor.getColumnIndex("cursor_position")));
        String position = cursor.getString(cursor.getColumnIndex("cursor_position"));

        return position;
        //if(cursor!=null && cursor.getCount()>0)

    }

    public String getLastRecordCreatedAt()
    {
        String created_at = "";
        Cursor cursor = DBAdapter.getAllRecords(TABLE_NAME);
        if(cursor!= null && cursor.getCount() > 0) {
            cursor.moveToPosition(cursor.getCount() - 1);
            created_at = cursor.getString(cursor.getColumnIndex(CREATED_AT));
            return created_at;
        }
        else {
            return created_at;
        }

    }

    public void removeCheckerByReceiverId(int id)
    {
        DBAdapter.deleteCheckerReiver(TABLE_NAME, id+"");
    }

}
