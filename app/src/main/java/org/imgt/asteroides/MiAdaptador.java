package org.imgt.asteroides;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

public class MiAdaptador extends RecyclerView.Adapter<MiAdaptador.ViewHolder> {

    private LayoutInflater inflador;    //El inflador permite crear una vista a partior del XML
    //private Vector<String> lista;
    private List<String> lista;
    protected View.OnClickListener onClickListener;
    private SharedPreferences pref;

    public MiAdaptador(Context context, List<String> lista){ //{public MiAdaptador(Context context, Vector<String> lista) {
        this.lista = lista;
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //ViewHolder contiene las vistas que queremos modificar de un elemento
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_lista, parent, false);
        v.setOnClickListener(onClickListener); //Aplica el escuchador a cada vista
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        String[] parts = lista.get(i).split(" ");
        if(pref.getString("almacenamiento", "0").equals("10")
                || pref.getString("almacenamiento", "0").equals("11")) {
            holder.titulo.setText(lista.get(i));
        }else {
            holder.titulo.setText(parts[0] +" "+ parts[1]);
            holder.subtitutlo.setText(parts[2]);
        }
        switch (Math.round((float)Math.random()*3)){
            case 0:
                holder.icon.setImageResource(R.drawable.asteroide1);
                break;
            case 1:
                holder.icon.setImageResource(R.drawable.asteroide2);
                break;
            default:
                holder.icon.setImageResource(R.drawable.asteroide3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, subtitutlo;
        public ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView)itemView.findViewById(R.id.titulo);
            subtitutlo = (TextView)itemView.findViewById(R.id.subtitulo);
            icon = (ImageView)itemView.findViewById(R.id.icono);
        }
    }

    //Código para el ejercicio: Selección de un elemento en un RecyclerView (pag.168)
    public void setOnItemClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

}
