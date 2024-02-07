package com.myapp.locusbuzzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

public class AddTaskActivity extends AppCompatActivity{

    private Button btn_save;
    private EditText txt_title;
    private EditText txt_range;
    private EditText txt_desc;
    private TextView txt_latitude;
    private TextView txt_longitude;
    private Button btn_loc;
    Database database;
    private LatLng location = null;
    private double curr_latitude=0;
    private double curr_longitude=0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("Data");

            curr_latitude = Double.parseDouble(data.substring(0,data.indexOf(" ")));
            curr_longitude = Double.parseDouble(data.substring(data.indexOf(" "), data.length()));

//            Toast.makeText(context, curr_latitude+"   "+curr_longitude, Toast.LENGTH_SHORT).show();
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        btn_save = findViewById(R.id.save);
        txt_title = findViewById(R.id.edit_title);
        txt_range = findViewById(R.id.txt_range);
        txt_desc = findViewById(R.id.txt_taskdesc);
        txt_latitude = findViewById(R.id.edit_lati);
        txt_longitude = findViewById(R.id.edit_longi);
        btn_loc = findViewById(R.id.btn_location);
//        btn_view = findViewById(R.id.btn_delete);

//        location.
//        String s = location.toString();



//        String[] latLng = s.substring(10, s.length() - 1).split(",");
//        String sLat = latLng[0];
//        String sLng = latLng[1];
//        Toast.makeText(this, "Latitude is: "+sLat+", Longtitude is: "+sLng, Toast.LENGTH_LONG).show();

        database = new Database(this);
//        SQLiteDatabase db = database.getReadableDatabase();

        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTaskActivity.this, MapActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

//        String loc = getIntent().getStringExtra("loc");
////        Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
        System.out.println(location);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer range=0;
//                Toast.makeText(AddTaskActivity.this, String.valueOf(location.latitude), Toast.LENGTH_SHORT).show();


                String title = txt_title.getText().toString();
                if(!txt_range.getText().toString().equals("")) {
                    range = Integer.parseInt(txt_range.getText().toString());

                }String desc = txt_desc.getText().toString();

                if(title == null || title.equals("")){
                    Toast.makeText(AddTaskActivity.this, "Please enter the Task", Toast.LENGTH_SHORT).show();
                }else if(range == null || range < 20){
                    Toast.makeText(AddTaskActivity.this, "Please enter range greater than 20 m", Toast.LENGTH_SHORT).show();
                }else if(location == null){
                    Toast.makeText(AddTaskActivity.this, "Please selection destination", Toast.LENGTH_SHORT).show();
                } else{
                    String latitude = String.valueOf(location.latitude);
                    String longitude = String.valueOf(location.longitude);

//                    txt_latitude.setText(String.valueOf(latitude));
//                    txt_longitude.setText(String.valueOf(longitude));
//                    txt_latitude.setVisibility(View.VISIBLE);
//                    txt_longitude.setVisibility(View.VISIBLE);

                    Boolean result = database.addTask(title,range,desc, latitude, longitude, curr_latitude, curr_longitude);
                    if(result) {
                        Toast.makeText(AddTaskActivity.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent move = new Intent(AddTaskActivity.this, MainActivity.class);
                        startActivity(move);
                    }else{
                        Toast.makeText(AddTaskActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            // st thomas 26.919161622027257, 80.95585832633073
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_tasks:
                Cursor t = database.getTasks();
                if (t.getCount() == 0){
                    Toast.makeText(AddTaskActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }

                StringBuffer s = new StringBuffer();
                while(t.moveToNext()){
                    s.append(t.getString(0)+"\n");
                    s.append(t.getString(1)+"\n");
                    s.append(t.getString(2)+"\n");
                    s.append(t.getString(3)+"\n");
                    s.append(t.getString(4)+"\n");
                    s.append(t.getString(5)+"\n");
                    s.append(t.getString(6)+"\n");
                    s.append(t.getString(7)+"\n\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddTaskActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Tasks");
                builder.setMessage(s.toString());
                builder.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                location = data.getParcelableExtra("loc");
//                Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();

                double latitude = location.latitude;
                double longitude = location.longitude;


                txt_latitude.setText("Latitude \n"+String.valueOf(latitude));
                txt_longitude.setText("Longitude \n"+String.valueOf(longitude));
                txt_latitude.setVisibility(View.VISIBLE);
                txt_longitude.setVisibility(View.VISIBLE);

//                Toast.makeText(this, latitude+" , "+longitude, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class LocationReceiver extends ResultReceiver {

        public LocationReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
//
//            if(resultCode == RESULT_OK && resultData!= null){
////                double[] arr = resultData.getDoubleArray("Data");
//                String data = resultData.getString("Data");
////                Toast.makeText(AddTaskActivity.this, data, Toast.LENGTH_SHORT).show();
//
////                curr_latitude = Double.parseDouble(data.substring(0,data.indexOf(" ")));
////            curr_longitude = Double.parseDouble(data.substring(data.indexOf(" "), data.length()));
//
////            Toast.makeText(getApplicationContext(), curr_latitude+" "+curr_longitude, Toast.LENGTH_SHORT).show();
//
//            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter("ServiceMessage"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }

}