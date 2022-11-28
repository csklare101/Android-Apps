package com.example.civiladvocacy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder{
    TextView name;
    TextView office;
    ImageView imageView;

    public OfficialViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.officialName);
        office = itemView.findViewById(R.id.officeTitle);
        imageView = itemView.findViewById(R.id.imageView);
    }
}
