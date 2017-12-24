package org.imgt.asteroides;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Juego extends Activity {

    private VistaJuego vistaJuego;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
    }

    //Métodos*************************************
    @Override protected void onPause() {
        vistaJuego.getThread().pausar();
        vistaJuego.desactivarSensores();
        super.onPause();
        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
        vistaJuego.activarSensores();
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

}
