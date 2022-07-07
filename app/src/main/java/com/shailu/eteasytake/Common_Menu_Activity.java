package com.shailu.eteasytake;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.shailu.eteasytake.View_Holder.Common_Menu_Item_Holder;
import com.shailu.eteasytake.model.Common_Menu;

import java.util.HashMap;

public class Common_Menu_Activity extends AppCompatActivity
{
    private RecyclerView menu_recycler_View;
    RecyclerView.LayoutManager menu_layout_Manager;
    private DatabaseReference common_menu_ref;
    private Button add_own_item_button;
    private  FirebaseDatabase databasdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common__menu);
        databasdatabase=FirebaseDatabase.getInstance();
        common_menu_ref = FirebaseDatabase.getInstance().getReference().child("CommonMenu");
        common_menu_ref.keepSynced(true);

        add_own_item_button=(Button)findViewById(R.id.add_own_menu_item);
        add_own_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                final AlertDialog.Builder alert=new AlertDialog.Builder(Common_Menu_Activity.this);
                View myview=getLayoutInflater().inflate(R.layout.add_own_item_dialog,null);

                final EditText enter_item=(EditText)myview.findViewById(R.id.add_own_item_text);
                Button done=(Button)myview.findViewById(R.id.done_btn_own_menu);
                Button cancel=(Button)myview.findViewById(R.id.cancel_btn_own_menu);
                alert.setView(myview);
                final AlertDialog alertDialog=alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String itemname=enter_item.getText().toString();
                        if(TextUtils.isEmpty(itemname))
                        {
                            Toast.makeText(Common_Menu_Activity.this, "item is madenatery", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            uploaditem(itemname);
                            alertDialog.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();


            }
        });
        menu_recycler_View = findViewById(R.id.common_menu_recycler_view);
        menu_recycler_View.setHasFixedSize(true);
        menu_layout_Manager = new GridLayoutManager(this,2);
        menu_recycler_View.setLayoutManager(menu_layout_Manager);
    }

    private void uploaditem(final String itemname)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);
        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("CommonMenu").child(itemname).exists()))
                {
                    HashMap<String, Object> userdatamap = new HashMap<>();
                    userdatamap.put("Name", itemname);

                    RootRef.child("CommonMenu"). child(itemname).updateChildren(userdatamap)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Common_Menu_Activity.this, "Succssesful", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Common_Menu_Activity.this, "Network error..Please try again!!", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Common_Menu> options =
                new FirebaseRecyclerOptions.Builder<Common_Menu>()
                        .setQuery(common_menu_ref, Common_Menu.class)
                        .build();

        FirebaseRecyclerAdapter<Common_Menu, Common_Menu_Item_Holder> adapter =
                new FirebaseRecyclerAdapter<Common_Menu, Common_Menu_Item_Holder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final Common_Menu_Item_Holder common_menu_item_holder, int position,
                                                    @NonNull final Common_Menu common_menu)
                    {

                        common_menu_item_holder.item_name.setText(common_menu.getName());
                        common_menu_item_holder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                final AlertDialog.Builder alert1=new AlertDialog.Builder(Common_Menu_Activity.this);
                                View myview1=getLayoutInflater().inflate(R.layout.add_menu_dialog_layout,null);
                                final EditText set_price_text=(EditText) myview1.findViewById(R.id.admin_add_price_common_menu);
                                Button add=(Button)myview1.findViewById(R.id.add_btn_menu);
                                Button cancel=(Button)myview1.findViewById(R.id.cancel_btn_menu);
                                alert1.setView(myview1);
                                final AlertDialog alertDialog=alert1.create();
                                alertDialog.setCanceledOnTouchOutside(false);

                              add.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v)
                                  {
                                      String item_toadmin_name=common_menu_item_holder.item_name.getText().toString();
                                      uplodietmname_to_admin(item_toadmin_name);

                                      final String item_price=set_price_text.getText().toString();
                                      if(TextUtils.isEmpty(item_price))
                                      {
                                          Toast.makeText(Common_Menu_Activity.this, "Price is madenatery", Toast.LENGTH_SHORT).show();
                                      }
                                      else
                                      {
                                          final DatabaseReference RootRef;
                                          RootRef = FirebaseDatabase.getInstance().getReference().child("Admins");
                                          RootRef.keepSynced(true);
                                          HashMap<String, Object> userdatamap = new HashMap<>();
                                          userdatamap.put("Price", item_price);

                                          RootRef.child(prevalent.CurrentOnlineUser.getPhone()).child("Menu").child(item_toadmin_name).updateChildren(userdatamap)
                                                  .addOnCompleteListener(new OnCompleteListener<Void>()
                                                  {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task)
                                                      {
                                                          if(task.isSuccessful())
                                                          {
                                                              Toast.makeText(Common_Menu_Activity.this, "Sucssesful", Toast.LENGTH_SHORT).show();
                                                          }
                                                          else
                                                          {
                                                              Toast.makeText(Common_Menu_Activity.this, "Network error..Please try again!!", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }
                                                  });
                                          alertDialog.dismiss();
                                      }
                                  }
                              });

                                cancel.setOnClickListener(new View.OnClickListener() {
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
                    public Common_Menu_Item_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_menu_layout, parent, false);
                        Common_Menu_Item_Holder holder = new Common_Menu_Item_Holder(view);
                        return holder;
                    }
                };
        menu_recycler_View.setAdapter(adapter);
        adapter.startListening();
    }

    private void uplodietmname_to_admin(final String item_toadmin_name)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Admins");
        ref.keepSynced(true);
        HashMap<String,Object> usermap = new HashMap<>();
        usermap.put("itemName",item_toadmin_name);

        ref.child(prevalent.CurrentOnlineUser.getPhone()).child("Menu").child(item_toadmin_name).updateChildren(usermap);
        Toast.makeText(this, item_toadmin_name  +"  sucssessful..", Toast.LENGTH_SHORT).show();

    }

}