package org.imgt.asteroides;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Isabel María on 27/12/2017.
 */

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper
        implements AlmacenPuntuaciones{
    public AlmacenPuntuacionesSQLite(Context context) {
        super(context, "puntuaciones", null, 1);
    }
    //Métodos de SQLiteOpenHelper
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "puntos INTEGER, nombre TEXT, fecha BIGINT)");
    }

    @Override    public void onUpgrade(SQLiteDatabase db,
                                       int oldVersion, int newVersion) {
        // En caso de una nueva versión habría que actualizar las tablas
    }

    //Métodos de AlmacenPuntuaciones
    public void guardarPuntuacion(int puntos, String nombre,
                                  long fecha) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO puntuaciones VALUES ( null, "+
                puntos+", '"+nombre+"', "+fecha+")");
        db.close();
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        /* //(pág. 411 9.7)
        Cursor cursor = db.rawQuery("SELECT puntos, nombre, fecha FROM " +
                "puntuaciones ORDER BY puntos DESC LIMIT " +cantidad, null);
         */
        //pág.414 9.7.1)
        String[] CAMPOS = {"puntos", "nombre" , "fecha"};
        Cursor cursor = db.query("puntuaciones", CAMPOS, null, null,
                null, null, "puntos DESC", Integer.toString(cantidad));
        while (cursor.moveToNext()){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            BigInteger largeValueFecha = new BigInteger(cursor.getString(2));
            long longValueFecha = largeValueFecha.longValue();

            Date fechaFormateada = new Date(longValueFecha);
            result.add(cursor.getInt(0)+" " +cursor.getString(1)+" "+sdf.format(fechaFormateada));
        }
        cursor.close();
        db.close();
        return result;
    }
}
