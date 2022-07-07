package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

public class Admin_History_Holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{
    public TextView U_name;
    public TextView U_date;
    public TextView Total;
    public TextView t_no;
    public LinearLayout pay_type_hstry;
    public itemclicklistner listner;
    public Admin_History_Holder(@NonNull View itemView)
    {
        super(itemView);
        pay_type_hstry=(LinearLayout)itemView.findViewById(R.id.pay_type_ad_hstry);
        U_name = (TextView) itemView.findViewById(R.id.user_name_for_admin);
        U_date = (TextView) itemView.findViewById(R.id.date_admin_history);
        Total = (TextView) itemView.findViewById(R.id.total_admin_history);
        t_no = (TextView) itemView.findViewById(R.id.table_no_admin_hstry);
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
