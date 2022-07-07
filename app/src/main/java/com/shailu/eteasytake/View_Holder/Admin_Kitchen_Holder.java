package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

public class Admin_Kitchen_Holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{
    public TextView kitchen_table_no;
    public TextView kitchen_quntity;
    public TextView kitchen_item_name;
    public Button done;
    public itemclicklistner listner;

    public Admin_Kitchen_Holder(@NonNull View itemView)
    {
        super(itemView);
        kitchen_table_no=(TextView)itemView.findViewById(R.id.table_no_ktn_view);
        kitchen_quntity=(TextView)itemView.findViewById(R.id.quantity_of_item_ktn);
        kitchen_item_name=(TextView)itemView.findViewById(R.id.item_name_ktn);
        done=(Button)itemView.findViewById(R.id.done_by_kitchen);
    }

    @Override
    public void onClick(View v)
    {
        listner.OnClick(v, getAdapterPosition(),false);
    }
}
