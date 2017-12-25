package org.imgt.asteroides;

import java.util.List;
import java.util.Vector;

public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones{

    //private Vector<String> puntuaciones;
    private List<String> puntuaciones;

    public AlmacenPuntuacionesArray(){
        puntuaciones = new Vector<String>();
        puntuaciones.add("123000 Pepito Domínguez");
        puntuaciones.add("111000 Pedro Martínez");
        puntuaciones.add("011000 Paco Pérez");
    }

    @Override public void guardarPuntuacion(int puntos, String nombre, long fecha){
        puntuaciones.add(0, puntos +" "+ nombre);
    }

    /*@Override public Vector<String> listaPuntuaciones(int cantidad){
        return puntuaciones;
    }*/
    @Override public List<String> listaPuntuaciones(int cantidad){
        return puntuaciones;
    }
}
