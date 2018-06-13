package com.overhw.counttown;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class ActivityConfrontoComuni extends AppCompatActivity implements GestureDetector.OnGestureListener{

    LinearLayout salvato, confronto;
    LinearLayout lsalvato, lconfronto, dati_salvato, dati_confronto;
    GestureDetector detector;
    static Display display;
    static Point size;
    static ViewGroup.LayoutParams params_salvato;
    static ViewGroup.LayoutParams params_confronto;
    static int minus;
    static int numAppaltiSalvato, numAppaltiConfronto;

    ImageButton back;
    Button btnSalvato, btnConfronto;
    TextView titolo_toolbar, c1_numero_appalti, c1_media_appalti, c1_nome, c2_numero_appalti, c2_media_appalti, c2_nome;

    Spinner spinner;

    String[] comuni;

    RecyclerView recyclerViewSalvato, recyclerViewConfronto;
    RecyclerViewAdapter adapterSalvato, adapterConfronto;
    RecyclerView.LayoutManager layoutManagerSalvato, layoutManagerConfronto;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confronto_comuni);

        // inizializzo il detector
        detector = new GestureDetector(this, this);

        prefs = getApplicationContext().getSharedPreferences("CountTownPrefs", MODE_PRIVATE);
        editor = prefs.edit();

        salvato = findViewById(R.id.comune_salvato);
        confronto = findViewById(R.id.comune_confronto);
        lsalvato = findViewById(R.id.linear_salvato);
        lconfronto = findViewById(R.id.linear_confronto);
        dati_salvato = findViewById(R.id.dati_salvato);
        dati_confronto = findViewById(R.id.dati_confronto);

        btnSalvato = findViewById(R.id.button_comune_salvato);
        btnConfronto = findViewById(R.id.button_comune_confronto);

        back = findViewById(R.id.benchmark_comuni_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        comuni = new String[2];

        c1_numero_appalti = findViewById(R.id.numero_appalti_salvato);
        c1_media_appalti = findViewById(R.id.media_appalti_salvato);
        c1_nome = findViewById(R.id.nome_comune_salvato);
        c2_numero_appalti = findViewById(R.id.numero_appalti_confronto);
        c2_media_appalti = findViewById(R.id.media_appalti_confronto);
        c2_nome = findViewById(R.id.nome_comune_confronto);

        titolo_toolbar = findViewById(R.id.benchmark_comuni_titolo);

        spinner = findViewById(R.id.spinner_benchmark);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.anniappalti, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cercaAppalti(comuni[0], comuni[1], spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        recyclerViewSalvato = findViewById(R.id.lista_appalti_salvato_recycler);
        layoutManagerSalvato = new LinearLayoutManager(this);
        recyclerViewSalvato.setLayoutManager(layoutManagerSalvato);
        adapterSalvato = new RecyclerViewAdapter(new ArrayList<Appalto>(), this);
        recyclerViewSalvato.setAdapter(adapterSalvato);

        recyclerViewConfronto = findViewById(R.id.lista_appalti_confronto_recycler);
        layoutManagerConfronto = new LinearLayoutManager(this);
        recyclerViewConfronto.setLayoutManager(layoutManagerConfronto);
        adapterConfronto = new RecyclerViewAdapter(new ArrayList<Appalto>(), this);
        recyclerViewConfronto.setAdapter(adapterConfronto);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        minus = size.x * 20 / 100;

        params_salvato = salvato.getLayoutParams();
        params_confronto = confronto.getLayoutParams();
        params_salvato.width = size.x - minus;
        salvato.setLayoutParams(params_salvato);

        lsalvato.setVisibility(View.GONE);
        dati_salvato.setVisibility(View.VISIBLE);


        comuni[0] = prefs.getString("savedCity", null);
        comuni[1] = getIntent().getExtras().getString("compareCity");

        btnSalvato.setText(comuni[0]);
        btnConfronto.setText(comuni[1]);

        refreshData();

        cercaAppalti(comuni[0], comuni[1], spinner.getSelectedItem().toString());
    }

    private void refreshData(){
        titolo_toolbar.setText(comuni[0] + "    --    " + comuni[1]);

        c1_nome.setText(comuni[0]);
        c1_numero_appalti.setText("Numero appalti: " + DatiComuni.appalti.size());
        float numAppSal = numAppaltiSalvato/18f;
        c1_media_appalti.setText(String.format("Media appalti: %.2f", numAppSal));

        c2_nome.setText(comuni[1]);
        c2_numero_appalti.setText("Numero appalti: " + DatiComuni.appalti_comune_confronto.size());
        float numAppCon = numAppaltiConfronto/18f;
        c2_media_appalti.setText(String.format("Media appalti: %.2f", numAppCon));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(), "Show Press gesture", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(), "Single Tap Up gesture", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        int x = (int) motionEvent.getX();
        int x_final = (int) motionEvent1.getX();

        // è stato effettuato uno swipe?
        params_salvato = salvato.getLayoutParams();
        params_confronto = confronto.getLayoutParams();
        if(x_final > minus && x_final < (size.x - minus - 12)) {
            params_salvato.width = x_final;
            params_confronto.width = size.x - x_final - 12;

            int limite_dati = size.x * 35 / 100;

            if(params_salvato.width > limite_dati && params_salvato.width < (size.x - limite_dati - 12)){
                dati_salvato.setVisibility(View.VISIBLE);
                lsalvato.setVisibility(View.GONE);
                lconfronto.setVisibility(View.GONE);
                dati_confronto.setVisibility(View.VISIBLE);
            }else if(params_salvato.width <= limite_dati){
                lsalvato.setVisibility(View.VISIBLE);
                dati_salvato.setVisibility(View.GONE);
            }else{
                lconfronto.setVisibility(View.VISIBLE);
                dati_confronto.setVisibility(View.GONE);
            }
        }
        salvato.setLayoutParams(params_salvato);
        confronto.setLayoutParams(params_confronto);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(), "LongPress gesture", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Toast.makeText(getApplicationContext(), "Fling gesture", Toast.LENGTH_SHORT).show();*/
        return true;
    }

    private void cercaAppalti(String c1, String c2, String anno) {
        if(c1 != null && !c1.equalsIgnoreCase("") && c2 != null && !c2.equalsIgnoreCase("")){
            new DetailsEchoAppalti().execute("https://overhw.com/counttown/scripts/appalti_details_anno.php?ncomune=" + c1 + "&anno=" + anno, "https://overhw.com/counttown/scripts/appalti_details_anno.php?ncomune=" + c2 + "&anno=" + anno);
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Errore").setMessage("Il nome della città non è corretto").setCancelable(false);
            dialog.setPositiveButton("Home", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent home = new Intent(ActivityConfrontoComuni.this, HomeActivity.class);
                    startActivity(home);
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }

        if(c1 == null){
            System.out.println("ERRORE: comune 1 = null");
        }
        else if(c2 == null){
            System.out.println("ERRORE: comune 2 = null");
        }
    }

    class DetailsEchoAppalti extends AsyncTask<String, Integer, String> {
        String[] appalti_comuni = new String[2];
        ProgressDialog pDialog;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityConfrontoComuni.this);
            pDialog.setTitle("Caricamento appalti...");
            pDialog.setMessage("\tAttendere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            for(int i=0; i<strings.length;i++) {
                try {
                    // Enter URL address where your php file resides
                    url = new URL(strings[i]);

                    // Setup HttpURLConnection class to send and receive data from php
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    return e1.toString();
                }

                try {
                    int response_code = conn.getResponseCode();
                    if (response_code == HttpURLConnection.HTTP_OK) {

                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        appalti_comuni[i] = result.toString();
                    } else {
                        appalti_comuni[i] = "unsuccessful";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.toString();
                } finally {
                    conn.disconnect();
                }
            }
            return "finished";
        }

        @Override
        protected void onPostExecute(String result) {

            DatiComuni.appalti.clear();
            DatiComuni.appalti_comune_confronto.clear();

            if(!appalti_comuni[0].equalsIgnoreCase("unsuccessful") && appalti_comuni != null) {
                String[] appalto = appalti_comuni[0].split("€");
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
                if (DatiComuni.appalti.size() >= 0) {
                    adapterSalvato = new RecyclerViewAdapter(DatiComuni.appalti, ActivityConfrontoComuni.this.getApplicationContext());
                    recyclerViewSalvato.swapAdapter(adapterSalvato, true);
                } else {
                    Snackbar.make(recyclerViewSalvato, "Nessun appalto trovato", Snackbar.LENGTH_SHORT).show();
                }
            }else{
                System.out.println("ERROR: APPALTI COMUNE 0 NON VALIDO");
            }
            if(!appalti_comuni[1].equalsIgnoreCase("unsuccessful") && appalti_comuni != null) {
                String[] appalto = appalti_comuni[1].split("€");
                System.out.println("NUMERO APPALTI: " + appalto.length);
                int i = 0;
                Appalto common;
                String[] tokens;
                while (i < appalto.length) {
                    tokens = appalto[i].split(";");
                    common = new Appalto(tokens);
                    if(common.getIdGara() != null) {
                        DatiComuni.appalti_comune_confronto.add(common);
                        System.out.println("ADD: " + common.getIdGara());
                    }
                    i++;
                }
                if (DatiComuni.appalti_comune_confronto.size() >= 0) {
                    adapterConfronto = new RecyclerViewAdapter(DatiComuni.appalti_comune_confronto, ActivityConfrontoComuni.this.getApplicationContext());
                    recyclerViewConfronto.swapAdapter(adapterConfronto, true);
                } else {
                    Snackbar.make(recyclerViewConfronto, "Nessun appalto trovato", Snackbar.LENGTH_SHORT).show();
                }
            }else {
                System.out.println("ERROR: APPALTI COMUNE 1 NON VALIDO");
            }


            pDialog.dismiss();
            //refreshData();

            new NumeroTotaleAppalti().execute("https://overhw.com/counttown/scripts/get_numero_appalti.php?comune=" + comuni[0], "https://overhw.com/counttown/scripts/get_numero_appalti.php?comune=" + comuni[1]);
        }
    }

    class NumeroTotaleAppalti extends AsyncTask<String, Integer, String> {
        String[] numTotAppaltiComuni = new String[2];
        ProgressDialog pDialog;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityConfrontoComuni.this);
            pDialog.setTitle("Calcolo appalti in corso...");
            pDialog.setMessage("\tAttendere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            for(int i = 0; i<strings.length;i++) {
                try {
                    // Enter URL address where your php file resides
                    url = new URL(strings[i]);

                    // Setup HttpURLConnection class to send and receive data from php
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    return e1.toString();
                }

                try {
                    int response_code = conn.getResponseCode();
                    if (response_code == HttpURLConnection.HTTP_OK) {

                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        numTotAppaltiComuni[i] = result.toString();
                    } else {
                        numTotAppaltiComuni[i] = "unsuccessful";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.toString();
                } finally {
                    conn.disconnect();
                }
            }
            return "finished";
        }

        @Override
        protected void onPostExecute(String result) {
            for(int i=0; i<2; i++){
                numAppaltiSalvato = Integer.parseInt(numTotAppaltiComuni[0]);
                numAppaltiConfronto = Integer.parseInt(numTotAppaltiComuni[1]);
            }

            pDialog.dismiss();
            refreshData();
        }
    }
}
