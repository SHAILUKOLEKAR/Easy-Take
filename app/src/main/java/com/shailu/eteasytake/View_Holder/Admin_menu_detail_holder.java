package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_menu_detail_holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{
    public TextView show_price;
    public TextView usershow_price;
    public CircleImageView item_image_view;
    public CircleImageView useritem_image_view;
    public LinearLayout layout;
    public itemclicklistner listner;
    public  View view;
    public TextView item_name;
    public TextView useritem_name;


    public Admin_menu_detail_holder(@NonNull View itemView)
    {
        super(itemView);
        show_price=(TextView)itemView.findViewById(R.id.price_of_item);
        item_name=(TextView)itemView.findViewById(R.id.item_name_admin);
        item_image_view=(CircleImageView)itemView.findViewById(R.id.item_image);
        layout=(LinearLayout)itemView.findViewById(R.id.add_price_layout);
        usershow_price=(TextView)itemView.findViewById(R.id.price_of_item_user_view);
        useritem_name=(TextView)itemView.findViewById(R.id.item_name_user_view);
        view=(View)itemView.findViewById(R.id.delet_item_from_menu);
        useritem_image_view=(CircleImageView)itemView.findViewById(R.id.item_image_user_view);
    }

    @Override
    public void onClick(View view)
    {
        listner.OnClick(view, getAdapterPosition(),false);
    }
}
