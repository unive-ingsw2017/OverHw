package com.overhw.counttown;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;


public class DatiInterniActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap googleMap;

    private Toolbar toolbar;
    private TextView citta;
    private ImageButton back;

    private TextView nome, istat, provincia, siglaProvincia, regione, areaGeo, popRes, popStr, densDem, supKmq, altezzaCentro, altezzaMin, altezzaMax, zonaAlti, zonaSismica, tipoComune, gradoUrban, indiceMont, zonaClim, classeComune, latitudine, longitudine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* se è possibile caricare la mappa allora usa il layout activity_dati_interni altrimenti usa un altro layout */
        if(googleServiceAvailable()){
            setContentView(R.layout.activity_dati_interni);
            initMap();
        }else{
            // No google maps layout
        }

        toolbar = findViewById(R.id.toolbar_dati_interni);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        nome = findViewById(R.id.nome_citta_interni);
        istat = findViewById(R.id.istat_interni);
        provincia = findViewById(R.id.provincia_interni);
        siglaProvincia = findViewById(R.id.sigla_provincia_interni);
        regione = findViewById(R.id.regione_interni);
        areaGeo = findViewById(R.id.area_geo_interni);
        popRes = findViewById(R.id.pop_residente_interni);
        popStr = findViewById(R.id.pop_straniera_interni);
        densDem = findViewById(R.id.densita_demo_interni);
        supKmq = findViewById(R.id.sup_kmq_interni);
        altezzaCentro = findViewById(R.id.altezza_centro_interni);
        altezzaMin = findViewById(R.id.altezza_minima_interni);
        altezzaMax = findViewById(R.id.altezza_massima_interni);
        zonaAlti = findViewById(R.id.zona_altimetrica_interni);
        zonaSismica = findViewById(R.id.zona_sismica_interni);
        tipoComune = findViewById(R.id.tipo_comune_interni);
        gradoUrban = findViewById(R.id.grado_urbanizzazione_interni);
        indiceMont = findViewById(R.id.indice_mintanita_interni);
        zonaClim = findViewById(R.id.zona_climatica_interni);
        classeComune = findViewById(R.id.classe_comune_interni);
        latitudine = findViewById(R.id.latitudine_interni);
        longitudine = findViewById(R.id.longitudine_interni);

        //back = findViewById(R.id.interni_back_button);
        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

        citta = findViewById(R.id.dati_interni_edittext_citta);
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(DatiComuni.dettagli_comune != null) {
            citta.setText(DatiComuni.dettagli_comune.getNome());
        }
        showData();
    }

    private void showData(){
        float lat = Float.parseFloat(DatiComuni.dettagli_comune.getLatitudine().replace(",", "."));
        float lng = Float.parseFloat(DatiComuni.dettagli_comune.getLongitudine().replace(",", "."));
        nome.setText(Html.fromHtml("<font color='#FF5722'><b>" + DatiComuni.dettagli_comune.getNome() + "</b></font>"));
        istat.setText(Html.fromHtml("<b>Codice ISTAT: </b>" + DatiComuni.dettagli_comune.getIstat()));
        provincia.setText(Html.fromHtml("<b>Provincia: </b>" + DatiComuni.dettagli_comune.getProvincia() + " (" + "<b>" + DatiComuni.dettagli_comune.getSiglaProvincia() + "</b>"+ ")"));
        siglaProvincia.setText(Html.fromHtml("<b>Sigla provincia: </b>" + DatiComuni.dettagli_comune.getSiglaProvincia()));
        regione.setText(Html.fromHtml("<b>Regione: </b>" + DatiComuni.dettagli_comune.getRegione()));
        areaGeo.setText(Html.fromHtml("<b>Area geografica: </b>" + DatiComuni.dettagli_comune.getAreaGeo()));
        popRes.setText(Html.fromHtml("<b>Popolazione residente: </b>" + DatiComuni.dettagli_comune.getPopolazioneResidente() + " ab."));
        popStr.setText(Html.fromHtml("<b>Popolazione straniera: </b>" + DatiComuni.dettagli_comune.getPopolazioneStraniera() + " ab."));
        densDem.setText(Html.fromHtml("<b>Densità demografica: </b>" + DatiComuni.dettagli_comune.getDensitaDemografica() + " ab./km²"));
        supKmq.setText(Html.fromHtml("<b>Superficie: </b>" + DatiComuni.dettagli_comune.getSuperficieKmq() + " km²"));
        altezzaCentro.setText(Html.fromHtml("<b>Altezza centro: </b>" + DatiComuni.dettagli_comune.getAltezzaCentro() + " m s.l.m."));
        altezzaMin.setText(Html.fromHtml("<b>Altezza minima: </b>" + DatiComuni.dettagli_comune.getAltezzaMinima() + " m s.l.m."));
        altezzaMax.setText(Html.fromHtml("<b>Altezza massima: </b>" + DatiComuni.dettagli_comune.getAltezzaMassima() + " m s.l.m."));
        zonaAlti.setText(Html.fromHtml("<b>Zona altimetrica: </b>" + DatiComuni.dettagli_comune.getZonaAltimetrica()));
        tipoComune.setText(Html.fromHtml("<b>Tipo comune: </b>" + DatiComuni.dettagli_comune.getTipoComune()));
        gradoUrban.setText(Html.fromHtml("<b>Grado urbanizzazione: </b>" + DatiComuni.dettagli_comune.getGradoUrbanizzazione()));
        indiceMont.setText(Html.fromHtml("<b>Indice montanità: </b>" + DatiComuni.dettagli_comune.getIndiceMontanita()));
        zonaClim.setText(Html.fromHtml("<b>Zona climatica: </b>" + DatiComuni.dettagli_comune.getZonaClimatica()));
        zonaSismica.setText(Html.fromHtml("<b>Zona sismica: </b>" + DatiComuni.dettagli_comune.getZonaSismica()));
        classeComune.setText(Html.fromHtml("<b>Classe comune: </b>" + DatiComuni.dettagli_comune.getClasseComune()));
        latitudine.setText(Html.fromHtml("<b>Latitudine: </b>" + String.format(Locale.ITALY,"%.2f", lat)));
        longitudine.setText(Html.fromHtml("<b>Longitudine: </b>" + String.format(Locale.ITALY,"%.2f", lng)));
    }

    public boolean googleServiceAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }
        else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else{
            Toast.makeText(this, "Impossibile connettersi a Google Play Services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        this.googleMap = googleMap;
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);
        if(DatiComuni.dettagli_comune != null && DatiComuni.dettagli_comune.getLatitudine() != null){
            float lat = Float.parseFloat(DatiComuni.dettagli_comune.getLatitudine().replace(",", "."));
            float lng = Float.parseFloat(DatiComuni.dettagli_comune.getLongitudine().replace(",", "."));
            LatLng ll = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(ll).title(DatiComuni.dettagli_comune.getNome()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
            googleMap.moveCamera(update);
        }
    }

    /*private void goToLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        if(googleMap!=null) {
            googleMap.addMarker(new MarkerOptions().position(ll).title(DatiComuni.dettagli_comune.getNome()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
            googleMap.moveCamera(update);
        }
        else{
            Toast.makeText(getApplicationContext(), "Errore mappa", Toast.LENGTH_SHORT).show();
        }
    }*/
}
