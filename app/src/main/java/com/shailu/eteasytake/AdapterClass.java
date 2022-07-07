package com.shailu.eteasytake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.model.Categery;
import com.shailu.eteasytake.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>
    {
        public RecyclerClickListner listner;
        ArrayList<Users> list;
        public AdapterClass(ArrayList<Users>list,RecyclerClickListner listner)
        {
            this.list=list;
            this.listner=listner;
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_name_list,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
            holder.hotelname.setText(list.get(i).getHotel_Name());
            holder.villagename.setText(list.get(i).getVillage_Name());
            Picasso.get().load(list.get(i).getImage()).into(holder.hotelimage);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public interface RecyclerClickListner {
            void onClick(View v,int position);
        }


        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public CircleImageView hotelimage;
            public TextView hotelname , villagename;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                hotelimage=(CircleImageView) itemView.findViewById(R.id.list_hotel_img);
                hotelname=(TextView) itemView.findViewById(R.id.list_hotel_name);
                villagename=(TextView) itemView.findViewById(R.id.list_village_name);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                listner.onClick(view,getAdapterPosition());
            }
        }
    }