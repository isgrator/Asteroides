package org.imgt.asteroides;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Isabel María on 25/12/2017.
 */

public class AlmacenPuntuacionesFicheroExterno implements AlmacenPuntuaciones {
    private static String FICHERO = Environment.getExternalStorageDirectory() + "/puntuaciones.txt";
    private Context context;

    public AlmacenPuntuacionesFicheroExterno(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha){

        //comprueba el estado de acceso a la memoria externa
        String stadoSD = Environment.getExternalStorageState();
        boolean extraible= Environment.isExternalStorageRemovable();
        if(!stadoSD.equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(context, context.getString(R.string.error_escritura), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            FileOutputStream f = new FileOutputStream(FICHERO, true);
            String texto = puntos + " " + nombre + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    public List<String> listaPuntuaciones(int cantidad) {

        List<String> result = new ArrayList<String>();
        //comprueba el estado de acceso a la memoria externa
        String stadoSD = Environment.getExternalStorageState();

        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, context.getString(R.string.error_lectura),
                    Toast.LENGTH_LONG).show();
            return result;
        }

        try {
            FileInputStream f = new FileInputStream(FICHERO);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = entrada.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < cantidad && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }

    //Método para verificar el acceso a la memoria externa
}
