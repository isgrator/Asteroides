package org.imgt.asteroides;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
    }
}
