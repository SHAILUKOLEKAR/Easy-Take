package com.shailu.eteasytake;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shailu.eteasytake.View_Holder.User_history_holder;
import com.shailu.eteasytake.model.User_history_model;


public class User_History extends AppCompatActivity  {

    private RecyclerView history_recycler_view;
    RecyclerView.LayoutManager history_layout_Manager;
    private FirebaseDatabase databasdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__history);

        Toolbar toolbar = findViewById(R.id.user_hstry_toolbar);
        toolbar.setTitle("HISTORY");
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
        history_recycler_view = findViewById(R.id.user_history_recycler);
        history_recycler_view.setHasFixedSize(true);
        history_layout_Manager = new GridLayoutManager(this,2);
        history_recycler_view.setLayoutManager(history_layout_Manager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        final DatabaseReference user_H_ref = FirebaseDatabase.getInstance().getReference().child("History").
                child("User History").child(prevalent.CurrentOnlineUser.getPhone());
        user_H_ref.keepSynced(true);

        FirebaseRecyclerOptions<User_history_model> options =
                new FirebaseRecyclerOptions.Builder<User_history_model>()
                        .setQuery(user_H_ref, User_history_model.class)
                        .build();

        FirebaseRecyclerAdapter<User_history_model, User_history_holder> adapter =
                new FirebaseRecyclerAdapter<User_history_model, User_history_holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull User_history_holder user_history_holder, int position,
                                                    @NonNull User_history_model user_history_model)
                    {
                        user_history_holder.H_hotelname.setText(user_history_model.getHotel_name());
                        user_history_holder.H_date.setText(user_history_model.getTime_of_Order());
                        user_history_holder.H_total.setText(user_history_model.getTotal());
                    }

                    @NonNull
                    @Override
                    public User_history_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history_item_view, parent,false);
                        User_history_holder holder = new User_history_holder(view);
                        return holder;
                    }
                };
        history_recycler_view.setAdapter(adapter);
        adapter.startListening();
    }
}