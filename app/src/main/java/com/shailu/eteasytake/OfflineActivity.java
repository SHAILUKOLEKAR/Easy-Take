package com.shailu.eteasytake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OfflineActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListner{
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        button=findViewById(R.id.offline_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected)
        {
//            Intent intent=new Intent(OfflineActivity.this,MainActivity.class);
//            startActivity(intent);
            onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReciever connectivityReciever=new ConnectivityReciever();
        registerReceiver(connectivityReciever,intentFilter);
        MyApp.getInstance().setConnectivityListner(this);
    }
}