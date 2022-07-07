package com.shailu.eteasytake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class SplashScreenActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListner
{
    private static int SPLASH_TIME=2000;
    ImageView image;
    TextView text1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        chechConnection();

        image=(ImageView)findViewById(R.id.logo_image);
        text1=(TextView) findViewById(R.id.logo_text1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME);
    }


    //all about network

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected){
            ChangeAct();
        }
        //showSnack(isConnected);
    }
//    private void showSnack(boolean isConnected)
//    {
//        String message;
//        int color;
//        if (isConnected)
//        {
//            message="You are Online";
//            color= Color.WHITE;
//        }
//        else {
//            message="You are Offline...";
//            color=Color.RED;
//        }
//
//        Snackbar snackbar=Snackbar.make(findViewById(R.id.RL),message,Snackbar.LENGTH_LONG)
//                .setActionTextColor(color);
//        // View view=snackbar.getView();
//        snackbar.show();
//
//    }
    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReciever connectivityReciever=new ConnectivityReciever();
        registerReceiver(connectivityReciever,intentFilter);
        MyApp.getInstance().setConnectivityListner(this);
    }
    private void ChangeAct()
    {
        Intent intent=new Intent(this,OfflineActivity.class);
        startActivity(intent);
    }
    private void chechConnection()
    {
        boolean isConnected=ConnectivityReciever.isConnected();
       // showSnack(isConnected);
        if (!isConnected)
        {
            ChangeAct();
        }
    }

}