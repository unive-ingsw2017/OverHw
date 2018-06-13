package com.overhw.counttown;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListaAppaltiActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton back;
    private TextView citta;
    private Spinner spinner, spinnerTipo;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_appalti);

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView = findViewById(R.id.lista_appalti_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(new ArrayList<Appalto>(), this);
        recyclerView.setAdapter(adapter);

        toolbar = findViewById(R.id.lista_appalti_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        back = findViewById(R.id.lista_appalti_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        citta = findViewById(R.id.lista_appalti_edittext_citta);

        spinner = findViewById(R.id.spinner_lista_appalti);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.anniappalti, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                new DetailsEchoAppalti().execute("https://overhw.com/counttown/scripts/appalti_details_anno.php?ncomune=" + citta.getText().toString() + "&anno=" + spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        spinnerTipo = findViewById(R.id.spinner_tipo_appalto);
        ArrayAdapter adapterTipo= ArrayAdapter.createFromResource(this, R.array.tipoappalti, R.layout.spinner_item);
        adapterTipo.setDropDownViewResource(R.layout.spinner_item);
        spinnerTipo.setAdapter(adapterTipo);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        String city = null;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            city = extras.getString("city_bench");
        }
        initData(city);
    }

    private void initData(String city) {
        if(city != null){
            citta.setText(city);
            new DetailsEchoAppalti().execute("https://overhw.com/counttown/scripts/appalti_details_anno.php?ncomune=" + citta.getText().toString() + "&anno=" + spinner.getSelectedItem().toString());
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Errore").setMessage("Il nome della città non è corretto").setCancelable(false);
            dialog.setPositiveButton("Home", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent home = new Intent(ListaAppaltiActivity.this, HomeActivity.class);
                    startActivity(home);
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void refreshData(){
        ArrayList<Appalto> appalti = new ArrayList<>();
        int i=0;
        while(i<DatiComuni.appalti.size()){
            if(DatiComuni.appalti.get(i).getTipoBando().equalsIgnoreCase(spinnerTipo.getSelectedItem().toString()) && !spinnerTipo.getSelectedItem().toString().equalsIgnoreCase("Tutti")){
                appalti.add(DatiComuni.appalti.get(i));
            }else if(spinnerTipo.getSelectedItem().toString().equalsIgnoreCase("Tutti")){
                appalti.add(DatiComuni.appalti.get(i));
            }
            i++;
        }
        RecyclerViewAdapter newAdapter = new RecyclerViewAdapter(appalti, this);
        recyclerView.swapAdapter(newAdapter, true);
    }

    private void searchCityContracts(String s) {
        System.out.println("Per il comune di " + s + " sono stati trovati " + DatiComuni.appalti.size() + " appalti");

        if(DatiComuni.appalti.size() > 0){
            RecyclerViewAdapter newAdapter = new RecyclerViewAdapter(DatiComuni.appalti, this);
            recyclerView.swapAdapter(newAdapter, true);
        }
        else{
            Snackbar.make(recyclerView, "Nessun appalto trovato", Snackbar.LENGTH_SHORT).show();
        }
    }

    class DetailsEchoAppalti extends AsyncTask<String, Integer, String> {
        ProgressDialog pDialog;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(ListaAppaltiActivity.this);
            pDialog.setTitle("Caricamento appalti...");
            pDialog.setMessage("\tAttendere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                // Enter URL address where your php file resides
                url = new URL(strings[0]);

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {

            DatiComuni.appalti.clear();

            if(!result.equalsIgnoreCase("unsuccessful")) {
                String[] appalto = result.split("€");
                System.out.println("NUMERO APPALTI: " + appalto.length);
                int i = 0;
                Appalto common;
                String[] tokens;
                while (i < appalto.length) {
                    tokens = appalto[i].split(";");
                    common = new Appalto(tokens);
                    if(common.getIdGara() != null) {
                        DatiComuni.appalti.add(common);
                        System.out.println("ADD: " + common.getIdGara());
                    }
                    i++;
                }
            }
            searchCityContracts(citta.getText().toString());
            pDialog.dismiss();

            refreshData();
        }
    }
}