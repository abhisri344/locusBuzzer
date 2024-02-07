package com.myapp.locusbuzzer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.locusbuzzer.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DatabaseAdapter.OnTaskListener, DatabaseAdapter.OnSwitchListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView recyclerview;

    ArrayList<Task_Model> tasks = new ArrayList<>();
    Database database = new Database(this);
    boolean res = false;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(! Application.notificationIds.isEmpty()) {
            int id = Application.notificationIds.pop();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            database.disable(String.valueOf(id));
            notificationManager.cancel(id);
        }


//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Intent intent = getIntent();
//
//        int id = intent.getIntExtra("Noti_ID",-1);
//        database.disable(String.valueOf(id));
//        notificationManager.cancel(id);
//        String msg = intent.getStringExtra("NotificationMessage");
//        Toast.makeText(this, "Id -"+id + msg, Toast.LENGTH_SHORT).show();

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLocationServiceRunning())
                    startLocationService();

                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        recyclerview =(RecyclerView)  findViewById(R.id.new_rec_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = new Database(this).getTasks();

        while(cursor.moveToNext()){
            Task_Model task = new Task_Model(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),  cursor.getString(7) );
            tasks.add(task);
        }

        System.out.println(tasks);
        DatabaseAdapter adapter = new DatabaseAdapter(tasks,this,this);
        recyclerview.setAdapter(adapter);
        recyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        res = isLocationServiceRunning();

    }

    @Override
    public void onTaskClick(int position) {
        if(!isLocationServiceRunning())
            startLocationService();

        Intent intent = new Intent(this,UpdateTaskActivity.class);
        intent.putExtra("Task",tasks.get(position));
        startActivity(intent );
    }

    @Override
    public void onSwitchClick(int id_num, String status) {
//        Toast.makeText(this, id_num+"Switch " +status, Toast.LENGTH_SHORT).show();
        String id = String.valueOf(id_num);
        Boolean result = database.updateStatus(id, status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locus_menu, menu);

        MenuItem itemswitch = menu.findItem(R.id.location_switch);
        itemswitch.setActionView(R.layout.location_switch);

        final Switch sw = (Switch) menu.findItem(R.id.location_switch).getActionView().findViewById(R.id.switch2);
        if(res){
            sw.setChecked(true);
//            myloc.setVisible(true);
        }
        else{
            sw.setChecked(false);
//            myloc.setVisible(false);
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                    }else{
                        startLocationService();

                    }
                }else{
                    stopLocationService();
                }
            }
        });

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.locus_menu:
                if(!isLocationServiceRunning())
                    startLocationService();
                Intent intent = new Intent(MainActivity.this, MyLocation.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
//            System.out.println("------------------");
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}