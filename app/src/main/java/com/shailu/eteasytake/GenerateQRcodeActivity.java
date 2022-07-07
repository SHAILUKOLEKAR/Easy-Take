package com.shailu.eteasytake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class GenerateQRcodeActivity extends AppCompatActivity
{
    private Button generate_btn;
    private TextView mo_no,tbl_no;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        Toolbar toolbar = findViewById(R.id.qr_toolbar);
        toolbar.setTitle("Generate QR Code");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mo_no=(TextView)findViewById(R.id.mobile_no);
        tbl_no=(TextView)findViewById(R.id.table_no);
        final String mo=prevalent.CurrentOnlineUser.getPhone();
        final String tn=prevalent.CurrentOnlineUser.getNO_OF_TABLEs();
        mo_no.setText(mo);
        tbl_no.setText(tn);
        generate_btn=(Button)findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mo.isEmpty())
                {
                    Toast.makeText(GenerateQRcodeActivity.this, "mo not found", Toast.LENGTH_SHORT).show();
                }
                if (tn.isEmpty())
                {
                    Toast.makeText(GenerateQRcodeActivity.this, "Tn not found", Toast.LENGTH_SHORT).show();
                }
                int n=Integer.valueOf(tn);
                for (int i=1;i<=n;i++)
                {
                    String Text ="{'Mo':'"+mo+"','Tno':'"+i+"'}";
                    MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                    BitMatrix bitMatrix= null;
                    try {
                        bitMatrix = multiFormatWriter.encode(Text, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                        Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                        //qr_view.setImageBitmap(bitmap);

                        //  BitmapDrawable image=(BitmapDrawable) qr_view.getDrawable();
                        File path= Environment.getExternalStorageDirectory();
                        File dir= new File(path.getAbsolutePath()+"/Easy Take_QR Codes/");
                        dir.mkdir();
                        File file=new File(dir,mo+"@"+i+".jpg");
                        OutputStream out=null;
                        try
                        {
                            out=new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                            out.flush();
                            out.close();
                            Toast.makeText(GenerateQRcodeActivity.this, "Done..Ckeck Easy Take folder..", Toast.LENGTH_LONG).show();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                            Toast.makeText(GenerateQRcodeActivity.this, "ERROR..."+e, Toast.LENGTH_SHORT).show();
                        }
                    } catch (WriterException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getPermission();
    }
    private void getPermission()
    {
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response)
            {

            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response)
            {
                Toast.makeText(GenerateQRcodeActivity.this, "Permission Require...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}