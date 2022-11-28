package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final ArrayList<Stock> stockList = new ArrayList<>();
    private final ArrayList<Stock> tempStockList = new ArrayList<>();

    private HashMap<String, String> stockNames;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter stockAdapter;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockNames = new HashMap<>();

        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        loadDataFromFile();

        /* if(stockList.size() == 0){
            Stock good = new Stock("GOOD", "Good Comp", 111.11, 1.11, 0.11);
            Stock bad = new Stock("BAD", "Bad Inc", 90.00, -9.99, -9.99);
            stockList.add(good);
            stockList.add(bad);
            stockAdapter.notifyItemRangeChanged(0, stockList.size());
            setTitle(getString(R.string.app_name) + "(" + stockList.size() + ")");
        }
        */

        if(hasNetworkConnection()){
            NameDownloader.downloadNames(this);
        }
        else{
            AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
            noInternet.setTitle("No Network Connection");
            noInternet.setMessage("Stocks cannot be accesed without internet!");
            noInternet.create().show();
        }
        doRefresh();
    }

    private void doRefresh() {
        stockList.clear();
        loadTempDataFromFile();

        if(hasNetworkConnection()) {
            count = tempStockList.size();
            for (Stock s : tempStockList) {
                downloadStock(s.getSymbol());
            }
            swiper.setRefreshing(false);
        }

        else{
            stockList.addAll(tempStockList);
            tempStockList.clear();

            Collections.sort(stockList);
            stockAdapter.notifyItemRangeChanged(0, stockList.size());
            swiper.setRefreshing(false);

            AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
            noInternet.setTitle("No Network Connection");
            noInternet.setMessage("Stocks cannot be refreshed without internet!");
            noInternet.create().show();
            return;
        }
        swiper.setRefreshing(false);
    }

    public void updateData(HashMap names){
        stockNames.putAll(names);
    }

    public void downloadFailed(){
        stockNames.clear();
    }

    public void downloadStock(String s){
        StockDownloader.downloadStockData(this, s);
    }

    public void passStock(Stock s){
        Log.d(TAG, "passStock: ");
        if(stockList.contains(s)){
            AlertDialog.Builder dup = new AlertDialog.Builder(this);
            dup.setTitle("Duplicate Stock!");
            dup.setMessage("Stocks cannot be added twice!");
            dup.create().show();
        }
        else{
            count--;
            stockList.add(s);
            Collections.sort(stockList);
            if (count == 0)
                stockAdapter.notifyItemRangeChanged(0, stockList.size());
            saveDataToFile();
        }
    }


    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + s.getSymbol() + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            stockList.remove(pos);
            stockAdapter.notifyItemRemoved(pos);
            saveDataToFile();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {});
        builder.create().show();

        return false;
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.marketwatch.com/investing/stock/" + s.getSymbol()));

        //if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addStock){
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Stock Selection");
            build.setMessage("Please enter a Stock Symbol");
            final EditText symbol = new EditText(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(symbol);
            build.setView(layout);

            symbol.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            if(hasNetworkConnection()){
                build.setPositiveButton("OK", (dialog, which) -> {
                        ArrayList<String> tempMap = new ArrayList<>();
                        for(String key: stockNames.keySet()){
                            if(key.contains(symbol.getText().toString()) || stockNames.get(key).contains(symbol.getText().toString())){
                                tempMap.add(key + " - " + stockNames.get(key));
                            }
                        }
                        if(tempMap.size() > 1){
                            AlertDialog.Builder stockSelect = new AlertDialog.Builder(this);
                            stockSelect.setTitle("Make a selection");
                            String[] arr = tempMap.toArray(new String[tempMap.size()]);
                            stockSelect.setItems(arr, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int spaceInd = arr[which].indexOf(" ");
                                    String add = arr[which].substring(0,spaceInd);
                                    count = 1;
                                    downloadStock(add);
                                }
                            });
                            stockSelect.create().show();
                        }
                        else if(tempMap.size() == 0){
                            AlertDialog.Builder symNotFound = new AlertDialog.Builder(this);
                            symNotFound.setTitle("Symbol not found: " + symbol.getText().toString());
                            symNotFound.setMessage("No data found!");
                            symNotFound.create().show();
                        }
                        else if(tempMap.size() == 1){
                            downloadStock(tempMap.get(0).substring(0, tempMap.get(0).indexOf(" ")));
                        }
                });
                build.setNegativeButton("Cancel", (dialog, which) -> {});
                build.create().show();
            }
            else{
                AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
                noInternet.setTitle("No Network Connection");
                noInternet.setMessage("Stocks cannot be added without internet!");
                noInternet.create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveDataToFile(){
        JSONArray jArray = new JSONArray();
        for(Stock s : stockList){
            try{
                jArray.put(s.toJSON());
            }
            catch(JSONException je) {
                Log.d(getClass().getSimpleName(), je.getMessage());
                je.printStackTrace();
            }
        }

        try{
            FileOutputStream fos = getApplicationContext().openFileOutput("JSONText.json", MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);
            pr.println(jArray);
            pr.close();
            fos.close();
        }
        catch (Exception e){
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
        setTitle(getString(R.string.app_name) + "(" + stockList.size() + ")");
    }

    private void loadDataFromFile(){
        FileInputStream fis;
        try{
            fis = getApplicationContext().openFileInput("JSONText.json");
        }
        catch(FileNotFoundException fnfe){
            Log.d(getClass().getSimpleName(), fnfe.getMessage());
            return;
        }

        StringBuilder fileContent = new StringBuilder();

        try{
            byte[] buffer = new byte[1024];
            int i;
            while ((i = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, i));
            }
            JSONArray jsonArray = new JSONArray(fileContent.toString());
            for (int j = 0; j < jsonArray.length(); j++) {
                stockList.add(Stock.createFromJSON(jsonArray.getJSONObject(j)));
            }
        }
        catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
            return;
        }
        setTitle(getString(R.string.app_name) + "(" + stockList.size() + ")");
    }

    private void loadTempDataFromFile(){
        tempStockList.clear();
        FileInputStream fis;
        try{
            fis = getApplicationContext().openFileInput("JSONText.json");
        }
        catch(FileNotFoundException fnfe){
            Log.d(getClass().getSimpleName(), fnfe.getMessage());
            return;
        }

        StringBuilder fileContent = new StringBuilder();

        try{
            byte[] buffer = new byte[1024];
            int i;
            while ((i = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, i));
            }
            JSONArray jsonArray = new JSONArray(fileContent.toString());
            for (int j = 0; j < jsonArray.length(); j++) {
                tempStockList.add(Stock.createFromJSON(jsonArray.getJSONObject(j)));
            }
        }
        catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
            return;
        }
        setTitle(getString(R.string.app_name) + "(" + tempStockList.size() + ")");
    }

    public boolean hasNetworkConnection(){
        ConnectivityManager cm = getSystemService(ConnectivityManager.class);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return(ni != null && ni.isConnectedOrConnecting());
    }
}