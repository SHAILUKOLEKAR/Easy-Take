package com.shailu.eteasytake.View_Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shailu.eteasytake.Interfaces.itemclicklistner;
import com.shailu.eteasytake.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class hotels_name_holder extends RecyclerView.ViewHolder  implements View.OnClickListener
{
    public CircleImageView hotelimage;
    public TextView hotelname , villagename;
    public itemclicklistner listner;

    public hotels_name_holder(@NonNull View itemView)
    {
        super(itemView);
        hotelimage=(CircleImageView) itemView.findViewById(R.id.list_hotel_img);
        hotelname=(TextView) itemView.findViewById(R.id.list_hotel_name);
        villagename=(TextView) itemView.findViewById(R.id.list_village_name);

    }


    @Override
    public void onClick(View view)
    {
        listner.OnClick(view, getAdapterPosition(),false);
    }
}
