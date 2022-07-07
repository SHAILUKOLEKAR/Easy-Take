package com.shailu.eteasytake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

public class ScanCodeActivity extends AppCompatActivity
{
    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        codeScannerView=(CodeScannerView)findViewById(R.id.codeScannerView);
        codeScanner= new CodeScanner(this,codeScannerView);

        codeScanner.setDecodeCallback(new DecodeCallback()
        {
            @Override
            public void onDecoded(@NonNull final Result result)
            {
                runOnUiThread(new Runnable()
                {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject obj = new JSONObject(result.getText());
                        //setting values to textviews
                        if (obj.has("Mo")&&obj.has("Tno"))
                        {
                            String mobile=obj.getString("Mo");
                            String table=obj.getString("Tno");
                            Toast.makeText(ScanCodeActivity.this, "Mo No: "+mobile+"::Table No:"+table, Toast.LENGTH_LONG).show();
                            Go_to_page_order(mobile,table);
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(ScanCodeActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
                });
            }
        });
        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    private void Go_to_page_order(String mobile, String table)
    {
        Intent intent=new Intent(ScanCodeActivity.this,UserOrderActivity.class);
        intent.putExtra("hotelid",mobile);
        intent.putExtra("TBL_NO",table);
        startActivity(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getPermission();
    }

    private void getPermission()
    {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response)
            {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response)
            {
                Toast.makeText(ScanCodeActivity.this, "Permission Require...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}