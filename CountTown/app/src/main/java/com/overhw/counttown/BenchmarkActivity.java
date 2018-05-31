package com.overhw.counttown;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class BenchmarkActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView ncomune, comune1, comune2, comune3, comune4, comune5;
    Button startBenchmark;
    ImageButton back;

    private SeekBar sBar;
    private TextView tView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benchmark_comuni);

        Toolbar toolbar = findViewById(R.id.benchmark_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ncomune = findViewById(R.id.benchNcomune);
        comune1 = findViewById(R.id.benchComune0);
        comune2 = findViewById(R.id.benchComune1);
        comune3 = findViewById(R.id.benchComune2);
        comune4 = findViewById(R.id.benchComune3);
        comune5 = findViewById(R.id.benchComune4);

        sBar = findViewById(R.id.seekBar1);
        tView = findViewById(R.id.textview1);

        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tView.setText(progress + "/" + seekBar.getMax());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        back = findViewById(R.id.ben_comuni_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        startBenchmark = findViewById(R.id.startBench);
        startBenchmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseIndexEcho().execute("https://overhw.com/counttown/scripts/choose_town.php?ncomune=" + DatiComuni.dettagli_comune.getNome()+"&nabitanti=" + sBar.getProgress() ); /*Salva in cinqueComuni il risultato*/
                System.out.println(sBar.getProgress());
                callSetComuni(DatiComuni.ben_comuni);
            }
        });

        init();

    }    /*Chiusura onCreate */

    void init(){
        if (DatiComuni.dettagli_comune != null) {
            sBar.setMax(Integer.parseInt(DatiComuni.dettagli_comune.getPopolazioneResidente()));
            tView.setText(sBar.getProgress() + "/" + sBar.getMax());
            ncomune.setText(DatiComuni.dettagli_comune.getNome());
        }/*Chiusura if */
        else {
            Toast.makeText(getApplicationContext(), "Prima devi salvare il comune nella HOME!", Toast.LENGTH_SHORT).show();
            Intent home = new Intent(BenchmarkActivity.this, HomeActivity.class);  /*Se non trova il nome del comune riporta alla home */
            startActivity(home);
        }/* Chiusura else*/
    }

    @Override
    public void onClick(View view) {

        int comune = -1;
        switch(view.getId()){
            case R.id.benchComune0:
                comune = 0;
                break;
            case R.id.benchComune1:
                comune = 1;
                break;
            case R.id.benchComune2:
                comune = 2;
                break;
            case R.id.benchComune3:
                comune = 3;
                break;
            case R.id.benchComune4:
                comune = 4;
                break;
        }
        if(comune > -1){
            Intent city = new Intent(BenchmarkActivity.this, ListaAppaltiActivity.class);
            city.putExtra("city_bench", DatiComuni.ben_comuni[comune]);
            startActivity(city);
        }
    }

    public void callSetComuni(String[] cinqueComuni) {
        boolean check = setComuni(cinqueComuni);
        if (check == false) {
            Intent home = new Intent(BenchmarkActivity.this, HomeActivity.class);  /*Se non trova il nome del comune riporta alla home */
            startActivity(home);
            Toast.makeText(getApplicationContext(), "Non sono stati trovati abbastanza comuni per il benchmark!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean setComuni(String[] cinqueComuni) {
        if (cinqueComuni != null) {
            /*Set*/
            ncomune.setText(DatiComuni.dettagli_comune.getNome());
            comune1.setText(cinqueComuni[0]);
            comune2.setText(cinqueComuni[1]);
            comune3.setText(cinqueComuni[2]);
            comune4.setText(cinqueComuni[3]);
            comune5.setText(cinqueComuni[4]);

            /*On click listener*/
            comune1.setOnClickListener(this);
            comune2.setOnClickListener(this);
            comune3.setOnClickListener(this);
            comune4.setOnClickListener(this);
            comune5.setOnClickListener(this);
            return true;

        } else {
            return false;
        }
    }

    public void refreshButton(){
        comune1.setText(DatiComuni.ben_comuni[0]);
        comune2.setText(DatiComuni.ben_comuni[1]);
        comune3.setText(DatiComuni.ben_comuni[2]);
        comune4.setText(DatiComuni.ben_comuni[3]);
        comune5.setText(DatiComuni.ben_comuni[4]);
    }

    /*CLASSE PER SALVARE DATI DA ChooseIndex */
    class ChooseIndexEcho extends AsyncTask<String, Integer, String>  {
        HttpURLConnection conn;
        URL url = null;

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
        } /*Chiusura do it in background*/

        @Override
        protected void onPostExecute(String result) {

            if(!result.equalsIgnoreCase("unsuccessful")) {

                DatiComuni.ben_comuni = null;
                String [] res = result.split(",");
                DatiComuni.ben_comuni = res.clone();
                System.out.println(Arrays.toString(DatiComuni.ben_comuni));

                refreshButton();
            }
        } /*Chiusura onPostExecute*/
    }
}/*Chiusura classe BenchmarkActivity*/