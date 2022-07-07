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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersettingsActivity extends AppCompatActivity
{
    CatLoadingView loadingbar;

    private CircleImageView profileimageview;
    private EditText changeusername, changepass;
    private EditText setting_user_village;
    private StorageTask uploadtask;
    public Uri imageUri;
    private TextView profilechangebtn;
    private String myUrl="";
    StorageReference storageprofilepictureref;
    private  FirebaseDatabase databasdatabase;
    private String checker="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.user_stng_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        loadingbar = new CatLoadingView();

        databasdatabase=FirebaseDatabase.getInstance();
        storageprofilepictureref = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileimageview = (CircleImageView) findViewById(R.id.setting_profile_image);
        changeusername = (EditText) findViewById(R.id.setting_change_user_name);
        changepass = (EditText) findViewById(R.id.setting_change_pass);
        setting_user_village=(EditText)findViewById(R.id.setting_user_village);
        profilechangebtn = (TextView) findViewById(R.id.profile_image_change_btn);

        userInfoDisplay(profileimageview, changeusername, changepass,setting_user_village);
        profilechangebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(UsersettingsActivity.this);
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

    private void updateOnlyuserInfo()
    {
        if(TextUtils.isEmpty(changeusername.getText().toString()))
        {
            changeusername.setError("The name is madenatory..");
            changeusername.requestFocus();
        }
        else if(TextUtils.isEmpty(changepass.getText().toString()))
        {
            changepass.setError("The password is madenatory..");
            changepass.requestFocus();
        }
        else if(TextUtils.isEmpty(setting_user_village.getText().toString()))
        {
            setting_user_village.setError("The village is madenatory..");
            setting_user_village.requestFocus();
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.keepSynced(true);

            HashMap<String,Object> usermap = new HashMap<>();
            usermap.put("Name",changeusername.getText().toString());
            usermap.put("Password",changepass.getText().toString());
            usermap.put("Village",setting_user_village.getText().toString());

            ref.child(prevalent.CurrentOnlineUser.getPhone()).updateChildren(usermap);
            startActivity(new Intent(UsersettingsActivity.this, UserActivity.class));
            Toast.makeText(UsersettingsActivity.this, "Info updated successfully...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void userInfoSaved()
    {
        if(TextUtils.isEmpty(changeusername.getText().toString()))
        {
            changeusername.setError("The name is madenatory..");
            changeusername.requestFocus();        }
        else if(TextUtils.isEmpty(changepass.getText().toString()))
        {
            changepass.setError("The password is madenatory..");
            changepass.requestFocus();
        }
        else if(TextUtils.isEmpty(setting_user_village.getText().toString()))
        {
            setting_user_village.setError("The village is madenatory..");
            setting_user_village.requestFocus();
        }
        else if(checker.equals("clicked"))
        {
            uploadimage();
        }
    }

    private void uploadimage()
    {
      // final ProgressDialog progressDialog= new ProgressDialog(this);
        loadingbar.show(getSupportFragmentManager(), "");
        loadingbar.setCanceledOnTouchOutside(false);

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

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> usermap = new HashMap<>();

                        usermap.put("Name",changeusername.getText().toString());
                        usermap.put("Password",changepass.getText().toString());
                        usermap.put("Image",myUrl);
                        usermap.put("Village",setting_user_village.getText().toString());

                        ref.child(prevalent.CurrentOnlineUser.getPhone()).updateChildren(usermap);
                        loadingbar.dismiss();
                        startActivity(new Intent(UsersettingsActivity.this, UserActivity.class));
                        Toast.makeText(UsersettingsActivity.this, "Info updated successfully...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        loadingbar.dismiss();
                        Toast.makeText(UsersettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Image is not Selected...", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(UsersettingsActivity.this, UsersettingsActivity.class));
            finish();
        }
    }

    private void userInfoDisplay(final CircleImageView profileimageview, final EditText changeusername,
                                 final EditText changepass, final EditText setting_user_village)
    {
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(prevalent.CurrentOnlineUser.getPhone());
        userref.keepSynced(true);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if( (dataSnapshot.child("Image").exists())&&(dataSnapshot.child("Village").exists()))
                    {
                        String image = dataSnapshot.child("Image").getValue().toString();
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String village = dataSnapshot.child("Village").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        Picasso.get().load(image).into(profileimageview);

                        changeusername.setText(name);
                        setting_user_village.setText(village);
                        changepass.setText(password);
                    }
                    else if(dataSnapshot.child("Image").exists())
                    {
                        String image = dataSnapshot.child("Image").getValue().toString();
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        Picasso.get().load(image).into(profileimageview);
                        changeusername.setText(name);
                        changepass.setText(password);
                    }
                    else if(dataSnapshot.child("Village").exists())
                    {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        String village = dataSnapshot.child("Village").getValue().toString();
                        changeusername.setText(name);
                        changepass.setText(password);
                        setting_user_village.setText(village);
                    }
                    else
                    {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String password = dataSnapshot.child("Password").getValue().toString();
                        changeusername.setText(name);
                        changepass.setText(password);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
}