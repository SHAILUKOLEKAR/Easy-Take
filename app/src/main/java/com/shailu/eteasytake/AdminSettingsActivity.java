package com.shailu.eteasytake;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shailu.eteasytake.prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminSettingsActivity extends AppCompatActivity
{
    CatLoadingView loadingbar;

    private CircleImageView profileimageview;
    private EditText changeownername, changepass,hotelname,no_of_table,monoforpay,villagename;
    private TextView profilechangebtn;
    private StorageTask uploadtask;
    public Uri imageUri;
    private String myUrl="";
    StorageReference storageprofilepictureref;
    private  FirebaseDatabase databasdatabase;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        Toolbar toolbar = findViewById(R.id.admin_stng_toolbar);
        toolbar.setTitle("SETTINGS");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        databasdatabase=FirebaseDatabase.getInstance();
        storageprofilepictureref = FirebaseStorage.getInstance().getReference().child("admin_hotel_picture");

        profileimageview = (CircleImageView) findViewById(R.id.setting_admin_profile_image);
        changeownername = (EditText) findViewById(R.id.setting_change_owner_name);
        changepass = (EditText) findViewById(R.id.setting_admin_change_pass);
        profilechangebtn = (TextView) findViewById(R.id.profile_image_change_btn);
        villagename=(EditText) findViewById(R.id.setting_change_village);
        hotelname = (EditText) findViewById(R.id.setting_change_hotel_name);
        no_of_table = (EditText) findViewById(R.id.setting_change_taable_no);
        monoforpay = (EditText) findViewById(R.id.setting_change_payment_mono);

        loadingbar = new CatLoadingView();

        userInfoDisplay(profileimageview, changeownername, changepass,monoforpay,hotelname);

//        savebtn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if(checker.equals("clicked"))
//                {
//                    userInfoSaved();
//                }
//                else
//                {
//                    updateOnlyuserInfo();
//                }
//            }
//        });
        profilechangebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(AdminSettingsActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_settings)
        {
            if(checker.equals("clicked"))
            {
                userInfoSaved();
            }
            else
            {
                updateOnlyuserInfo();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void userInfoSaved()
    {
        if(TextUtils.isEmpty(changeownername.getText().toString()))
        {
            changeownername.setError("The name is madenatory..");
            changeownername.requestFocus();
        }
        else if(TextUtils.isEmpty(changepass.getText().toString()))
        {
            changepass.setError("The password is madenatory..");
            changepass.requestFocus();
        }
        else if(TextUtils.isEmpty(hotelname.getText().toString()))
        {
            hotelname.setError("The Hotel Name is madenatory..");
            hotelname.requestFocus();
        }
        else if(TextUtils.isEmpty(no_of_table.getText().toString()))
        {
            no_of_table.setError("The Table No is madenatory..");
            no_of_table.requestFocus();
        }
        else if(TextUtils.isEmpty(villagename.getText().toString()))
        {
            villagename.setError("The Village name is madenatory..");
            villagename.requestFocus();
        }
        else if(TextUtils.isEmpty(monoforpay.getText().toString()))
        {
            monoforpay.setError("The UPI add. is madenatory..");
            monoforpay.requestFocus();
        }
        else if(checker.equals("clicked"))
        {
            uploadimage();
        }
    }
    private void uploadimage()
    {
        loadingbar.show(getSupportFragmentManager(), "");
        loadingbar.setCanceledOnTouchOutside(false);
//        final ProgressDialog progressDialog= new ProgressDialog(this);
//        progressDialog.setTitle("update profile");
//        progressDialog.setMessage("plz wait ....checking account info,,");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
        if(imageUri!=null)
        {
            final StorageReference Ref=storageprofilepictureref.child(prevalent.CurrentOnlineUser.getPhone()+".jpg");
            uploadtask=Ref.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return Ref.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if(task.isSuccessful())
                            {
                                Uri downloadurl= task.getResult();

                                myUrl= Objects.requireNonNull(downloadurl).toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admins");
                                ref.keepSynced(true);

                                HashMap<String,Object> usermap = new HashMap<>();

                                usermap.put("Name",changeownername.getText().toString());
                                usermap.put("Password",changepass.getText().toString());
                                usermap.put("Hotel_Name",hotelname.getText().toString());
                                usermap.put("NO_OF_TABLEs",no_of_table.getText().toString());
                                usermap.put("Village_Name",villagename.getText().toString());
                                usermap.put("UPI",monoforpay.getText().toString());
                                usermap.put("Image",myUrl);

                                ref.child(prevalent.CurrentOnlineUser.getPhone()).updateChildren(usermap);
                                loadingbar.dismiss();
                                startActivity(new Intent(AdminSettingsActivity.this,MainActivity.class));
                                Toast.makeText(AdminSettingsActivity.this, "Info updated successfully...", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                loadingbar.dismiss();
                                Toast.makeText(AdminSettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Image is not Selected...", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateOnlyuserInfo()
    {
        if(TextUtils.isEmpty(changeownername.getText().toString()))
        {
            changeownername.setError("The name is madenatory..");
            changeownername.requestFocus();
        }
        else if(TextUtils.isEmpty(changepass.getText().toString()))
        {
            changepass.setError("The password is madenatory..");
            changepass.requestFocus();
        }
        else if(TextUtils.isEmpty(hotelname.getText().toString()))
        {
            hotelname.setError("The Hotel Name is madenatory..");
            hotelname.requestFocus();
        }
        else if(TextUtils.isEmpty(no_of_table.getText().toString()))
        {
            no_of_table.setError("The Table No is madenatory..");
            no_of_table.requestFocus();
        }
        else if(TextUtils.isEmpty(villagename.getText().toString()))
        {
            villagename.setError("The Village name is madenatory..");
            villagename.requestFocus();
        }
        else if(TextUtils.isEmpty(monoforpay.getText().toString()))
        {
            monoforpay.setError("The UPI add. is madenatory..");
            monoforpay.requestFocus();
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admins");
            ref.keepSynced(true);

            HashMap<String,Object> usermap = new HashMap<>();
            usermap.put("Name",changeownername.getText().toString());
            usermap.put("Password",changepass.getText().toString());
            usermap.put("Hotel_Name",hotelname.getText().toString());
            usermap.put("NO_OF_TABLEs",no_of_table.getText().toString());
            usermap.put("Village_Name",villagename.getText().toString());
            usermap.put("UPI",monoforpay.getText().toString());

            ref.child(prevalent.CurrentOnlineUser.getPhone()).updateChildren(usermap);
            startActivity(new Intent(AdminSettingsActivity.this,AdminActivity.class));
            Toast.makeText(AdminSettingsActivity.this, "Info updated successfully...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileimageview.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error try again...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminSettingsActivity.this,AdminActivity.class));
            finish();
        }
    }
    private void userInfoDisplay(final CircleImageView profileimageview, final EditText changeownername, final EditText changepass, final EditText monoforpay, final EditText hotelname)
    {

        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Admins").child(prevalent.CurrentOnlineUser.getPhone());
        userref.keepSynced(true);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.child("Image").exists())
                    {
                        String image = dataSnapshot.child("Image").getValue().toString();
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        String hotelnm = dataSnapshot.child("Hotel_Name").getValue().toString();
                        String tableno = dataSnapshot.child("NO_OF_TABLEs").getValue().toString();
                        String village = dataSnapshot.child("Village_Name").getValue().toString();
                        String payingupi = dataSnapshot.child("UPI").getValue().toString();

                        Picasso.get().load(image).into(profileimageview);
                        changeownername.setText(name);
                        changepass.setText(password);
                        hotelname.setText(hotelnm);
                        no_of_table.setText(tableno);
                        villagename.setText(village);
                        monoforpay.setText(payingupi);

                    }
                    else if(dataSnapshot.child("NO_OF_TABLEs").exists()&&dataSnapshot.child("Village_Name").exists())
                    {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        String hotelnm = dataSnapshot.child("Hotel_Name").getValue().toString();
                        String tableno = dataSnapshot.child("NO_OF_TABLEs").getValue().toString();
                        String village = dataSnapshot.child("Village_Name").getValue().toString();
                        String payingupi = dataSnapshot.child("UPI").getValue().toString();

                        changeownername.setText(name);
                        changepass.setText(password);
                        hotelname.setText(hotelnm);
                        no_of_table.setText(tableno);
                        villagename.setText(village);
                        monoforpay.setText(payingupi);
                    }
                    else
                    {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        String hotelnm = dataSnapshot.child("Hotel_Name").getValue().toString();

                        changeownername.setText(name);
                        changepass.setText(password);
                        hotelname.setText(hotelnm);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}