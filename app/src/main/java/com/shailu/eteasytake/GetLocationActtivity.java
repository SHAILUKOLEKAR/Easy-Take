package com.shailu.eteasytake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocationActtivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;

    Button getlocationBtn;
    TextView showLocationTxt;
    private static final long UPDATE_IN_MILLI = 10000;
    private static final long FAST_UPDATE_IN_MILLI = 5000;
    private static final long CHECK_SETTINGS = 100;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest mlocationSettingsRequest;
    private LocationCallback mlocationCallback;
    private Location mCurrentlocation;
    private boolean mRequestionLoacationUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        init();

        getlocationBtn = findViewById(R.id.getLocation);
        showLocationTxt = findViewById(R.id.show_location);

        getlocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(GetLocationActtivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Log.e("AllowPermissions", "Allow");
                                mRequestionLoacationUpdate = true;
                                setLocation();

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Log.e("AllowPermissions", "Canceled");
                                openSettings();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

    }

    private void setLocation() {
        settingsClient.checkLocationSettings(mlocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(GetLocationActtivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(GetLocationActtivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mlocationCallback, Looper.myLooper());

                        if (mCurrentlocation!=null)
                        {
                            showLocationTxt.setText("Lat: "+ mCurrentlocation.getLatitude() + "Long: " + mCurrentlocation.getLongitude());
                        }
                    }
                })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int code=((ApiException)e).getStatusCode();

                switch (code)
                {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException re=(ResolvableApiException)e;
                        try {
                            re.startResolutionForResult(GetLocationActtivity.this, (int) CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(GetLocationActtivity.this, "Check your settings...", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void openSettings()
    {
        Intent i =new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package", BuildConfig.APPLICATION_ID,null);
        i.setData(uri);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void init() {
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        settingsClient=LocationServices.getSettingsClient(this);
        mlocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentlocation=locationResult.getLastLocation();
            }
        };

        mRequestionLoacationUpdate=false;
        locationRequest=new LocationRequest();
       // locationRequest.setInterval(UPDATE_IN_MILLI);
      //  locationRequest.setFastestInterval(FAST_UPDATE_IN_MILLI);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mlocationSettingsRequest=builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (resultCode)
        {
            case AppCompatActivity.RESULT_OK:
                Log.e("AllowPermissions","Allow");
                break;
            case Activity.RESULT_CANCELED:
                Log.e("AllowPermissions","Canceled");
                mRequestionLoacationUpdate=false;
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}