package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shailu.eteasytake.View_Holder.hotels_name_holder;
import com.shailu.eteasytake.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , ConnectivityReciever.ConnectivityReceiverListner
{
    private long backpressedtime;
    ArrayList<Users>list;
    private DatabaseReference hotellistref;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users, hotels_name_holder> adapter;
    private AdapterClass.RecyclerClickListner listner1;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        chechConnection();

        hotellistref =FirebaseDatabase.getInstance().getReference().child("Admins");
        hotellistref.keepSynced(true);

        if (hotellistref!=null)
        {
            hotellistref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        list=new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            list.add(ds.getValue(Users.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(UserActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close) {};
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0 );

        TextView usernametext = headerview.findViewById(R.id.user_name);
        usernametext.setText(prevalent.CurrentOnlineUser.getName());

        CircleImageView userprofile=(CircleImageView)headerview.findViewById(R.id.user_profile);
        userInfoDisplay(userprofile);

        recyclerView=findViewById(R.id.recycler_hotel_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showData();
    }

    private void showData()
    {
        options= new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(hotellistref,Users.class)
                .build();
        adapter= new FirebaseRecyclerAdapter<Users, hotels_name_holder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull hotels_name_holder hotels_name_holder, int position, @NonNull final Users users)
            {
                final String htl_tbl_no=users.getNO_OF_TABLEs();
                hotels_name_holder.hotelname.setText(users.getHotel_Name());
                hotels_name_holder.villagename.setText(users.getVillage_Name());
                Picasso.get().load(users.getImage()).into(hotels_name_holder.hotelimage);

                hotels_name_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        final AlertDialog.Builder alert2=new AlertDialog.Builder(UserActivity.this);
                        View addtableview=getLayoutInflater().inflate(R.layout.add_table_no_dialog,null);
                        Button add_table=(Button)addtableview.findViewById(R.id.add_btn_add_table);
                        Button cancel=(Button)addtableview.findViewById(R.id.cancel_btn_add_table);
                        final EditText tableno=(EditText)addtableview.findViewById(R.id.user_add_table_no);

                        alert2.setView(addtableview);
                        final AlertDialog alertDialog = alert2.create();

                        alertDialog.setCanceledOnTouchOutside(false);

                        add_table.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                final String table_no=tableno.getText().toString();
                                if(TextUtils.isEmpty(table_no))
                                {
                                    tableno.setError("Table No.is madenatery");
                                    tableno.requestFocus();
                                }
                                else if(Integer.valueOf(htl_tbl_no)<(Integer.valueOf(table_no)))
                                {
                                    tableno.setError("Enter Correct Table No.(upto): "+htl_tbl_no);
                                    tableno.requestFocus();
                                }
                                else
                                {
                                    Intent intent = new Intent(UserActivity.this,UserOrderActivity.class);
                                    intent.putExtra("hotelid",users.getPhone());
                                    intent.putExtra("hotel_name",users.getHotel_Name());
                                    intent.putExtra("TBL_NO",table_no);
                                    startActivity(intent);
                                    alertDialog.dismiss();
                                }
                                //alertDialog.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        alertDialog.show();

                    }
                });
            }
            @NonNull
            @Override
            public hotels_name_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_name_list,parent,false);
                hotels_name_holder holder=new hotels_name_holder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


   /*private void firebaseSearch(String searchText)
    {
        String query=searchText;
        Query firebaseSearchQuery=hotellistref.orderByChild("Hotel_Name").startAt(query)

                .endAt(query+"\uf8ff");
        options=new FirebaseRecyclerOptions.Builder<Users>().setQuery(firebaseSearchQuery,Users.class).build();

        adapter=new FirebaseRecyclerAdapter<Users, hotels_name_holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull hotels_name_holder hotels_name_holder, int i, @NonNull final Users users)
            {
                final String htl_tbl_no=users.getNO_OF_TABLEs();
                hotels_name_holder.hotelname.setText(users.getHotel_Name());
                hotels_name_holder.villagename.setText(users.getVillage_Name());
                Picasso.get().load(users.getImage()).into(hotels_name_holder.hotelimage);

                hotels_name_holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        final AlertDialog.Builder alert2=new AlertDialog.Builder(UserActivity.this);
                        View addtableview=getLayoutInflater().inflate(R.layout.add_table_no_dialog,null);
                        Button add_table=(Button)addtableview.findViewById(R.id.add_btn_add_table);
                        Button cancel=(Button)addtableview.findViewById(R.id.cancel_btn_add_table);
                        final EditText tableno=(EditText)addtableview.findViewById(R.id.user_add_table_no);

                        alert2.setView(addtableview);
                        final AlertDialog alertDialog = alert2.create();

                        alertDialog.setCanceledOnTouchOutside(false);

                        add_table.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                final String table_no=tableno.getText().toString();
                                if(TextUtils.isEmpty(table_no))
                                {
                                    tableno.setError("Table No.is madenatery");
                                    tableno.requestFocus();
                                }
                                else if(Integer.valueOf(htl_tbl_no)<(Integer.valueOf(table_no)))
                                {
                                    tableno.setError("Enter Correct Table No.(upto): "+htl_tbl_no);
                                    tableno.requestFocus();
                                }
                                else
                                {
                                    Intent intent = new Intent(UserActivity.this,UserOrderActivity.class);
                                    intent.putExtra("hotelid",users.getPhone());
                                    intent.putExtra("hotel_name",users.getHotel_Name());
                                    intent.putExtra("TBL_NO",table_no);
                                    startActivity(intent);
                                    alertDialog.dismiss();
                                }
                                //alertDialog.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        alertDialog.show();

                    }
                });
            }

            @NonNull
            @Override
            public hotels_name_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_name_list,parent,false);
                hotels_name_holder viewHolder=new hotels_name_holder(itemView);
                return viewHolder;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    */


    @Override
    protected void onStart()
    {
        super.onStart();
        if (adapter!=null)
        {
            adapter.startListening();
        }

    }

    @Override
    public void onBackPressed ()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return true;
    }

    private void firebaseSearch(String s) {
        ArrayList<Users>mylist=new ArrayList<>();
        for (Users object : list)
        {
            if (object.getHotel_Name().contains(s)||object.getHotel_Name().toLowerCase().contains(s)
                    ||object.getHotel_Name().toUpperCase().contains(s))
            {
                mylist.add(object);
                setOnClick(mylist);
            }
        }
        AdapterClass adapterClass=new AdapterClass(mylist,listner1);
        adapterClass.notifyDataSetChanged();
        recyclerView.setAdapter(adapterClass);
    }

    private void setOnClick(final ArrayList<Users> mylist) {
        listner1=new AdapterClass.RecyclerClickListner() {
            @Override
            public void onClick(View v, final int position) {

                final AlertDialog.Builder alert2=new AlertDialog.Builder(UserActivity.this);
                View addtableview=getLayoutInflater().inflate(R.layout.add_table_no_dialog,null);
                Button add_table=(Button)addtableview.findViewById(R.id.add_btn_add_table);
                Button cancel=(Button)addtableview.findViewById(R.id.cancel_btn_add_table);
                final EditText tableno=(EditText)addtableview.findViewById(R.id.user_add_table_no);

                alert2.setView(addtableview);
                final AlertDialog alertDialog = alert2.create();

                alertDialog.setCanceledOnTouchOutside(false);

                add_table.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final String table_no=tableno.getText().toString();
                        if(TextUtils.isEmpty(table_no))
                        {
                            tableno.setError("Table No.is madenatery");
                            tableno.requestFocus();
                        }
                        else if(Integer.valueOf(mylist.get(position).getNO_OF_TABLEs())<(Integer.valueOf(table_no)))
                        {
                            tableno.setError("Enter Correct Table No.(upto): "+mylist.get(position).getNO_OF_TABLEs());
                            tableno.requestFocus();
                        }
                        else
                        {
                            Intent intent = new Intent(UserActivity.this,UserOrderActivity.class);
                            intent.putExtra("hotelid",mylist.get(position).getPhone());
                            intent.putExtra("hotel_name",mylist.get(position).getHotel_Name());
                            intent.putExtra("TBL_NO",table_no);
                            startActivity(intent);
                            alertDialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.find_hotels_by_location) {
            Toast.makeText(this, "Find nearby hotels of your location...", Toast.LENGTH_SHORT).show();

        }


        if (id==R.id.search)
        {
            Toast.makeText(this, "SEARCH HOTEL", Toast.LENGTH_SHORT).show();


        }


        if (id==R.id.scan_qr)
        {
            Intent intent=new Intent(UserActivity.this,ScanCodeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected (MenuItem item)
    {   int id = item.getItemId();
        if (id == R.id.nav_history)
        {
            Intent history= new Intent(UserActivity.this,User_History.class);
            startActivity(history);
        }
        else if (id == R.id.nav_settings)
        {
            Intent intent=new Intent(UserActivity.this, UsersettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout)
        {
            Toast.makeText(UserActivity.this, "LOGOUT SUCCESSFULLY..", Toast.LENGTH_SHORT).show();
            Paper.book().destroy();
            Intent intent=new Intent(UserActivity.this,LoginActivity.class);
            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void userInfoDisplay(final CircleImageView userprofile)
    {
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Users").child(prevalent.CurrentOnlineUser.getPhone());
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
                Toast.makeText(UserActivity.this, "Error...", Toast.LENGTH_SHORT).show();
            }
        });
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

/*
    public class hotels_name_holder extends RecyclerView.ViewHolder{
        public hotels_name_holder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
    }

    public void setDetails(CircleImageView img,String htlname,String vilgname)
        {
        hotelimage=(CircleImageView) mView.findViewById(R.id.list_hotel_img);
        hotelname=(TextView) mView.findViewById(R.id.list_hotel_name);
        villagename=(TextView) mView.findViewById(R.id.list_village_name);

        hotelname.setText(htlname);
        villagename.setText(vilgname);

        }

 */


}