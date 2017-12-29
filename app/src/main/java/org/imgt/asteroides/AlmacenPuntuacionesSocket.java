package org.imgt.asteroides;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Isabel María on 28/12/2017.
 */

public class AlmacenPuntuacionesSocket implements AlmacenPuntuaciones{

    public AlmacenPuntuacionesSocket() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha){
        try {
            //Socket sk = new Socket("X.X.X.X", 1234);
            Socket sk = new Socket("158.42.146.127", 1234);
            //Socket sk = new Socket("10.0.2.2", 1234);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(sk.getInputStream()));
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(sk.getOutputStream()),true);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaFormateada = new Date(fecha);

            salida.println(puntos + " " + nombre+" "+sdf.format(fechaFormateada));
            String respuesta = entrada.readLine();
            if (!respuesta.equals("OK")) {
                Log.e("Asteroides", "Error: respuesta de servidor incorrecta");
            }
            sk.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.toString(), e);
        }
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        try {
            //Socket sk = new Socket("X.X.X.X", 1234);
            Socket sk = new Socket("158.42.146.127", 1234);
            //Socket sk = new Socket("10.0.2.2", 1234);
            BufferedReader entrada =    new BufferedReader(
                    new InputStreamReader(sk.getInputStream()));
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(sk.getOutputStream()),true);
            salida.println("PUNTUACIONES");
            int n = 0;
            String respuesta;
            do {
                respuesta = entrada.readLine();
                if (respuesta != null) {
                    result.add(respuesta);
                    n++;
                }
            } while (n < cantidad && respuesta != null);
            sk.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.toString(), e);
        }
        return result;
    }
}
