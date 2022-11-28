package com.example.civiladvocacy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {
    private static final String TAG = "OfficialAdapter";
    private final ArrayList<Official> officialList;
    private final MainActivity mainAct;

    public OfficialAdapter(ArrayList<Official> officialList, MainActivity mainAct) {
        this.officialList = officialList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_entry, parent, false);
        itemView.setOnClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);

        holder.name.setText(official.getName() + " (" + official.getParty() + ")");
        holder.office.setText(official.getOffice());


        if(!official.getPhotoUrl().equals("")){
            Picasso.get().load(official.getPhotoUrl())
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            long time = System.currentTimeMillis();
                            Log.d(TAG, "onSuccess: "+ time);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "onError " + e);
                            holder.imageView.setImageResource(R.drawable.brokenimage);
                        }
                    });
        }
        else{
            holder.imageView.setImageResource(R.drawable.missing);
        }
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
