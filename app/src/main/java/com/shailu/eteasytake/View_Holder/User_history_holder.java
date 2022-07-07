package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

public class User_history_holder extends RecyclerView.ViewHolder  implements View.OnClickListener {

    public TextView H_hotelname;
    public TextView H_total;
    public TextView H_date;
    public itemclicklistner listner;

    public User_history_holder(@NonNull View itemView)
    {
        super(itemView);
        H_hotelname = (TextView) itemView.findViewById(R.id.hotel_name_history);
        H_total= (TextView) itemView.findViewById(R.id.total_history);
        H_date=(TextView) itemView.findViewById(R.id.date_hidtory);
    }

    @Override
    public void onClick(View v)
    {
        listner.OnClick(v, getAdapterPosition(), false);
    }
    public void setItemClickedListner(itemclicklistner listner)
    {
        this.listner=listner;
    }
}
