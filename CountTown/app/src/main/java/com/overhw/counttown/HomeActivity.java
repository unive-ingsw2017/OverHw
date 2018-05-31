package com.overhw.counttown;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import ru.dimorinny.showcasecard.step.ShowCaseStep;
import ru.dimorinny.showcasecard.step.ShowCaseStepDisplayer;

public class HomeActivity extends AppCompatActivity {

    private CardView datiInterni, appalti, benchmarkComuni, info;
    private AutoCompleteTextView citta;
    private ImageButton salvaCitta;
    private Util csvUtil;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getApplicationContext().getSharedPreferences("CountTownPrefs", MODE_PRIVATE);
        editor = prefs.edit();

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        csvUtil = new Util(this);

        datiInterni = findViewById(R.id.dati_interni);
        datiInterni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DatiComuni.dettagli_comune != null && DatiComuni.nomi_citta.contains(DatiComuni.dettagli_comune.getNome())) {
                    Intent dati = new Intent(HomeActivity.this, DatiInterniActivity.class);
                    startActivity(dati);
                }
                else{
                    Snackbar.make(view, "Nome comune non valido", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        appalti = findViewById(R.id.appalti);
        appalti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DatiComuni.dettagli_comune != null && DatiComuni.nomi_citta.contains(DatiComuni.dettagli_comune.getNome())) {
                    Intent appalti = new Intent(HomeActivity.this, ListaAppaltiActivity.class);
                    appalti.putExtra("city_bench", DatiComuni.dettagli_comune.getNome());
                    startActivity(appalti);
                }
                else{
                    Snackbar.make(view, "Nome comune non valido", Snackbar.LENGTH_SHORT).show();
                }

            }
        });


        benchmarkComuni = findViewById(R.id.benchmark_comuni);
        benchmarkComuni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent benchmark= new Intent(HomeActivity.this,BenchmarkActivity.class);
                startActivity(benchmark);
            }
        });

        info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infopage = new Intent(HomeActivity.this,Info.class);
                startActivity(infopage);
            }
        });

        citta = findViewById(R.id.citta);
        // carica la lista dei nomi dei comuni italiani come suggerimento per l'immissione del nome
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DatiComuni.nomi_citta);
        citta.setAdapter(adapter);
        citta.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch(i){
                        case KeyEvent.KEYCODE_ENTER:
                            imm.hideSoftInputFromWindow(citta.getWindowToken(), 0);
                            String newcity = citta.getText().toString().substring(0,1).toUpperCase() + citta.getText().toString().substring(1,citta.getText().toString().length());
                            System.out.println(newcity);
                            citta.setText(newcity);
                            DatiComuni.dettagli_comune.setNome(newcity);
                            new DetailsEcho().execute("https://overhw.com/counttown/scripts/towns_details_nome.php?ncomune=" + newcity);
                            editor.putString("savedCity",newcity);
                            editor.apply();
                            editor.commit();
                            Snackbar.make(view, "Comune salvato con successo", Snackbar.LENGTH_SHORT).show();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        salvaCitta = findViewById(R.id.salvaCitta);
        salvaCitta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newcity = citta.getText().toString().substring(0,1).toUpperCase() + citta.getText().toString().substring(1,citta.getText().toString().length());
                System.out.println(newcity);
                citta.setText(newcity);
                DatiComuni.dettagli_comune = new Comune();
                DatiComuni.dettagli_comune.setNome(newcity);
                new DetailsEcho().execute("https://overhw.com/counttown/scripts/towns_details_nome.php?ncomune=" + newcity);
                editor.putString("savedCity",newcity);
                editor.apply();
                editor.commit();
                Snackbar.make(view, "Comune salvato con successo", Snackbar.LENGTH_SHORT).show();
            }
        });

        boolean flag = prefs.getBoolean("flag", false);
        if(!flag){
            showTutorial();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("flag",true);
            editor.apply();
            editor.commit();
        }
    }/*Chiusura OnCreate di HomeActivity*/

    private void showTutorial(){
        String[] texttutorial= new String[]{"SALVA IL COMUNE DI RIFERIMENTO!","VISUALIZZA I DATI DEL COMUNE","SCORRI APPALTI E CONCORSI PER ANNO","ESEGUI UN BENCHMARK FRA COMUNI"};
        View[] idtutorial=new View[]{findViewById(R.id.citta),findViewById(R.id.dati_interni),findViewById(R.id.appalti),findViewById(R.id.benchmark_comuni)};

        new ShowCaseStepDisplayer.Builder(HomeActivity.this)
                .addStep(new ShowCaseStep(idtutorial[0], texttutorial[0]))
                .addStep(new ShowCaseStep(idtutorial[1], texttutorial[1]))
                .addStep(new ShowCaseStep(idtutorial[2], texttutorial[2]))
                .addStep(new ShowCaseStep(idtutorial[3], texttutorial[3]))
                .build().start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        /* check internet connection */
        if (testConnection()) {

            // se la lista dei comuni è vuota la scarica
            if (DatiComuni.nomi_citta.isEmpty()) {
                csvUtil.downloadTownsNames();
            }

            // se il comune salvato nelle preferenze è diverso da null scarica tutti i dettagli di quel comune
            String savedCity = prefs.getString("savedCity", null);
            if(savedCity != null && !savedCity.equals("")) {
                citta.setText(savedCity);
                new DetailsEcho().execute("https://overhw.com/counttown/scripts/towns_details_nome.php?ncomune=" + savedCity);
            }

            // se il comune
            if(DatiComuni.dettagli_comune != null){
                citta.setText(DatiComuni.dettagli_comune.getNome());
            }
        }
        else{                      /*Chiude l'app in assenza di connessione*/
            dialogConnection();
        }


    }

    // controlla che la connessione sia attiva (true) o disattivata (false)
    private boolean testConnection(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // apre un alert dialog per indicare all'utente che la connessione ad internet è lenta o assente
    private void dialogConnection(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setTitle("Attenzione");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.not_connection);
        dialog.setMessage(Html.fromHtml("Connessione lenta o assente.\nControlla la connessione e riprova."));
        dialog.setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onStart();
            }
        });
        dialog.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                System.exit(0);
            }
        });
        dialog.show();
    }

    /**-------------------------------------------- CLASSE PER SCARICARE IL FILE TOWNS_DETAILS.CSV ---------------------------------------------*/

    class DetailsEcho extends AsyncTask<String, Integer, String> {
        ProgressDialog pDialog;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setTitle("Caricamento comune...");
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

            if(!result.equalsIgnoreCase("nocomune")){
                if(!result.equalsIgnoreCase("unsuccessful")) {
                    DatiComuni.dettagli_comune = null;
                    String[] com = result.split(":");
                    DatiComuni.dettagli_comune = new Comune(com);
                    System.out.println(Arrays.toString(com));
                }
            }
            else{
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Intent home = new Intent(HomeActivity.this, HomeActivity.class);
                                startActivity(home);
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Attenzione")
                        .setMessage(Html.fromHtml("Il comune inserito non è valido." +
                                "<br/><b>Suggerimento:</b> prova a scegliere un comune dalla lista."))
                        .setNegativeButton("Home", dialogClickListener).show()
                        .setCancelable(false);
            }
            pDialog.dismiss();

        }
    }
}
