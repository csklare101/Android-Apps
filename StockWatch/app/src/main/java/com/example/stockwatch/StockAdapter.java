package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {
    private final ArrayList<Stock> stockList;
    private final MainActivity mainAct;

    public StockAdapter(ArrayList<Stock> stockList, MainActivity mainAct) {
        this.stockList = stockList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_entry, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.price.setText(String.format("%.2f", stock.getPrice()));
        holder.change.setText(String.format("%.2f", stock.getPriceChange()));
        holder.percent.setText(String.format("(%.2f%%)", stock.getChangePercent()));
        if(stock.getChangePercent() >= 0){
            holder.arrow.setText("▲");
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
            holder.percent.setTextColor(Color.GREEN);
            holder.arrow.setTextColor(Color.GREEN);
        }
        else{
            holder.arrow.setText("▼");
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.percent.setTextColor(Color.RED);
            holder.arrow.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
