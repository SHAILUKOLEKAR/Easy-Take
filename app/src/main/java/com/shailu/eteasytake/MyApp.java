package com.shailu.eteasytake;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class MyApp extends Application {
    private static MyApp mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance=this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    public static synchronized MyApp getInstance(){
        return mInstance;
    }

    public void setConnectivityListner(ConnectivityReciever.ConnectivityReceiverListner listner)
    {
        ConnectivityReciever.connectivityReceiverListner=listner;
    }
}