package com.example.tipsplit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
/*
* tip spliting calculator
* Carson Sklare
* 10/2/22
* For CSC 472 Project 1
 */
public class MainActivity extends AppCompatActivity {
    private EditText billTotal;
    private EditText numPeople;
    private TextView tipAmmountField;
    private TextView tipTotalAmmountField;
    private TextView totPerPersonField;
    private RadioGroup buttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billTotal = findViewById(R.id.billTotalText);
        numPeople = findViewById(R.id.numPeopleText);
        tipAmmountField = findViewById(R.id.tipAmmText);
        tipTotalAmmountField = findViewById(R.id.totwithTipText);
        totPerPersonField = findViewById(R.id.totPerPersText);
        numPeople = findViewById(R.id.numPeopleText);
        buttons = findViewById(R.id.radioGroup);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        if(tipAmmountField.getText().toString().length() > 0) {
            outState.putString("TIPAMMOUNTFIELD", tipAmmountField.getText().toString());
        }
        if(tipTotalAmmountField.getText().toString().length() > 0) {
            outState.putString("TIPTOTALAMMOUNTFIELD", tipTotalAmmountField.getText().toString());
        }
        if(totPerPersonField.getText().toString().length() > 0) {
            outState.putString("TOTPERPERSONFIELD", totPerPersonField.getText().toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tipAmmountField.setText(savedInstanceState.getString("TIPAMMOUNTFIELD"));
        tipTotalAmmountField.setText(savedInstanceState.getString("TIPTOTALAMMOUNTFIELD"));
        totPerPersonField.setText((savedInstanceState.getString("TOTPERPERSONFIELD")));
    }

    public void tipPercent(View v){
        double tipAmount;
        double totalWithTip;
        System.out.println(billTotal.getText().toString());
        if(billTotal.getText().toString().length() > 0) {
            double billTotalNum = Double.parseDouble(billTotal.getText().toString());
            if (v.getId() == R.id.tipBut12) {
                tipAmount = billTotalNum * 0.12;
                totalWithTip = tipAmount + billTotalNum;
                tipAmmountField.setText(String.format("%.2f", tipAmount));
                tipTotalAmmountField.setText(String.format("%.2f", totalWithTip));
            } else if (v.getId() == R.id.tipBut15) {
                tipAmount = billTotalNum * 0.15;
                totalWithTip = tipAmount + billTotalNum;
                tipAmmountField.setText(String.format("%.2f", tipAmount));
                tipTotalAmmountField.setText(String.format("%.2f", totalWithTip));
            } else if (v.getId() == R.id.tipBut18) {
                tipAmount = billTotalNum * 0.18;
                totalWithTip = tipAmount + billTotalNum;
                tipAmmountField.setText(String.format("%.2f", tipAmount));
                tipTotalAmmountField.setText(String.format("%.2f", totalWithTip));
            } else if (v.getId() == R.id.tipBut20) {
                tipAmount = billTotalNum * 0.20;
                totalWithTip = tipAmount + billTotalNum;
                tipAmmountField.setText(String.format("%.2f", tipAmount));
                tipTotalAmmountField.setText(String.format("%.2f", totalWithTip));
            }
        }
        else{
            buttons.clearCheck();
        }
    }

    public void personSplit(View v){
        if(numPeople.getText().toString().length() > 0 && tipTotalAmmountField.getText().toString().length() > 0){
            int people = Integer.parseInt(numPeople.getText().toString());
            double totWithoutPeople = Double.parseDouble(tipTotalAmmountField.getText().toString());
            double totWithPeople = totWithoutPeople / people;
            totPerPersonField.setText(String.format("%.2f",totWithPeople));
        }
    }

    public void clear(View v){
        buttons.clearCheck();
        billTotal.getText().clear();
        numPeople.getText().clear();
        tipAmmountField.setText("");
        tipTotalAmmountField.setText("");
        totPerPersonField.setText("");
    }
}