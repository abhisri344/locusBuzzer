package com.myapp.locusbuzzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DB_NAME = "Locus.db";
    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "Create table tasks ( id integer primary key autoincrement, title text, range integer, description text, latitude text, longitude text, isActive Text, distance text )";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("Drop table if exists tasks");
        onCreate(sqLiteDatabase);
    }

    public boolean addTask(String title, int range, String desc, String latitude, String longitude, double curr_lat, double curr_long){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("title",title);
        c.put("range",range);
        c.put("description",desc);
        c.put("latitude",latitude);
        c.put("longitude",longitude);
        c.put("isActive","yes");

        float results[] = new float[10];
        Location.distanceBetween(curr_lat, curr_long,Double.parseDouble(latitude),Double.parseDouble(longitude),results);

        c.put("distance",String.valueOf(results[0]));

        long result = db.insert("tasks",null, c);
        if(result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean deteteTask(String id) {

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor= db.rawQuery("select * from tasks where id=?", new String[]{id});

        if (cursor.getCount()>0) { long r = db.delete( "tasks", "id=?", new String[]{id});
            if (r == -1)
                return false;
            else
                return true;

        }

        else
            return false;

    }

    public boolean updateTask(String id, String title, int range, String desc, String latitude, String longitude, double curr_lat, double curr_long){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues c=new ContentValues();

        c.put("title",title);
        c.put("range",range);
        c.put("description",desc);
        c.put("latitude",latitude);
        c.put("longitude",longitude);
        c.put("isActive","yes");

        float results[] = new float[10];
        Location.distanceBetween(curr_lat, curr_long,Double.parseDouble(latitude),Double.parseDouble(longitude),results);

        c.put("distance",String.valueOf(results[0]));

        Cursor cursor= db.rawQuery( "select * from tasks where id=?", new String[]{id});
        if (cursor.getCount()>0) {
            long r= db.update( "tasks", c,"id=?", new String[]{id});
            if (r == -1)
                return false;
            else
                return true;
        }
        else
            return false;
    }

    public boolean updateStatus(String id, String isActive){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues c=new ContentValues();

        c.put("isActive",isActive);

        Cursor cursor= db.rawQuery( "select * from tasks where id=?", new String[]{id});
        if (cursor.getCount()>0) {
            long r= db.update( "tasks", c,"id=?", new String[]{id});
            if (r == -1)
                return false;
            else
                return true;
        }
        else
            return false;
    }

    public Cursor getTasks(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from tasks",null);
        return cursor;
    }

//    public Task_Model get

    public boolean disable(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues c=new ContentValues();

        c.put("isActive","No");

        Cursor cursor= db.rawQuery( "select * from tasks where id=?", new String[]{id});
        if (cursor.getCount()>0) {
            long r= db.update( "tasks", c,"id=?", new String[]{id});
            if (r == -1)
                return false;
            else
                return true;
        }
        else
            return false;
    }

}
