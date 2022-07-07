package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shailu.eteasytake.View_Holder.Admin_History_Holder;
import com.shailu.eteasytake.model.Admin_History_Model;

public class Admin_History extends AppCompatActivity
{
    private int daily_turnover;
    private RecyclerView admimhistory_recycler_view;
    RecyclerView.LayoutManager adminhistory_layout_Manager;
    private FirebaseDatabase databasdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__history);

        Toolbar toolbar = findViewById(R.id.htry_toolbar);
        toolbar.setTitle("Payment History");
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
        admimhistory_recycler_view = findViewById(R.id.admin_history_recycler);
        admimhistory_recycler_view.setHasFixedSize(true);
        adminhistory_layout_Manager = new GridLayoutManager(this,1);
        admimhistory_recycler_view.setLayoutManager(adminhistory_layout_Manager);

        FloatingActionButton turnover=(FloatingActionButton)findViewById(R.id.turnover);
        turnover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Admin_History.this);
                builder.setTitle("Your Todays INCOME Is: "+daily_turnover+" \u20B9.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    @Override
    protected void onStart()
    {
        super.onStart();
        final DatabaseReference admin_H_ref = FirebaseDatabase.getInstance().getReference().child("History").
                child("Admin History").child(prevalent.CurrentOnlineUser.getPhone());
        admin_H_ref.keepSynced(true);

        FirebaseRecyclerOptions<Admin_History_Model> options =
                new FirebaseRecyclerOptions.Builder<Admin_History_Model>()
                        .setQuery(admin_H_ref, Admin_History_Model.class)
                        .build();

        FirebaseRecyclerAdapter<Admin_History_Model, Admin_History_Holder> adapter =
                new FirebaseRecyclerAdapter<Admin_History_Model, Admin_History_Holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull Admin_History_Holder admin_history_holder, int position,
                                                    @NonNull Admin_History_Model admin_history_model)
                    {
                        admin_history_holder.U_name.setText(admin_history_model.getName());
                        admin_history_holder.U_date.setText(admin_history_model.getTime_of_Order());
                        admin_history_holder.Total.setText(admin_history_model.getTotal());
                        admin_history_holder.t_no.setText(admin_history_model.getTable_no());

                        final DatabaseReference admin_turn_over_ref = FirebaseDatabase.getInstance().getReference("History")
                        .child("Admin History").child("Turnover").child(prevalent.CurrentOnlineUser.getPhone());
                        admin_turn_over_ref.keepSynced(true);
                        admin_turn_over_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.child("TURN_OVER").exists())
                                {
                                    daily_turnover=Integer.valueOf(dataSnapshot.child("TURN_OVER").getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        String pay_type=admin_history_model.getPay_type();
                        if(pay_type.equals("online"))
                        {
                            admin_history_holder.pay_type_hstry.setBackgroundColor(Color.parseColor("#2962FF"));
                        }
                        else if(pay_type.equals("offline"))
                        {
                            admin_history_holder.pay_type_hstry.setBackgroundColor(Color.parseColor("#FF3D00"));
                        }
                    }

                    @NonNull
                    @Override
                    public Admin_History_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_history_view,
                                parent,false);
                        Admin_History_Holder holder = new Admin_History_Holder(view);
                        return holder;
                    }
                };
        admimhistory_recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
}