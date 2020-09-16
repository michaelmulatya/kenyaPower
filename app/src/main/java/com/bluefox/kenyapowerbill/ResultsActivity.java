package com.bluefox.kenyapowerbill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    RecyclerView results;
    List<Data> dataList;
    ResultsAdapter resultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        results = findViewById(R.id.results);
        results.setLayoutManager(new LinearLayoutManager(this));


        //initialise the arraylist
        dataList = new ArrayList<>();
        //get json data results
        Intent intent = getIntent();
        String jsoninfo = intent.getStringExtra("jsondata");
        try {
            JSONObject jsonObject = new JSONObject(jsoninfo);
            if (jsonObject.length() > 2){
                Iterator<String> stringIterator = jsonObject.keys();
                while (stringIterator.hasNext()){
                    String charges = stringIterator.next();
                    String amounts = jsonObject.getString(charges);
                    Data newdata = new Data(charges,amounts);
                    dataList.add(newdata);
                }

            }else {
                Toast.makeText(this, "data load failed", Toast.LENGTH_SHORT).show();
            }
            resultsAdapter = new ResultsAdapter(dataList,getApplicationContext());
            results.setAdapter(resultsAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }








    }
}