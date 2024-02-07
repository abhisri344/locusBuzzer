package com.myapp.locusbuzzer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocation extends AppCompatActivity  {
//implements LocationListener

    String Latitude ="";
    String Longitude ="";

    private TextView lati;
    private TextView longi;
    private Button btn;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("Data");
            setMyLocation(data);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

    }

    public class LocationReceiver extends ResultReceiver{

        public LocationReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode == RESULT_OK && resultData!= null){
//                double[] arr = resultData.getDoubleArray("Data");
                String res = resultData.getString("Data");
                Toast.makeText(MyLocation.this, res, Toast.LENGTH_SHORT).show();
                MyLocation.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMyLocation(res);
                    }
                });
            }
        }
    }

    private void sendMessage(String message)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(intent);
    }


    private void setMyLocation(String res){

        Latitude = res.substring(0,res.indexOf(" "));
        Longitude = res.substring(res.indexOf(" ")+1);

        lati = findViewById(R.id.txt_latval);
        longi = findViewById(R.id.txt_longval);
        btn = findViewById(R.id.btn_share);
        lati.setText(String.valueOf(Latitude));
        longi.setText(String.valueOf(Longitude));
        String text = "http://maps.google.com/maps?daddr=" + Latitude + "," + Longitude;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(text);
            }
        });
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
