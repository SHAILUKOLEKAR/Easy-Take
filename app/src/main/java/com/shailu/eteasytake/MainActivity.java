package com.shailu.eteasytake;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shailu.eteasytake.model.Users;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    CatLoadingView loadingbar;
   // private FirebaseUser currentuser;
   // private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button joinnowbutton = findViewById(R.id.main_join_now_btn);
        Button loginbutton = findViewById(R.id.main_login_btn);
        loadingbar = new CatLoadingView();
     //  loadingbar=new ProgressDialog(this);
        Paper.init(this);

        loginbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Go_to_login_page();
            }
        });

        joinnowbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Go_to_join_now_page();
            }
        });

        String UserPhoneKey = Paper.book().read(prevalent.UserPhonekey);
        String UserPasswordKey = Paper.book().read(prevalent.UserPasswordkey);
        String Parentdbname = Paper.book().read(prevalent.Parentdbname);
        if(UserPhoneKey!=("") && UserPasswordKey!=("")&&Parentdbname!=(""))

        if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
        {
            AllowAccess(UserPhoneKey,UserPasswordKey,Parentdbname);
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show(getSupportFragmentManager(), "");
            Toast.makeText(this, "\u25CFAlready LOGGED in ...!\u25CFPlease Wait...!", Toast.LENGTH_LONG).show();
        }

        ConnectivityManager connectivityManager =(ConnectivityManager)getApplicationContext().
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isConnected()||!networkInfo.isAvailable())
        {
            Toast.makeText(this, "INTERNET NOT AVAILABLE.Check Connection...", Toast.LENGTH_LONG).show();
        }
    }

    private void AllowAccess(final String phone, final String pass1, final String parentdbname)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.child(parentdbname).child(phone).exists())
            {
            Users userdata = dataSnapshot.child(parentdbname).child(phone).getValue(Users.class);
            if (userdata.getPhone().equals(phone))
            {
            if (userdata.getPassword().equals(pass1))
            {
            if (parentdbname.equals("Admins"))
            {
                Toast.makeText(MainActivity.this, "\u25CFWELCOME Admin..\u25CFLOGGED in Successfully!!!",Toast.LENGTH_LONG).show();
                loadingbar.dismiss();
                prevalent.CurrentOnlineUser = userdata;
                Go_to_Admin_page();
            }
            else if (parentdbname.equals("Users"))
            {
                Toast.makeText(MainActivity.this, "\u25CFWELCOME..\u25CFLOGGED in Successefully!!!", Toast.LENGTH_LONG).show();
                loadingbar.dismiss();
                prevalent.CurrentOnlineUser = userdata;
                Go_to_User_page();
            }
            }
            else
            {
                loadingbar.dismiss();
                Toast.makeText(MainActivity.this, "Password is INCORRECT..!Enter correct password..",
                        Toast.LENGTH_SHORT).show();
            }
            }
            } else
            { loadingbar.dismiss();
                Toast.makeText(MainActivity.this, "Account with thise: " + phone + " does not exists..",
                        Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void Go_to_User_page()
    {
        Intent user_intent = new Intent(MainActivity.this, UserActivity.class);
        user_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(user_intent);
    }

    private void Go_to_Admin_page()
    {
        Intent admin_intent = new Intent(MainActivity.this, AdminActivity.class);
        admin_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(admin_intent);
    }
    private void Go_to_join_now_page()
    {
        Intent intent = new Intent(MainActivity.this,join_now_Activity.class);
        startActivity(intent);
    }

    private void Go_to_login_page()
    {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}