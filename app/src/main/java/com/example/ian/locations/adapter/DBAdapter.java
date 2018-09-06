package com.example.ian.locations.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ian.locations.model.CheckerReceiver;
import com.example.ian.locations.model.Receiver;
import com.example.ian.locations.model.User;

public class DBAdapter
{
    private static DBHelper helper;
    private static SQLiteDatabase db;
    static Cursor cursor;
    //wayne added
    private Context context;

    public DBAdapter(Context context) {
        // wayne added
        this.context = context;
        helper = new DBHelper(context);
    }

    public void test(){
        db = helper.getWritableDatabase();
    }

    public static void open(){
        db = helper.getWritableDatabase();
        //db.setForeignKeyConstraintsEnabled(true);
    }

    public static void close(){
        helper.close();
    }

    // used for all tables using generic inserting
    public static long add(String table, String nullColumns, ContentValues values){
        long ifInserted = db.insert(table, nullColumns, values);
        return ifInserted;
    }

    public static int getHighestID(String table) {
        final String MY_QUERY = "SELECT MAX(id) FROM " + table;
        Cursor cur = db.rawQuery(MY_QUERY, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    //get a single record from id
    public static Cursor getRecord(String tableName, String column, String value){
        String table = tableName;
        String[] columns = null;
        String selection = column + " = ? ";
        String[] selectionArgs= {value+""};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;

    }
    //get a single record from id
    public static Cursor getRecord2(String tableName, String column, String value){
        String table = tableName;
        String[] columns = null;
        String selection = column + " = ? ";
        String[] selectionArgs= {value+""};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if(cursor!=null && cursor.getCount()>0) {
            return  cursor;
        }
        return null;
    }

    //get a single record from id
    public static Cursor getRecordByUsername(String tableName, String username){
        String table = tableName;
        String[] columns = null;
        String selection = "username" + " = ? ";
        String[] selectionArgs= {username};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return  cursor;
    }

    public static Cursor getReceiverPhone(String id)
    {
        cursor = db.rawQuery("SELECT * FROM receivers WHERE TRIM(id) = '"+id.trim()+"'", null);
        return cursor;

    }

    public static Cursor searchUser(String password)
    {
        cursor = db.rawQuery("SELECT * FROM user WHERE TRIM(password) = '"+password.trim()+"'", null);
        return cursor;

    }

    public static Cursor getReceiverByUserId(String user_id)
    {
        cursor = db.rawQuery("SELECT * FROM receivers WHERE TRIM(user_id) = '"+user_id.trim()+"'", null);
        return cursor;
    }

    public static String getLastInsertId(String table)
    {
        String id = "";
        String[] columns = null;
        cursor = db.query(table, columns,null, null, null, null, null);
        cursor.moveToLast();
        if(cursor != null && cursor.getCount() > 0) {
            return cursor.getString(cursor.getColumnIndex("id"));
        }
        return id;
    }

    public static boolean checkPhone(String TABLE_USER, String COLUMN_USER_ID, String phone) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        //SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = "phone" + " = ?";

        // selection arguments
        String[] selectionArgs = {phone};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    //get All records
    public static Cursor getAllRecords(String tableName){
        String table = tableName;
        String[] columns = null;
        String selection = "";
        String[] selectionArgs= null;
        String groupBy = null;
        String having = null;
        String orderBy = "created_at";
        cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return  cursor;
    }

    public static void deleteRecord(String table, String id) {

        db.delete(table, "id = ?",
                new String[] { String.valueOf(id) });
    }

    public static void deleteCheckerReiver(String table, String id) {

        db.delete(table, "cursor_position = ?",
                new String[] { String.valueOf(id) });
    }


    static class  DBHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "location_database";
        //change version everytime schema is changed
        private static final int DATABASE_VERSION = 1;


        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
            Log.d("database","database constructor called");

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("database", "database created called");
            //create tables
            db.execSQL(User.CREATE_TABLE);
            db.execSQL(Receiver.CREATE_TABLE);
            db.execSQL(CheckerReceiver.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.d("database","on updateById called");
            db.execSQL(User.DROP_TABLE);
            db.execSQL(Receiver.DROP_TABLE);
            db.execSQL(CheckerReceiver.DROP_TABLE);
        }
    }
}
