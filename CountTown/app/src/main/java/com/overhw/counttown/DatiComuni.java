package com.overhw.counttown;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;


public class DatiComuni extends AppCompatActivity{

    /* dettagli singolo comune */
    protected static Comune dettagli_comune = null;

    /* lista dei nomi dei comuni */
    protected static ArrayList<String> nomi_citta = new ArrayList<>();

    /* lista degli appalti per il comune salvato in dettagli_comune */
    protected static ArrayList<Appalto> appalti = new ArrayList<>();

    /* lista di 5 comuni utilizzati dal benchmark */
    protected static String[] ben_comuni = new String[5];

    /* lista appalti per il comune selezionato da confrontare con il comune salvato */
    protected static ArrayList<Appalto> appalti_comune_confronto = new ArrayList<>();
}
