package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.R;

public class CategoryTwoViewHolder extends RecyclerView.ViewHolder  {
    public TextView total_bill;
    public TextView user_name;
    public LinearLayout layout;

    public CategoryTwoViewHolder(@NonNull View itemView) {
        super(itemView);
        total_bill=itemView.findViewById(R.id.total_billing);
        user_name=itemView.findViewById(R.id.user_name_billing);
        layout=itemView.findViewById(R.id.item_vw_linear_layout);
    }
}
