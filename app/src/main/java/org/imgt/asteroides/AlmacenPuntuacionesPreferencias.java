package org.imgt.asteroides;

import android.content.Context;
import android.content.SharedPreferences;

import org.imgt.asteroides.AlmacenPuntuaciones;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Isabel María on 25/12/2017.
 */

public class AlmacenPuntuacionesPreferencias implements AlmacenPuntuaciones {

    private static String PREFERENCIAS = "puntuaciones";
    private Context context;

    public AlmacenPuntuacionesPreferencias(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {

        SharedPreferences preferencias = context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        for (int n = 9; n >= 1; n--) {
            editor.putString("puntuacion" + n,
                    preferencias.getString("puntuacion" + (n - 1), ""));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaFormateada = new Date(fecha);

        editor.putString("puntuacion0", puntos + " " + nombre + " " + sdf.format(fechaFormateada));
        editor.apply();
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        SharedPreferences preferencias =context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        for (int n = 0; n <= 9; n++) {
            String s = preferencias.getString("puntuacion" + n, "");
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return result;
    }
}
