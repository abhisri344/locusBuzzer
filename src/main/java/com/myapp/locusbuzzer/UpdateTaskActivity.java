package com.myapp.locusbuzzer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.android.gms.maps.model.LatLng;

public class UpdateTaskActivity extends AppCompatActivity{

    private Button btn_save;
    private EditText txt_title;
    private EditText txt_range;
    private EditText txt_desc;
    private EditText txt_latitude;
    private EditText txt_longitude;
    private Button btn_delete;
    private Button btn_location;
    Database database;
    Task_Model task;

    private LatLng location = null;
    private double curr_latitude=0;
    private double curr_longitude=0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("Data");

            curr_latitude = Double.parseDouble(data.substring(0,data.indexOf(" ")));
            curr_longitude = Double.parseDouble(data.substring(data.indexOf(" "), data.length()));

//            Toast.makeText(context, curr_latitude+" "+curr_longitude, Toast.LENGTH_SHORT).show();
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        Intent intent = getIntent();
        task = intent.getParcelableExtra("Task");



        btn_save = findViewById(R.id.save);
        txt_title = findViewById(R.id.edit_title);
        txt_range = findViewById(R.id.txt_range);
        txt_desc = findViewById(R.id.txt_taskdesc);
        txt_latitude = findViewById(R.id.edit_lati);
        txt_longitude = findViewById(R.id.edit_longi);
//        btn_delete = findViewById(R.id.btn_delete);
        btn_location = findViewById(R.id.btn_location);

        txt_title.setText(task.getTitle());
        txt_range.setText(String.valueOf(task.getRange()));
        txt_desc.setText(task.getDesc());
        txt_latitude.setText("Latitude "+String.valueOf(task.getLatitude()));
        txt_longitude.setText("Longitude "+String.valueOf(task.getLongitude()));

//        txt_location.setText(String.valueOf(task.getLatitude())+", "+String.valueOf(task.getLongitude()));

        database = new Database(this);


        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateTaskActivity.this, MapActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer range=0;

                String title = txt_title.getText().toString();
                if(!txt_range.getText().toString().equals("")) {
                    range = Integer.parseInt(txt_range.getText().toString());

                }String desc = txt_desc.getText().toString();

                if(title == null || title.equals("")){
                    Toast.makeText(UpdateTaskActivity.this, "Please enter the Task", Toast.LENGTH_SHORT).show();
                }else if(range == null || range < 20){
                    Toast.makeText(UpdateTaskActivity.this, "Please enter range greater than 20 m", Toast.LENGTH_SHORT).show();
                } else{

                    String lat = txt_latitude.getText().toString();
                    lat = lat.substring(lat.indexOf(" ")+1);
                    String longi = txt_longitude.getText().toString();
                    longi = longi.substring(longi.indexOf(" ")+1);

                    String id = String.valueOf(task.getId());
                    Boolean result = database.updateTask(id, title,range,desc, lat, longi, curr_latitude, curr_longitude);
                    if(result) {
                        Toast.makeText(UpdateTaskActivity.this, "Task Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent move = new Intent(UpdateTaskActivity.this, MainActivity.class);
                        startActivity(move);
                    }else{
                        Toast.makeText(UpdateTaskActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // st thomas 26.919161622027257, 80.95585832633073
        });

//        btn_delete.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id = String.valueOf(task.getId());
//                boolean del = database.deteteTask(id);
////                System.out.println("-----------------"+del);
//                if(del) {
////                    System.out.println("-----------------"+del+"---------");
//                    Toast.makeText(UpdateTaskActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                    Intent move = new Intent(UpdateTaskActivity.this, MainActivity.class);
//                    startActivity(move);
//                }else {
//                    Toast.makeText(UpdateTaskActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_task:
                String id = String.valueOf(task.getId());
                boolean del = database.deteteTask(id);
//                System.out.println("-----------------"+del);
                if(del) {
//                    System.out.println("-----------------"+del+"---------");
                    Toast.makeText(UpdateTaskActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    Intent move = new Intent(UpdateTaskActivity.this, MainActivity.class);
                    startActivity(move);
                }else {
                    Toast.makeText(UpdateTaskActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

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


                txt_latitude.setText("Latitude "+String.valueOf(latitude));
                txt_longitude.setText("Longitude "+String.valueOf(longitude));
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