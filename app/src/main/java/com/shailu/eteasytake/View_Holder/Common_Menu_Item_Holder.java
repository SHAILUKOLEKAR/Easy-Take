package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

public class Common_Menu_Item_Holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{


    public TextView item_name;
    public itemclicklistner listner;

    public Common_Menu_Item_Holder(@NonNull View itemView)
    {
        super(itemView);
        item_name=(TextView)itemView.findViewById(R.id.item_name);

    }

    @Override
    public void onClick(View view)
    {
        listner.OnClick(view, getAdapterPosition(),false);
    }
}
