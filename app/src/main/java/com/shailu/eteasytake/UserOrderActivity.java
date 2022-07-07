package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shailu.eteasytake.View_Holder.Admin_menu_detail_holder;
import com.shailu.eteasytake.model.Common_Menu;
import com.shailu.eteasytake.model.Users;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UserOrderActivity extends AppCompatActivity
{
    private TextView hotelname;
    private String upiad;
    private String htlname;
    private String table1="";
    private String ownername;
    private String hotelid = "";
  //  private int overall_total_price=0;

    private RecyclerView menurecyclerView;
    RecyclerView.LayoutManager menulayoutManager;
    private DatabaseReference adminmenuref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_order_activity);
        hotelid = getIntent().getStringExtra("hotelid");
        table1 = getIntent().getStringExtra("TBL_NO");

      //  hotelname=(TextView)findViewById(R.id.hotel_name);

        adminmenuref = FirebaseDatabase.getInstance().getReference().child("Admins").child(hotelid)
                .child("Menu");
        adminmenuref.keepSynced(true);

        menurecyclerView=findViewById(R.id.user_order_recycler_view);
        menurecyclerView.setHasFixedSize(true);
        menulayoutManager=new LinearLayoutManager(this);
        menurecyclerView.setLayoutManager(menulayoutManager);

        getTableNO(hotelid);
        Toolbar toolbar = findViewById(R.id.user_order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.user_cart);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intento=new Intent(UserOrderActivity.this,User_Cart_Activity.class);
                intento.putExtra("owner_name",ownername);
                intento.putExtra("upi_id",upiad);
                intento.putExtra("hotel name",htlname);
                intento.putExtra("hotelmono",hotelid);
                intento.putExtra("table_no",table1);
                startActivity(intento);
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Common_Menu> options =
                new FirebaseRecyclerOptions.Builder<Common_Menu>()
                        .setQuery(adminmenuref, Common_Menu.class)
                        .build();

        FirebaseRecyclerAdapter<Common_Menu, Admin_menu_detail_holder> adapter =
                new FirebaseRecyclerAdapter<Common_Menu, Admin_menu_detail_holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final Admin_menu_detail_holder admin_menu_detail_holder,
                                                    int position, @NonNull final Common_Menu common_menu)
                    {
                        admin_menu_detail_holder.usershow_price.setText(common_menu.getPrice());
                        admin_menu_detail_holder.useritem_name.setText(common_menu.getItemName());
                        Picasso.get().load(common_menu.getItemImage()).into(admin_menu_detail_holder.useritem_image_view);

                        admin_menu_detail_holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                final AlertDialog.Builder alert1=new AlertDialog.Builder(UserOrderActivity.this);
                                View myview1=getLayoutInflater().inflate(R.layout.user_add_order_dialog,null);

                                Button place_order=(Button)myview1.findViewById(R.id.done_btn_place_order);
                                Button cancel=(Button)myview1.findViewById(R.id.cancel_btn_place_order);

                                final TextView totalprice=(TextView)myview1.findViewById(R.id.total_price_of_item);
                               // final EditText usertableno=(EditText)myview1.findViewById(R.id.user_table_no_text);
                                //final String usertableno = table1;
                                table1 = getIntent().getStringExtra("TBL_NO");
                                totalprice.setText(common_menu.getPrice());
                                final int item_price=Integer.valueOf(common_menu.getPrice());
                                final ElegantNumberButton item_count= (ElegantNumberButton)myview1.findViewById(R.id.select_item_count);

                                final TextView selecteditemname=(TextView)myview1.findViewById(R.id.selected_item_name);
                                selecteditemname.setText(common_menu.getItemName());
                                alert1.setView(myview1);
                                final AlertDialog alertDialog=alert1.create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                place_order.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {       int totalprice_of_item= (Integer.parseInt(item_count.getNumber()))*(item_price);
                                    //*************ORDER IS SEND TO ADMIN***************
                                            send_order_to_admin(totalprice,item_count,selecteditemname,table1,totalprice_of_item);
                                            alertDialog.dismiss();
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
                                alertDialog.show();
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public Admin_menu_detail_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view_for_order,parent,false);
                        Admin_menu_detail_holder holder=new Admin_menu_detail_holder(view);
                        return holder;
                    }
                };
        menurecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void send_order_to_admin(final TextView totalprice, final ElegantNumberButton item_count, final TextView selecteditemname,
                                     final String table1, final int totalprice_of_item)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss a", Locale.getDefault());
        final String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat time = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss:SSS:", Locale.getDefault());
        final String admin_time_in_milisecond = time.format(new Date());

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                final HashMap<String, Object> adminbilling1 = new HashMap<>();
                DatabaseReference billing = FirebaseDatabase.getInstance()
                        .getReference().child("Billing").child("Admin").child(hotelid).child(table1)
                        .child("data").child(prevalent.CurrentOnlineUser.getPhone());
                billing.keepSynced(true);
                billing.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                    if (dataSnapshot.child("Total").exists())
                    {
                        int overall_total_price1=0;
                        String Toal1=dataSnapshot.child("Total").getValue().toString();
                        overall_total_price1=Integer.parseInt(Toal1)+totalprice_of_item;
                        adminbilling1.put("Total", String.valueOf(overall_total_price1));
                        adminbilling1.put("user_id",prevalent.CurrentOnlineUser.getPhone());
                        adminbilling1.put("user_name",prevalent.CurrentOnlineUser.getName());
                        adminbilling1.put("user_table",table1);
                    }

                    else
                    {
                        int overall_total_price1=0;
                        overall_total_price1=overall_total_price1+totalprice_of_item;
                        adminbilling1.put("Total", String.valueOf(overall_total_price1));
                        adminbilling1.put("user_id",prevalent.CurrentOnlineUser.getPhone());
                        adminbilling1.put("user_name",prevalent.CurrentOnlineUser.getName());
                        adminbilling1.put("user_table",table1);
                    }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

                final HashMap<String, Object> user_cart_total = new HashMap<>();

                        RootRef.child("Cart View").child("User_cart_total").child(prevalent.CurrentOnlineUser.getPhone()).child(hotelid)
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if( dataSnapshot.child("CART_TOTAL").exists())
                              {  int overCART_TOTAL=0;
                              String cartt=dataSnapshot.child("CART_TOTAL").getValue().toString();
                                  overCART_TOTAL= totalprice_of_item +Integer.valueOf(cartt);
                                  user_cart_total.put("CART_TOTAL",overCART_TOTAL);
                              }
                              else
                              {
                                  int overCART_TOTAL=0;
                                  overCART_TOTAL=overCART_TOTAL+ totalprice_of_item;
                                  user_cart_total.put("CART_TOTAL",overCART_TOTAL);
                              }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                final HashMap<String, Object> user_cart_map = new HashMap<>();
                user_cart_map.put("Time_of_Order",currentDateandTime);
                user_cart_map.put("Price", totalprice.getText().toString());
                user_cart_map.put("Quantity",String.valueOf( item_count.getNumber()));
                user_cart_map.put("Item_Name", selecteditemname.getText().toString());
                user_cart_map.put("Table_No", table1);
                user_cart_map.put("Total", String.valueOf(totalprice_of_item));

                RootRef.child("Cart View").child("User View").child(prevalent.CurrentOnlineUser.getPhone())
                       // child("Products")
                        .child(hotelid).child(currentDateandTime)
                        .updateChildren(user_cart_map)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {  RootRef.child("Cart View").child("User_cart_total").child(prevalent.CurrentOnlineUser.getPhone())
                                        .child(hotelid)
                                        .updateChildren(user_cart_total);
                                    //              ************************
                                    final HashMap<String, Object> admin_cart_map = new HashMap<>();
                                    admin_cart_map.put("Time_of_Order",admin_time_in_milisecond+prevalent.CurrentOnlineUser.getPhone());
                                    admin_cart_map.put("Table_No", table1);
                                    admin_cart_map.put("Quantity",String.valueOf( item_count.getNumber()));
                                    admin_cart_map.put("item_price",totalprice.getText().toString());
                                    admin_cart_map.put("Item_Name", selecteditemname.getText().toString());

                                    RootRef.child("Cart View").child("Admin View").child(hotelid)
                                           // child(prevalent.CurrentOnlineUser.getPhone())
                                            .child(admin_time_in_milisecond+prevalent.CurrentOnlineUser.getPhone())
                                            .updateChildren(admin_cart_map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        final DatabaseReference admin_billing_ref;
                                                        admin_billing_ref = FirebaseDatabase.getInstance().getReference("Billing")
                                                        .child("Admin").child(hotelid).child(table1);
                                                        admin_billing_ref.keepSynced(true);

                                                        admin_billing_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                            {
                                                              if(dataSnapshot.child("Table_No").exists())
                                                              {
                                                                  admin_billing_ref.child("data")
                                                                  .child(prevalent.CurrentOnlineUser.getPhone())
                                                                  .updateChildren(adminbilling1)
                                                                  .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                  {
                                                                  @Override
                                                                  public void onComplete(@NonNull Task<Void> task)
                                                                  {
                                                                  if (task.isSuccessful())
                                                                  {
                                                                      Toast.makeText(UserOrderActivity.this,
                                                                              "Item Added Succssesfully...Visit to Cart...", Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                      {
                                                                      Toast.makeText(UserOrderActivity.this,
                                                                              "Network error..Please try again!!", Toast.LENGTH_SHORT).show();
                                                                  }
                                                                  }
                                                                  });
                                                              }

                                                              else
                                                              {
                                                                  final HashMap<String, Object> admin_billing_tabla_no = new HashMap<>();
                                                                  admin_billing_tabla_no.put("Table_No", table1);
                                                                  admin_billing_ref.updateChildren(admin_billing_tabla_no)
                                                                  .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                  {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<Void> task)
                                                                      { if (task.isSuccessful()) {
                                                                          admin_billing_ref.child("data")
                                                                          .child(prevalent.CurrentOnlineUser.getPhone())
                                                                          .updateChildren(adminbilling1)
                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if (task.isSuccessful()) {
                                                                                  Toast.makeText(UserOrderActivity.this, "Item Added Succssesfully...Visit to Cart...", Toast.LENGTH_LONG).show();
                                                                              } else {
                                                                                  Toast.makeText(UserOrderActivity.this, "Network error..Please try again!!", Toast.LENGTH_SHORT).show();
                                                                              }
                                                                              }
                                                                          });
                                                                      }
                                                                      }
                                                                  });
                                                              }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });



                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }


    private void getTableNO(String hotelid)
    {
        DatabaseReference tablenoref = FirebaseDatabase.getInstance().getReference().child("Admins");
        tablenoref.keepSynced(true);
        tablenoref.child(hotelid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    ownername=users.getOwner_Name();
                    upiad=users.getUPI();
                    htlname=users.getHotel_Name();
                    getSupportActionBar().setTitle(users.getHotel_Name());
                   // hotelname.setText(users.getHotel_Name());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
}