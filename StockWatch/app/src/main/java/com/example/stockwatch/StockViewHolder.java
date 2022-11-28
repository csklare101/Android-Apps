package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder{
    TextView symbol;
    TextView price;
    TextView arrow;
    TextView change;
    TextView percent;
    TextView name;

    StockViewHolder(View view){
        super(view);
        symbol = view.findViewById(R.id.stockSymbol);
        name = view.findViewById(R.id.stockName);
        price = view.findViewById(R.id.stockPrice);
        arrow = view.findViewById(R.id.stockArrow);
        percent = view.findViewById(R.id.stockPercent);
        change = view.findViewById(R.id.stockChange);
    }
}
