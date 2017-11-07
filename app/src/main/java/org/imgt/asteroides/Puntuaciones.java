package org.imgt.asteroides;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

//Esta actividad se limkta a visualizar el RecyclerView
public class Puntuaciones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MiAdaptador adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones); //Asignación del layout correspondiente a esta clase
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adaptador = new MiAdaptador(this,
                MainActivity.almacen.listaPuntuaciones(10));
        recyclerView.setAdapter(adaptador);  //Asigna al recyclerview nuestro adaptador
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Asignación de un escuchador
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getChildAdapterPosition indica la posición de una vista dentro del adaptador
                int pos= recyclerView.getChildAdapterPosition(v);
                String s= MainActivity.almacen.listaPuntuaciones(10).get(pos);
                Toast.makeText(Puntuaciones.this,  getResources().getString(R.string.seleccion) +": " + pos + " - "
                        + s, Toast.LENGTH_LONG).show();
            }
        });
    }
}
