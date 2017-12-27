package org.imgt.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AlmacenPuntuacionesGson implements AlmacenPuntuaciones{
    private static String PREFERENCIAS = "puntuacionesgson";
    private Context context;
    //private String string; //Almacena puntuaciones en formato JSON

    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {}.getType();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public AlmacenPuntuacionesGson(Context context) {
        this.context= context;
        guardarPuntuacion(45000,"Mi nombre", System.currentTimeMillis());
        guardarPuntuacion(31000,"Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();
        Clase objeto;
        if (string == null) {
            objeto= new Clase();
        } else {

            objeto = gson.fromJson(string, type);
        }

        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(objeto, type);
        guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        Clase objeto;
        if (string == null) {
            objeto =new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            Date fechaFormateada = new Date(puntuacion.getFecha());
            salida.add(puntuacion.getPuntos()+" "+puntuacion.getNombre()+ " "+ sdf.format(fechaFormateada));
        }
        return salida;
    }

    //Métodos para guardar y leer el json
    public void guardarString(String s){
        SharedPreferences preferencias = context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("puntuacionesGSon",s);

        editor.apply();
    }

    public String leerString(){
        String result;
        SharedPreferences preferencias =context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        result = preferencias.getString("puntuacionesGSon","");
        return result;
    }

    //Código para guardar una clase en JSON con la librería Gson
    public class Clase{
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;

    }

}


