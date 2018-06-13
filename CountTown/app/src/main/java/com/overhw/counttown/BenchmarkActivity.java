package com.overhw.counttown;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class BenchmarkActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView ncomune,ncomune1;
    Button startRicerca , startConfronta;
    ImageButton back;

    private SeekBar sBarFiltri ,sBarSelezione;
    private TextView tViewSBFILTRI , tViewSBSELEZIONE;
    private ArrayAdapter<String> listviewAdapter;

    Spinner spinner;
    ArrayAdapter<String> adp;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benchmark_comuni);

        Toolbar toolbar = findViewById(R.id.benchmark_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ncomune= findViewById(R.id.benchNcomune);
        ncomune1= findViewById(R.id.benchNcomune1);

        /*Seekbar filtri */
        sBarFiltri = findViewById(R.id.seekBar1);
        tViewSBFILTRI = findViewById(R.id.textview1);

        spinner = findViewById(R.id.listaComuni);

        sBarFiltri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tViewSBFILTRI.setText(progress + "/" + seekBar.getMax()  + " volte");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        /*Seekbar selezione*/
        sBarSelezione = findViewById(R.id.seekBar2);
        tViewSBSELEZIONE = findViewById(R.id.textview2);

        sBarSelezione.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tViewSBSELEZIONE.setText(progress + "/" + seekBar.getMax() + "%");
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

        /*BOTTONE RICERCA*/
        startRicerca = findViewById(R.id.startRicerca);
        startRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseIndexEcho().execute("https://overhw.com/counttown/scripts/choose_town.php?ncomune=" + DatiComuni.dettagli_comune.getNome() + "&nabitanti=" + sBarFiltri.getProgress() + "&napptot=" + sBarSelezione.getProgress()); /*Salva in cinqueComuni il risultato*/
                System.out.println(" ______________________________" + DatiComuni.ben_comuni);
                System.out.println("Valore sbar filtri"+ sBarFiltri.getProgress());
                System.out.println("Valore sbar selezione"+ sBarSelezione.getProgress());
                /*callSetComuni(DatiComuni.ben_comuni);*/
            }
        });


        /*BOTTONE CONFRONTA*/

        startConfronta = findViewById(R.id.confrontaBenchamark);
        startConfronta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* ........Apre pagina di confronto fra i 2 comuni ....*/
                Intent confronto = new Intent(BenchmarkActivity.this, ActivityConfrontoComuni.class);
                confronto.putExtra("compareCity", spinner.getSelectedItem().toString());
                System.out.println("Comune passato ad ActivityConfrontoComune = " + spinner.getSelectedItem().toString());

                startActivity(confronto);
         }
        });


        init();
    }  /*Chiusura onCreate */




    void init(){
        if (DatiComuni.dettagli_comune != null) {
            ncomune.setText(DatiComuni.dettagli_comune.getNome());
            ncomune1.setText(DatiComuni.dettagli_comune.getNome());
            tViewSBFILTRI.setText(sBarFiltri.getProgress() + "/" + sBarFiltri.getMax() + " volte");
            sBarSelezione.setMax(100);
            tViewSBSELEZIONE.setText(sBarSelezione.getProgress() + "/" + sBarSelezione.getMax() +  "%");
        }/*Chiusura if */
        else {
            Toast.makeText(getApplicationContext(), "Prima devi salvare il comune nella HOME!", Toast.LENGTH_SHORT).show();
            Intent home = new Intent(BenchmarkActivity.this, HomeActivity.class);  /*Se non trova il nome del comune riporta alla home */
            startActivity(home);
        }/* Chiusura else*/

    }/*Chiusura init*/




    @Override
    public void onClick(View view) {


    }/*Chiusura onClick*/



    public void callSetComuni(String[] cinqueComuni) {
        boolean check = setComuni(cinqueComuni); /* Settaggio vero e proprio dei comuni enllo spinner */
        if (check == false) {
            Intent home = new Intent(BenchmarkActivity.this, HomeActivity.class);  /*Se non trova il nome del comune riporta alla home */
            startActivity(home);
            Toast.makeText(getApplicationContext(), "Non sono stati trovati abbastanza comuni per il benchmark!", Toast.LENGTH_SHORT).show();
        }
    }



    public boolean setComuni(String[] cinqueComuni) {
        if (cinqueComuni != null) {

            ArrayList<String> listacomuni=new ArrayList<>();
            int i;
            for(i = 0; i < 5; i++) {
                listacomuni.add(cinqueComuni[i]);
            }

            adp = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listacomuni);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adp);

            return true;

        } else {
            return false;
        }
    }



    public void refreshButton(String[] arrayToRefresh){
        if (arrayToRefresh.length >= 5) {

            ArrayList<String> listacomuni = new ArrayList<>();
            int i;

            for (i = 0; i < 5; i++) {
                listacomuni.add(arrayToRefresh[i]);
            }
            adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listacomuni);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adp);
        }
        else{

            Toast.makeText(getApplicationContext(), "Non sono stati trovati abbastanza comuni per il benchmark!", Toast.LENGTH_SHORT).show();
        }
    }








    /*----------------------------------------------------CLASSE PER SALVARE DATI DA ChooseIndex ------------------------------------*/
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
        }                               /*Chiusura do it in background*/



        @Override
        protected void onPostExecute(String result) {           /*Salva i 5 comuni (o l'array vuoto in DatiComuni.ben_comuni  */

            if(!result.equalsIgnoreCase("unsuccessful")) {
                String[] res = result.split(",");
                DatiComuni.ben_comuni = res.clone();
                System.out.println(Arrays.toString(DatiComuni.ben_comuni));
                refreshButton(DatiComuni.ben_comuni);

            }

        } /*Chiusura onPostExecute*/
    }
}/*Chiusura classe BenchmarkActivity*/