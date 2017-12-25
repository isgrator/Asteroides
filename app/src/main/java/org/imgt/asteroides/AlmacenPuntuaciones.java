package org.imgt.asteroides;

import java.util.List;
import java.util.Vector;

public interface AlmacenPuntuaciones {
    //Guarda la puntuación de una partida. Parámetros: puntuación, nombre jugador, fecha partida
    public void guardarPuntuacion(int puntos, String nombre, long fecha);
    //Obtiene lista de puntuaciones previamente guardadas Parámetro: cantidad -> núm. máximo de puntuaciones que ha de devolver
    //public Vector<String> listaPuntuaciones( int cantidad);
    public List<String> listaPuntuaciones(int cantidad);
}
