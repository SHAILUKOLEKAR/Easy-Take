package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.shailu.eteasytake.View_Holder.CategoryTwoViewHolder;
import com.shailu.eteasytake.View_Holder.CategoryViewHolder;
import com.shailu.eteasytake.View_Holder.hotels_name_holder;
import com.shailu.eteasytake.model.Categery;
import com.shailu.eteasytake.model.CategeryTwo;
import com.shailu.eteasytake.model.Users;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static android.graphics.Color.RED;
import static android.graphics.Color.parseColor;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReciever.ConnectivityReceiverListner
{
    private long backpressedtime;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    RecyclerView.LayoutManager manager;
    private DatabaseReference billing_ref;
    FirebaseRecyclerAdapter<Categery, CategoryViewHolder> adapter;
    private FirebaseRecyclerOptions<Categery> options;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        chechConnection();

        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        toolbar.setTitle("BILLING");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close) {};

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.admin_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0 );

        TextView usernametext = headerview.findViewById(R.id.user_name);
        usernametext.setText(prevalent.CurrentOnlineUser.getHotel_Name());

        CircleImageView userprofile=(CircleImageView)headerview.findViewById(R.id.user_profile);
        userInfoDisplay(userprofile);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        billing_ref = FirebaseDatabase.getInstance().getReference("Billing")
                .child("Admin").child(prevalent.CurrentOnlineUser.getPhone());
        billing_ref.keepSynced(true);

        showBillingInfo();
    }

    private void showBillingInfo()
    {
            super.onStart();
             options =      new FirebaseRecyclerOptions.Builder<Categery>()
                            .setQuery(billing_ref, Categery.class)
                            .build();

            FirebaseRecyclerAdapter<Categery, CategoryViewHolder>
                    adapter = new FirebaseRecyclerAdapter<Categery, CategoryViewHolder>(options)
            {
                @NonNull
                @Override
                public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                    View v1= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_one,parent,false);
                    return new CategoryViewHolder(v1);
                }

                @Override
                protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Categery categery)
                {
                    categoryViewHolder.Table_no.setText(categery.getTable_No());
                    //categoryViewHolder.category_recyclerView.setAdapter(adapter2);
                    final String Table=categery.getTable_No();
                    recyclerView2=categoryViewHolder.category_recyclerView;

                    childRV(Table,recyclerView2);
                }
            };
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.startListening();

    }

    private void childRV(final String Table, RecyclerView recyclerView2)
    {
       // this.recyclerView2 = findViewById(R.id.recycler_view1);
        FirebaseRecyclerOptions<CategeryTwo>
                option2 =new FirebaseRecyclerOptions.Builder<CategeryTwo>()
                .setQuery(billing_ref.child(Table).child("data"), CategeryTwo.class)
                .build();


        FirebaseRecyclerAdapter<CategeryTwo, CategoryTwoViewHolder>
                adapter2 = new FirebaseRecyclerAdapter<CategeryTwo, CategoryTwoViewHolder>(option2)
        {

            @NonNull
            @Override
            public CategoryTwoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v2= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_two,parent,false);
                return new CategoryTwoViewHolder(v2);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CategoryTwoViewHolder categoryTwoViewHolder, int i, @NonNull final CategeryTwo categeryTwo)
            {
                categoryTwoViewHolder.total_bill.setText(categeryTwo.getTotal()+".\u20B9");
                categoryTwoViewHolder.user_name.setText(categeryTwo.getUser_name());

                billing_ref.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final String username = categeryTwo.getUser_name();
                        final String usermono = categeryTwo.getUser_id();
                        final String tabl = categeryTwo.getUser_table();
                        final String total = categeryTwo.getTotal();

                        final String count=String.valueOf(dataSnapshot.child(tabl).child("data").getChildrenCount());

                        if (Integer.parseInt(count)==0)
                        {
                            billing_ref.child(tabl).removeValue();
                        }

                        if(dataSnapshot.child(Table).child("data").child(categeryTwo.getUser_id()).child("pay_mode").exists())
                        {
                            final String paymode=dataSnapshot.child(Table).child("data")
                                    .child(categeryTwo.getUser_id()).child("pay_mode").getValue().toString();
                            String paystatus=dataSnapshot.child(Table).child("data")
                                    .child(categeryTwo.getUser_id()).child("pay_status").getValue().toString();

                            if(paymode.equals("online"))
                            {
                                categoryTwoViewHolder.layout.setBackgroundColor(Color.parseColor("#EB3C81F8"));
                                if (paystatus.equals("done"))
                                {
                                    DatabaseReference user_cart_total_ref_delete = FirebaseDatabase.getInstance().getReference().child("Cart View").
                                            child("User_cart_total").child(categeryTwo.getUser_id()).child(prevalent.CurrentOnlineUser.getPhone());
                                    user_cart_total_ref_delete.keepSynced(true);
                                    user_cart_total_ref_delete.removeValue();

                                    new CountDownTimer(5000, 1000)
                                    {

                                        public void onTick(long millisUntilFinished)
                                        {
                                            //categoryTwoViewHolder.layout.setBackgroundColor(Color.parseColor("#15E61E"));
                                            //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                                        }

                                        public void onFinish() {
                                            billing_ref.child(Table).child("data").child(categeryTwo.getUser_id()).removeValue();
                                        }
                                    }.start();
                                }
                                else
                                {
                                    categoryTwoViewHolder.total_bill.setBackgroundColor(Color.parseColor("#FF3D00"));
                                    categoryTwoViewHolder.layout.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                                            builder.setTitle(categeryTwo.getTotal() + " \u20B9." + " RECIEVED From " + categeryTwo.getUser_name() + " ,as ONLINE");
                                            builder.setMessage("  Press OK to DONE, If not-then press Cancel...");
                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {

                                                    add_to_history(paymode, usermono, username, tabl, total);

                                                    DatabaseReference user_cart_total_ref_delete = FirebaseDatabase.getInstance().getReference().child("Cart View").
                                                            child("User_cart_total").child(categeryTwo.getUser_id()).child(prevalent.CurrentOnlineUser.getPhone());
                                                    user_cart_total_ref_delete.keepSynced(true);
                                                    user_cart_total_ref_delete.removeValue();

                                                    DatabaseReference user_cart_ref_delete = FirebaseDatabase.getInstance()

                                                            .getReference().child("Cart View").
                                                                    child("User View").child(categeryTwo.getUser_id()).child(prevalent
                                                                    .CurrentOnlineUser.getPhone());
                                                    user_cart_ref_delete.removeValue();
                                                    billing_ref.child(Table).child("data").child(categeryTwo.getUser_id()).removeValue();
                                                }
                                            });
                                            AlertDialog alertDialog=builder.create();
                                            alertDialog.setCanceledOnTouchOutside(true);
                                            alertDialog.show();
                                        }
                                    });
                                    categoryTwoViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v)
                                        {
                                            userinfo(usermono,username,total);
                                            Toast.makeText(AdminActivity.this, "Info Of User..", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });
                                }
                            }

                            else if(paymode.equals("offline"))
                            {
                                if (paystatus.equals("not_done"))
                                {
                                    categoryTwoViewHolder.layout.setBackgroundColor(Color.parseColor("#FF3D00"));

                                    categoryTwoViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v)
                                        {
                                            userinfo(usermono,username,total);
                                            Toast.makeText(AdminActivity.this, "Info Of User..", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });

                                    categoryTwoViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                                            builder.setTitle(categeryTwo.getTotal()+" \u20B9."+" RECIEVED From "+categeryTwo.getUser_name());
                                            builder.setMessage("  Press OK to DONE, If not-then press Cancel...");
                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    String username=categeryTwo.getUser_name();
                                                    String usermono=categeryTwo.getUser_id();
                                                    String tabl=categeryTwo.getUser_table();
                                                    String total=categeryTwo.getTotal();
                                                    add_to_history(paymode,usermono,username,tabl,total);

                                                    DatabaseReference user_cart_total_ref_delete = FirebaseDatabase.getInstance().getReference().child("Cart View").
                                                            child("User_cart_total").child(categeryTwo.getUser_id()).child(prevalent.CurrentOnlineUser.getPhone());
                                                    user_cart_total_ref_delete.keepSynced(true);
                                                    user_cart_total_ref_delete.removeValue();

                                                    DatabaseReference user_cart_ref_delete = FirebaseDatabase.getInstance().getReference().child("Cart View").
                                                            child("User View").child(categeryTwo.getUser_id()).child(prevalent.CurrentOnlineUser.getPhone());
                                                    user_cart_ref_delete.keepSynced(true);
                                                    user_cart_ref_delete.removeValue();
                                                    billing_ref.child(Table) .child("data").child(categeryTwo.getUser_id()).removeValue();
                                                }
                                            });
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    AlertDialog alertDialog=builder.create();
                                                    alertDialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog=builder.create();
                                            alertDialog.setCanceledOnTouchOutside(true);
                                            alertDialog.show();
                                        }
                                    });
                                }
                            }
                        }
                        else {
                            categoryTwoViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                                    builder.setTitle("Are You Get " + categeryTwo.getTotal() + " \u20B9. By Other Way?");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            billing_ref.child(Table).child("data").child(categeryTwo.getUser_id()).removeValue();
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.dismiss();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setCanceledOnTouchOutside(true);
                                    alertDialog.show();
                                }
                            });
                            categoryTwoViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v)
                                {
                                    userinfo(usermono,username,total);
                                    Toast.makeText(AdminActivity.this, "Info Of User..", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

            }
        };
        adapter2.startListening();
        adapter2.notifyDataSetChanged();
        recyclerView2.setAdapter(adapter2);
    }

    private void add_to_history(final String paymode, final String usermono, final String username, final String tabl, final String total)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
        SimpleDateFormat htry = new SimpleDateFormat("EEE,dd MMM", Locale.getDefault());
        final String hstrytime = htry.format(new Date());
        final String currentDateandTime = sdf.format(new Date());

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("History");
        RootRef.keepSynced(true);
        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                final HashMap<String, Object> Admin_turn_over = new HashMap<>();
                RootRef. child("Admin History").child("Turnover").child(prevalent.CurrentOnlineUser.getPhone())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if( dataSnapshot.child("TURN_OVER").exists())
                                {  int TURNover_TOTAL=0;
                                    String turno=dataSnapshot.child("TURN_OVER").getValue().toString();
                                    TURNover_TOTAL= Integer.valueOf(total) +Integer.valueOf(turno);
                                    Admin_turn_over.put("TURN_OVER",TURNover_TOTAL);
                                }
                                else
                                {
                                    int TURNover_TOTAL=0;
                                    TURNover_TOTAL=TURNover_TOTAL+ Integer.valueOf(total);
                                    Admin_turn_over.put("TURN_OVER",TURNover_TOTAL);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                final HashMap<String, Object> admindatamap = new HashMap<>();

                admindatamap.put("Time_of_Order",hstrytime);
                admindatamap.put("Total",String.valueOf(total));
                admindatamap.put("Name",username);
                admindatamap.put("table_no",tabl);
                admindatamap.put("Phone",usermono);
                admindatamap.put("pay_type",paymode);

                RootRef. child("Admin History").child(prevalent.CurrentOnlineUser.getPhone())
                        //.child(prevalent.CurrentOnlineUser.getPhone())
                        .child(currentDateandTime+usermono)
                        .updateChildren(admindatamap)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                RootRef. child("Admin History").child("Turnover").child(prevalent.CurrentOnlineUser.getPhone())
                                .updateChildren(Admin_turn_over)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                final HashMap<String, Object> userdatamap = new HashMap<>();
                                userdatamap.put("Time_of_Order",hstrytime);
                                userdatamap.put("Hotel_name", prevalent.CurrentOnlineUser.getHotel_Name());
                                userdatamap.put("Total",String.valueOf(total));
                                RootRef. child("User History").child(usermono).child(currentDateandTime)
                                        .updateChildren(userdatamap);
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }

    @Override
    public void onBackPressed ()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        { drawer.closeDrawer(GravityCompat.START); }
        else
            {
                if(backpressedtime+1500>System.currentTimeMillis())
                {
                    super.onBackPressed();
                    return;
                }
                else {
                    Toast.makeText(this, "Press Back to exit", Toast.LENGTH_SHORT).show();
                }
                backpressedtime=System.currentTimeMillis();
            }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item=menu.findItem(R.id.search);

        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (!TextUtils.isEmpty(query.trim()))
                {
                    firebaseSearch(query);
                }
                else
                {
                    showBillingInfo();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (!TextUtils.isEmpty(newText.trim()))
                {
                    firebaseSearch(newText);
                }
                else
                {
                    showBillingInfo();
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.find_hotels_by_location)
        {
            Intent locAct=new Intent(this,GetLocationActtivity.class);
            startActivity(locAct);
            /*
            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("\u2605 EASY TAKE \u2605");
            builder.setMessage("For Any Information, Call To Team \u2605AIM\u2605.Contact:\u2605 96 89 86 49 36 \u2605." +
                    "All Rights are Reserved To Team \u2605AIM\u2605...");
            builder.setPositiveButton("GOT IT.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    AlertDialog alertDialog=builder.create();
                    alertDialog.dismiss();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

             */
        }
        if (id==R.id.search)
        {
            Toast.makeText(this, "Search...", Toast.LENGTH_SHORT).show();
        }

        if (id==R.id.scan_qr)
        {
            Intent intent=new Intent(this,GenerateQRcodeActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Generate QR code...", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void firebaseSearch(String s)
    {
        Query firebaseSearchqry=billing_ref.orderByChild("Table_No").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Categery>().setQuery(firebaseSearchqry,Categery.class).build();
        adapter = new FirebaseRecyclerAdapter<Categery, CategoryViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Categery categery)
            {
                categoryViewHolder.Table_no.setText(categery.getTable_No());
                final String Table=categery.getTable_No();
                recyclerView2=categoryViewHolder.category_recyclerView;

                childRV(Table,recyclerView2);
            }
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_one,parent,false);
                return new CategoryViewHolder(v1);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if (result.getContents()==null)
            {
                Toast.makeText(this, "you cancelled...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected (MenuItem item)
    {   int id = item.getItemId();
        if (id == R.id.nav_kitchen)
        {
            Intent intent=new Intent(AdminActivity.this,Admin_kitchen.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_admin_history)
        {
            Intent history= new Intent(AdminActivity.this,Admin_History.class);
            startActivity(history);
        }
        else if (id == R.id.nav_menu)
        {
            Intent intent=new Intent(AdminActivity.this,Admin_Menu_Activity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_admin_settings)
        {
            Intent intent=new Intent(AdminActivity.this,AdminSettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout)
        {
            Toast.makeText(AdminActivity.this, "LOGOUT SUCCESSFULLY..", Toast.LENGTH_SHORT).show();
            Paper.book().destroy();
            Intent intent=new Intent(AdminActivity.this,LoginActivity.class);
            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void userInfoDisplay(final CircleImageView userprofile)
    {
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(prevalent.CurrentOnlineUser.getPhone());
        userref.keepSynced(true);
        userref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.child("Image").exists())
                    {
                        String image = dataSnapshot.child("Image").getValue().toString();
                        Picasso.get().load(image).into(userprofile);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminActivity.this, "Error...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (adapter!=null)
        {
            adapter.startListening();
        }
    }

    private void userinfo(final String user_mono, String user_name, String user_total)
    {
        final AlertDialog.Builder alert=new AlertDialog.Builder(AdminActivity.this);
        View userinfo=getLayoutInflater().inflate(R.layout.view_user_detail_dialog,null);

        final CircleImageView user_img=(CircleImageView)userinfo.findViewById(R.id.user_profile_for_verify);
        TextView username=(TextView)userinfo.findViewById(R.id.user_name_for_verify);
        TextView usertotal=(TextView)userinfo.findViewById(R.id.user_total_for_verify);
        TextView usermono=(TextView)userinfo.findViewById(R.id.user_mono_for_verify);

        DatabaseReference aref=FirebaseDatabase.getInstance().getReference().child("Users");
        aref.keepSynced(true);
        aref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(user_mono).child("Image").exists())
                {
                    Picasso.get().load(dataSnapshot.child(user_mono).child("Image").getValue().toString()).into(user_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        username.setText(user_name);
        usermono.setText(user_mono);
        usertotal.setText(user_total+".\u20B9");

        Button done=(Button)userinfo.findViewById(R.id.done_btn_verify);

        alert.setView(userinfo);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected){
            ChangeAct();
        }
       // showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReciever connectivityReciever=new ConnectivityReciever();
        registerReceiver(connectivityReciever,intentFilter);
        MyApp.getInstance().setConnectivityListner(this);

      //  Toast.makeText(this, "Shailu....", Toast.LENGTH_SHORT).show();

    }

    private void ChangeAct() {
        Intent intent=new Intent(this,OfflineActivity.class);
        startActivity(intent);
    }

    private void chechConnection()
    {
        boolean isConnected=ConnectivityReciever.isConnected();
       // showSnack(isConnected);
        if (!isConnected){
            ChangeAct();
        }
    }
}