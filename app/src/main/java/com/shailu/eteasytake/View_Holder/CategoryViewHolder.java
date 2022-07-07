package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder
{
    public TextView Table_no;
    public RecyclerView category_recyclerView;
    public RecyclerView.LayoutManager manager;
    public CategoryViewHolder(@NonNull View itemView)
    {
        super(itemView);
        manager=new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL,false);
        Table_no=itemView.findViewById(R.id.table_no_billing);
        category_recyclerView=itemView.findViewById(R.id.recycler_view1);
        category_recyclerView.setLayoutManager(manager);
    }
}
