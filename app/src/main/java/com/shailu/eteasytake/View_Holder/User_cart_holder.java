package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_cart_holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{
    public TextView item_name_cart;
    public TextView item_price_cart;
    public TextView item_count_cart;
    public TextView total_amount_cart;
    public CircleImageView item_image_cart;

    public itemclicklistner listner;

    public User_cart_holder(@NonNull View itemView)
    {
        super(itemView);
        item_name_cart = (TextView) itemView.findViewById(R.id.item_name_user_cart_view);
        item_price_cart= (TextView) itemView.findViewById(R.id.price_of_item_user_cart_view);
        item_count_cart=(TextView) itemView.findViewById(R.id.count_of_item_user_cart_view);
        total_amount_cart=(TextView) itemView.findViewById(R.id.total_of_item_user_cart_view);
        // item_image_cart=(CircleImageView) itemView.findViewById(R.id.item_image_user_cart_view);

    }

    @Override
    public void onClick(View view)
    {
        listner.OnClick(view, getAdapterPosition(), false);
    }

    public void setItemClickedListner(itemclicklistner listner)
    {
        this.listner=listner;
    }
}
