package com.shailu.eteasytake;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shailu.eteasytake.View_Holder.Admin_Kitchen_Holder;
import com.shailu.eteasytake.model.Admin_Kitchen_Model;
import com.shailu.eteasytake.prevalent;

public class Admin_kitchen extends AppCompatActivity
{
    private RecyclerView kitchen_recycler_View;
    RecyclerView.LayoutManager kitchen_layout_Manager;
    private DatabaseReference kitchen_ref;
    private FirebaseDatabase database;
    private String time="";
    // private Button order_done_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_kitchen);

        Toolbar toolbar = findViewById(R.id.admin_ktn_toolbar);
        toolbar.setTitle("ORDERS");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        kitchen_recycler_View=findViewById(R.id.admin_kitchen_recycler);
        kitchen_recycler_View.setHasFixedSize(true);
        kitchen_layout_Manager=new LinearLayoutManager(this);
        kitchen_recycler_View.setLayoutManager(kitchen_layout_Manager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        final DatabaseReference  kitchen_ref = FirebaseDatabase.getInstance()
                .getReference().child("Cart View").child("Admin View")
                .child(prevalent.CurrentOnlineUser.getPhone());

        kitchen_ref.keepSynced(true);

        FirebaseRecyclerOptions<Admin_Kitchen_Model> options =
                new FirebaseRecyclerOptions.Builder<Admin_Kitchen_Model>()
                        .setQuery(kitchen_ref, Admin_Kitchen_Model.class)
                        .build();

        FirebaseRecyclerAdapter<Admin_Kitchen_Model, Admin_Kitchen_Holder> adapter =
                new FirebaseRecyclerAdapter<Admin_Kitchen_Model, Admin_Kitchen_Holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull Admin_Kitchen_Holder admin_kitchen_holder,
                                                    int i, @NonNull final Admin_Kitchen_Model admin_kitchen_model)
                    {
                        admin_kitchen_holder.kitchen_item_name.setText(admin_kitchen_model.getItem_Name());
                        admin_kitchen_holder.kitchen_quntity.setText(admin_kitchen_model.getQuantity());
                        admin_kitchen_holder.kitchen_table_no.setText(admin_kitchen_model.getTable_No());
                        admin_kitchen_holder.done.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View v)
                            {

                                time=admin_kitchen_model.getTime_of_Order();
                                kitchen_ref.child(time).removeValue();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public Admin_Kitchen_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_kitchen_view,parent,false);
                        Admin_Kitchen_Holder cartholder=new Admin_Kitchen_Holder(view);
                        return cartholder;
                    }
                };
        kitchen_recycler_View.setAdapter(adapter);
        adapter.startListening();
    }


}