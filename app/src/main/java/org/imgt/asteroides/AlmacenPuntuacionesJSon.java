package org.imgt.asteroides;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Isabel María on 27/12/2017.
 */

public class AlmacenPuntuacionesJSon implements AlmacenPuntuaciones {
    //private String string; //Almacena puntuaciones en formato JSON
    private static String PUNTOS = "puntuacionesjson";
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public AlmacenPuntuacionesJSon(Context context) {
        this.context = context;
        guardarPuntuacion(45000,"Mi nombre", System.currentTimeMillis());
        guardarPuntuacion(31000,"Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJSon(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJSon(puntuaciones);
        guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJSon(string);
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion: puntuaciones) {
            Date fechaFormateada = new Date(puntuacion.getFecha());
            salida.add(puntuacion.getPuntos()+" "+puntuacion.getNombre()+" "+ sdf.format(fechaFormateada));
        }
        return salida;
    }

    private String guardarJSon(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacion> leerJSon(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        try {
            JSONArray json_array = new JSONArray(string);
            for (int i = 0; i < json_array.length(); i++) {
                JSONObject objeto = json_array.getJSONObject(i);
                puntuaciones.add(new Puntuacion(objeto.getInt("puntos"),
                        objeto.getString("nombre"), objeto.getLong("fecha")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
    }

    //Métodos para guardar y leer el json (esto es experimental. no funciona aún)
    //Métodos para guardar y leer el json
    public void guardarString(String s){
        SharedPreferences preferencias = context.getSharedPreferences(
                PUNTOS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("puntuacionesJSon",s);

        editor.apply();
    }

    public String leerString(){
        String result;
        SharedPreferences preferencias =context.getSharedPreferences(PUNTOS, Context.MODE_PRIVATE);
        result = preferencias.getString("puntuacionesJSon","");
        return result;
    }
}