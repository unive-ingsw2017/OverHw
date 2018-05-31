package com.overhw.counttown;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{

    public TextView tipoIntervento;
    public TextView idGara;
    public TextView dataPubblicazione;
    public TextView denomStazApp;

    private ItemClickListener itemClickListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        idGara = itemView.findViewById(R.id.item_appalto_id_gara);
        dataPubblicazione = itemView.findViewById(R.id.item_appalto_data_pubb);
        tipoIntervento = itemView.findViewById(R.id.item_appalto_tipo_intervento);
        denomStazApp = itemView.findViewById(R.id.item_appalto_denom_staz_appaltante);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<Appalto> listAppalti = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(ArrayList<Appalto> listAppalti, Context context) {
        this.listAppalti = listAppalti;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_appalto, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tipoIntervento.setText(Html.fromHtml("<b>Tipo bando: </b>" + listAppalti.get(position).getTipoBando()));
        holder.idGara.setText(Html.fromHtml("<b>Id gara: </b>" + listAppalti.get(position).getIdGara()));
        String data = listAppalti.get(position).getDataPubbBandoScp();
        String newData = data.substring(9,11) + "/" + data.substring(6,8) + "/" + data.substring(1,5);
        holder.dataPubblicazione.setText(Html.fromHtml("<b>Data: </b>" + newData));
        holder.denomStazApp.setText(Html.fromHtml("<b>Denominazione stazione appaltante: </b><br/>\n" + listAppalti.get(position).getDenominazioneStazioneAppaltante()));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    //Toast.makeText(context, "Long Click " + listAppalti.get(position).getIdGara(), Toast.LENGTH_SHORT).show();
                }else{
                    // passaggio alla scheda dettagli appalto
                    Intent dettagliAppalto = new Intent(context.getApplicationContext(), DettagliAppaltoActivity.class);
                    dettagliAppalto.putExtra("Appalto", position);
                    context.startActivity(dettagliAppalto);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAppalti.size();
    }
}
