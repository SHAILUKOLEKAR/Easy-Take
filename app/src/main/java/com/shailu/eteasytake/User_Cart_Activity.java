package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shailu.eteasytake.View_Holder.User_cart_holder;
import com.shailu.eteasytake.model.User_Cart_Model;
import com.shailu.eteasytake.prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class User_Cart_Activity extends AppCompatActivity
{
    private static final int UPI_PAYMENT =0 ;
    private int overall_total_price;
    private Button pay_btn;
    private String name="";
    private String upiId="";
    private String amount="";
    private String note="";
    private String hotelname="";
    private String hotelmono="";
    private String pay_type="";
    private String table_no1="";

    RecyclerView.LayoutManager user_cart_layoutManager;
    private RecyclerView user_cart_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__cart);

        Toolbar toolbar = findViewById(R.id.user_cart_toolbar);
        toolbar.setTitle("CART");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             onBackPressed();
            }
        });

        name = getIntent().getStringExtra("owner_name");
        upiId = getIntent().getStringExtra("upi_id");
        hotelname = getIntent().getStringExtra("hotel name");
        note="Pay for " +hotelname ;
        hotelmono= getIntent().getStringExtra("hotelmono");
        table_no1 = getIntent().getStringExtra("table_no");


        pay_btn=(Button)findViewById(R.id.user_pay_btn);
        pay_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder alert1=new AlertDialog.Builder(User_Cart_Activity.this);
                View myview1=getLayoutInflater().inflate(R.layout.user_pay_bill_dialog,null);

                final Button confirm=(Button) myview1.findViewById(R.id.done_btn_pay);
                Button cancel=(Button)myview1.findViewById(R.id.cancel_btn_pay);
                final RadioButton online=(RadioButton)myview1.findViewById(R.id.radio_online) ;
                final RadioButton offline=(RadioButton)myview1.findViewById(R.id.radio_offline) ;

                TextView total_cart_price=(TextView)myview1.findViewById(R.id.total_amount_to_pay_view);

                total_cart_price.setText(String.valueOf(overall_total_price));
                amount=String.valueOf(overall_total_price);

                alert1.setView(myview1);
                final AlertDialog alertDialog=alert1.create();
                alertDialog.setCanceledOnTouchOutside(false);

                confirm.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if((!online.isChecked())&&(!offline.isChecked()))
                        {
                            confirm.setError("Choose Any One Option...");
                          //  Toast.makeText(User_Cart_Activity.this, "Choose Any One Option", Toast.LENGTH_SHORT).show();
                        }
                        else if(online.isChecked())
                        {
                            Toast.makeText(User_Cart_Activity.this, "Online...", Toast.LENGTH_LONG).show();
                            payUsingUpi(amount, upiId, name, note);
                            alertDialog.dismiss();
                        }
                        else if(offline.isChecked())
                        {
                            String pay_status="not_done";
                            pay_type="offline";
                            Toast.makeText(User_Cart_Activity.this, "Pay On Counter..", Toast.LENGTH_LONG).show();
                            add_pay_type(pay_type,pay_status,amount);
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

                alertDialog.show();
            }
        });
        user_cart_recyclerView = findViewById(R.id.recycler_user_cart);
        user_cart_recyclerView.setHasFixedSize(true);
        user_cart_layoutManager=new LinearLayoutManager(this);
        user_cart_recyclerView.setLayoutManager(user_cart_layoutManager);
    }

    private void add_pay_type(String pay_type, String pay_status, String amount)
    {
        final HashMap<String, Object> payment_mode = new HashMap<>();
        final HashMap<String, Object> tbl_no = new HashMap<>();
        tbl_no.put("Table_No",table_no1);
        payment_mode.put("pay_mode",pay_type);
        payment_mode.put("pay_status",pay_status);
        payment_mode.put("user_table",table_no1);
        payment_mode.put("Total",amount);
        payment_mode.put("user_name",prevalent.CurrentOnlineUser.getName());
        payment_mode.put("user_id",prevalent.CurrentOnlineUser.getPhone());
       // payment_mode.put("payer_mo",prevalent.CurrentOnlineUser.getPhone());

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference()
                .child("Billing").child("Admin").child(hotelmono);
        RootRef.keepSynced(true);
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                RootRef.child(table_no1)
                        .updateChildren(tbl_no)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        RootRef.child(table_no1)
                                                .child("data")
                                                .child(prevalent.CurrentOnlineUser.getPhone())
                                                .updateChildren(payment_mode);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {

                                    }
                                });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void payUsingUpi(String amount, String upiId, String name, String note)
    {
        Uri uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if(null != chooser.resolveActivity(getPackageManager()))
        {
            startActivityForResult(chooser, UPI_PAYMENT);
        }
        else
            {
              Toast.makeText(User_Cart_Activity.this,"No UPI app found, please install one to continue",
                      Toast.LENGTH_SHORT).show();
             }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11))
                {
                    if (data != null)
                    {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    }
                    else
                        {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                        }
                }
                else
                    {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                    }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data)
    {
        if (isConnectionAvailable(User_Cart_Activity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++)
            {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2)
                {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase()))
                    {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase()
                            .equals("txnRef".toLowerCase()))
                    {
                        approvalRefNo = equalStr[1];
                    }
                }
                else
                {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success"))
            {
                String pay_status="done";
                pay_type="online";

                //Code to handle successful transaction here.
                Toast.makeText(User_Cart_Activity.this, "Transaction successful.", Toast.LENGTH_LONG).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);

                add_pay_type(pay_type,pay_status,amount);

                add_to_history(hotelmono,hotelname,amount,table_no1,pay_type);
                DatabaseReference user_cart_ref_delete = FirebaseDatabase.getInstance().getReference().child("Cart View").
                        child("User View").child(prevalent.CurrentOnlineUser.getPhone()).child(hotelmono);
                user_cart_ref_delete.keepSynced(true);
                user_cart_ref_delete.removeValue();

            }
            else if("Payment cancelled by user.".equals(paymentCancel))
            {
                String pay_status="not_done";
                pay_type="online";
                add_pay_type(pay_type,pay_status,amount);
                Toast.makeText(User_Cart_Activity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String pay_status="not_done";
                pay_type="online";
                add_pay_type(pay_type,pay_status,amount);
                Toast.makeText(User_Cart_Activity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(User_Cart_Activity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void add_to_history(final String hotelmono, final String hotelname, final String amount, final String table_no1, final String pay_type)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
        SimpleDateFormat htry = new SimpleDateFormat("EEE,dd MMM", Locale.getDefault());
        final String hstrytime = htry.format(new Date());
        final String currentDateandTime = sdf.format(new Date());

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference("History");
        RootRef.keepSynced(true);

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {


                final HashMap<String, Object> admindatamap = new HashMap<>();

                admindatamap.put("Time_of_Order",hstrytime);
                admindatamap.put("Total",String.valueOf(amount));
                admindatamap.put("Name",prevalent.CurrentOnlineUser.getName());
                admindatamap.put("table_no",table_no1);
                admindatamap.put("Phone",prevalent.CurrentOnlineUser.getPhone());
                admindatamap.put("pay_type",pay_type);

                final HashMap<String, Object> Admin_turn_over = new HashMap<>();
                final HashMap<String, Object> userdatamap = new HashMap<>();

                userdatamap.put("Time_of_Order",hstrytime);
                userdatamap.put("Hotel_name", hotelname);
                userdatamap.put("Total",String.valueOf(amount));

                RootRef. child("User History").child(prevalent.CurrentOnlineUser.getPhone()).child(currentDateandTime)
                        .updateChildren(userdatamap)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                RootRef. child("Admin History").child("Turnover").child(hotelmono)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if( dataSnapshot.child("TURN_OVER").exists())
                                                {  int TURNover_TOTAL=0;
                                                    String turno=dataSnapshot.child("TURN_OVER").getValue().toString();
                                                    TURNover_TOTAL= Integer.valueOf(amount) +Integer.valueOf(turno);
                                                    Admin_turn_over.put("TURN_OVER",TURNover_TOTAL);
                                                }
                                                else
                                                {
                                                    int TURNover_TOTAL=0;
                                                    TURNover_TOTAL=TURNover_TOTAL+ Integer.valueOf(amount);
                                                    Admin_turn_over.put("TURN_OVER",TURNover_TOTAL);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                RootRef. child("Admin History").child(hotelmono)//.child(prevalent.CurrentOnlineUser.getPhone())
                                        .child(currentDateandTime+prevalent.CurrentOnlineUser.getPhone())
                                        .updateChildren(admindatamap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                RootRef. child("Admin History").child("Turnover").child(hotelmono)
                                                        .updateChildren(Admin_turn_over);
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

    public static boolean isConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable())
            {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        final DatabaseReference user_cart_ref = FirebaseDatabase.getInstance().getReference().child("Cart View").
                child("User View").child(prevalent.CurrentOnlineUser.getPhone())
                .child(hotelmono);
        user_cart_ref.keepSynced(true);

        FirebaseRecyclerOptions<User_Cart_Model> options =
                new FirebaseRecyclerOptions.Builder<User_Cart_Model>()
                        .setQuery(user_cart_ref, User_Cart_Model.class)
                        .build();

        FirebaseRecyclerAdapter<User_Cart_Model, User_cart_holder> adapter =
                new FirebaseRecyclerAdapter<User_Cart_Model, User_cart_holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull User_cart_holder user_cart_holder, int i,
                                                    @NonNull User_Cart_Model user_cart_model)
                    {
                        user_cart_holder.item_name_cart.setText(user_cart_model.getItem_Name());
                        user_cart_holder.item_price_cart.setText(user_cart_model.getPrice());
                        user_cart_holder.item_count_cart.setText(user_cart_model.getQuantity());
                        user_cart_holder.total_amount_cart.setText(user_cart_model.getTotal());

                         final DatabaseReference user_cart_to_ref = FirebaseDatabase.getInstance().getReference().child("Cart View").
                                                child("User_cart_total").child(prevalent.CurrentOnlineUser.getPhone())
                                                .child(hotelmono);
                         user_cart_to_ref.keepSynced(true);
                                        user_cart_to_ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if(dataSnapshot.child("CART_TOTAL").exists())
                                                {
                                                   overall_total_price=Integer.valueOf(dataSnapshot.child("CART_TOTAL").getValue().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                    }

                    @NonNull
                    @Override
                    public User_cart_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cart_view,parent,false);
                        User_cart_holder cartholder=new User_cart_holder(view);
                        return cartholder;
                    }
                };
        user_cart_recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

}