package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shailu.eteasytake.View_Holder.Admin_menu_detail_holder;
import com.shailu.eteasytake.model.Common_Menu;
import com.shailu.eteasytake.prevalent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Admin_Menu_Activity extends AppCompatActivity
{
    private RecyclerView menurecyclerView;
    RecyclerView.LayoutManager menulayoutManager;
    private DatabaseReference adminmenuref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__menu);

        Toolbar toolbar = findViewById(R.id.admin_menu_toolbar);
        toolbar.setTitle("MENU");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        adminmenuref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(prevalent.CurrentOnlineUser.getPhone()).child("Menu");
        adminmenuref.keepSynced(true);

        menurecyclerView=findViewById(R.id.menu_recycler_view);
        menurecyclerView.setHasFixedSize(true);
        menulayoutManager=new LinearLayoutManager(this);
        menurecyclerView.setLayoutManager(menulayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.admin_add_menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
      if (id == R.id.add_menu_item1)
        {
            Intent intent= new Intent(Admin_Menu_Activity.this,Common_Menu_Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Common_Menu> options =
                new FirebaseRecyclerOptions.Builder<Common_Menu>()
                        .setQuery(adminmenuref, Common_Menu.class)
                        .build();
        FirebaseRecyclerAdapter<Common_Menu, Admin_menu_detail_holder> adapter =
                new FirebaseRecyclerAdapter<Common_Menu, Admin_menu_detail_holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final Admin_menu_detail_holder admin_menu_detail_holder, final int position,
                                                    @NonNull final Common_Menu common_menu)
                    {
                        admin_menu_detail_holder.item_name.setText(common_menu.getItemName());
                        admin_menu_detail_holder.show_price.setText(common_menu.getPrice());
                        final CharSequence itemid=admin_menu_detail_holder.item_name.getText();
                        Picasso.get().load(common_menu.getItemImage()).into(admin_menu_detail_holder.item_image_view);
                        admin_menu_detail_holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                adminmenuref.child(common_menu.getItemName())
                                        .removeValue();
                            }
                        });
                        admin_menu_detail_holder.layout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                        final AlertDialog.Builder alert1=new AlertDialog.Builder(Admin_Menu_Activity.this);
                                View myview1=getLayoutInflater().inflate(R.layout.add_price_dialog,null);
                                final EditText set_price_text=(EditText) myview1.findViewById(R.id.admin_add_price);
                                Button cancel=(Button)myview1.findViewById(R.id.cancel_btn_add_price);
                                Button add=(Button)myview1.findViewById(R.id.add_btn_add_price);

                                alert1.setView(myview1);
                                final AlertDialog alertDialog=alert1.create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                add.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        final String item_price=set_price_text.getText().toString();
                                        if(TextUtils.isEmpty(item_price))
                                        {
                                            set_price_text.setError("Price is madenatery");
                                            set_price_text.requestFocus();
                                        }
                                        else
                                        {
                                            final DatabaseReference RootRef;
                                            RootRef = FirebaseDatabase.getInstance().getReference().child("Admins");
                                            HashMap<String, Object> userdatamap = new HashMap<>();
                                            userdatamap.put("Price", item_price);

                                            RootRef.child(prevalent.CurrentOnlineUser.getPhone()).child("Menu").child(String.valueOf(itemid)).updateChildren(userdatamap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(Admin_Menu_Activity.this, "Sucssesful", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(Admin_Menu_Activity.this, "Network error..Please try again!!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
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
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_menu_detail_layout,parent,
                                false);
                        Admin_menu_detail_holder holder=new Admin_menu_detail_holder(view);
                        return holder;
                    }
                };
        menurecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
