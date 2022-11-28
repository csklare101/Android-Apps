package com.example.stockwatch;

import org.json.JSONException;
import org.json.JSONObject;

public class Stock implements Comparable<Stock>{
    private final String symbol;
    private final String name;
    private final double price;
    private final double priceChange;
    private final double changePercent;

    public Stock(String symbol, String name, double price, double priceChange, double changePercent) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.priceChange = priceChange;
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jobj = new JSONObject();

        jobj.put("symbol", symbol);
        jobj.put("name", name);
        jobj.put("price", price);
        jobj.put("priceChange", priceChange);
        jobj.put("changePercent", changePercent);

        return jobj;
    }

    public static Stock createFromJSON(JSONObject jobj) throws JSONException{
        String symbol = jobj.getString("symbol");
        String name = jobj.getString("name");
        double price = jobj.getLong("price");
        double priceChange = jobj.getLong("priceChange");
        double changePercent = jobj.getLong("changePercent");

        return new Stock(symbol, name, price, priceChange, changePercent);
    }


    @Override
    public int compareTo(Stock stock) {
        return name.compareTo(stock.name);
    }
}
