package com.shailu.eteasytake;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shailu.eteasytake.model.Users;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{
    CatLoadingView loadingbar;

    private Button loginbutton;
    private EditText password, mobileno;
    private TextView Admin,Notadmin,Forrget;
    private String parentdbname = "Users";
    private CheckBox remembermecbx;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbutton = (Button)findViewById(R.id.login_btn);
        password = (EditText)findViewById(R.id.login_pass_input);
        mobileno = (EditText)findViewById(R.id.login_mono_input);
        Admin = (TextView)findViewById(R.id.admin);
        Notadmin = (TextView)findViewById(R.id.not_admin);
        Forrget=(TextView)findViewById(R.id.forgot_pass);
        Notadmin.setVisibility(View.INVISIBLE);
       // loadingbar=new ProgressDialog(this);
        loadingbar = new CatLoadingView();

        Forrget.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(LoginActivity.this, "Contact To SHAILESH:\u2605:96 89 86 49 36:\u2605",
                        Toast.LENGTH_LONG).show();
            }
        });

        remembermecbx= (CheckBox) (findViewById(R.id.checkBox));
        Paper.init(this);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        });
        Admin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                loginbutton.setText(getString(R.string.login_admin));
                Admin.setVisibility(View.INVISIBLE);
                Notadmin.setVisibility(View.VISIBLE);
                parentdbname="Admins";
            }
        });
        Notadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginbutton.setText(getString(R.string.login));
                Admin.setVisibility(View.VISIBLE);
                Notadmin.setVisibility(View.INVISIBLE);
                parentdbname="Users";
            }
        });
    }
    private void loginuser() {
        String phone= mobileno.getText().toString();
        String pass1=password.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            mobileno.setError("Write Your Phone...");
            mobileno.requestFocus();
        }
        else if(!phone.matches("[0-9]+")||(phone.length()!=10))
        {
            mobileno.setError("Enter Correct Mo. No.");
            mobileno.requestFocus();
        }
        else if (TextUtils.isEmpty(pass1))
        {
            password.setError("Write Your Password...");
            password.requestFocus();
        }
        else
        {
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show(getSupportFragmentManager(), "");

            AllowAccesetoaccount(phone,pass1);
        }
    }
    private void AllowAccesetoaccount(final String phone,final String pass1)
    {
        if(remembermecbx.isChecked())
        {
            Paper.book().write(prevalent.Parentdbname,parentdbname);
            Paper.book().write(prevalent.UserPhonekey,phone);
            Paper.book().write(prevalent.UserPasswordkey,pass1);
        }
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(parentdbname).child(phone).exists()){
            Users userdata=dataSnapshot.child(parentdbname).child(phone).getValue(Users.class);
            if(userdata.getPhone().equals(phone))
            {
            if(userdata.getPassword().equals(pass1))
            {
            if(parentdbname.equals("Admins"))
            {
                Toast.makeText(LoginActivity.this, "WELCOME ADMIN...LOGGED in successefully...!!!", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                prevalent.CurrentOnlineUser=userdata;
                Go_to_Admin_page();
            }
            else if (parentdbname.equals("Users"))
            {
                Toast.makeText(LoginActivity.this, "LOGGED in successefully...!!!", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                prevalent.CurrentOnlineUser=userdata;
                Go_to_User_page();
            }
            }
            else
            {
                loadingbar.dismiss();
                Toast.makeText(LoginActivity.this, "Password is INCORRECT..!Enter correct password..", Toast.LENGTH_SHORT).show();
            }
            }
            }
            else
            {
                loadingbar.dismiss();
                Toast.makeText(LoginActivity.this, "Account with thise"
                        +phone+ "do not exists..", Toast.LENGTH_SHORT).show();
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
        Intent intent=new Intent(LoginActivity.this, UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void Go_to_Admin_page()
    {
        Intent admin_intent = new Intent(LoginActivity.this,AdminActivity.class);
        admin_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(admin_intent);
    }
}