package org.imgt.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VistaJuego extends View implements SensorEventListener{

    //PUNTUACIONES//////
    private int puntuacion =0;
    private Activity padre;

    //MULTIMEDIA ///////
    SoundPool soundPool;
    int idDisparo, idExplosion;


    // //// MISIL //////
    private Vector<Grafico> misiles;
    private Drawable drawableMisil;
    private AnimationDrawable animationDrawableMisil;
    private static int PASO_VELOCIDAD_MISIL = 12;
    private Vector<Integer> tiempoMisiles;

    //Manejo táctil de la nave
    private boolean controlPorSensor= false;
    private float mX=0, mY=0;
    private boolean disparo=false;
    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // //// ASTEROIDES //////
    private List<Grafico> asteroides; // Lista con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide



    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableAsteroide;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);

        //registro del sensor( TYPE_ORIENTATION si fuese de orientación)
        /*SensorManager mSensorManager= (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors= mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!listSensors.isEmpty()){
            Sensor orientationSensor= listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor,SensorManager.SENSOR_DELAY_GAME);
        }*/

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (pref.getString("graficos", "1").equals("0")) {
            //Código para dibujar los asteroides con representación vectorial
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            ShapeDrawable dAsteroide = new ShapeDrawable(
                    new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroide.setIntrinsicWidth(50);
            dAsteroide.setIntrinsicHeight(50);
            drawableAsteroide = dAsteroide;

            //Código para representar vectorialmente la nave
            Path pathNave = new Path();
            pathNave.moveTo((float) 0.0, (float) 0.0);
            pathNave.lineTo((float) 1.0, (float) 0.5);
            pathNave.lineTo((float) 0.0, (float) 1.0);
            pathNave.lineTo((float) 0.0, (float) 0.0);
            ShapeDrawable dNave = new ShapeDrawable(
                    new PathShape(pathNave, 1, 1));
            dNave.getPaint().setColor(Color.YELLOW);
            dNave.getPaint().setStyle(Paint.Style.STROKE);
            dNave.setIntrinsicWidth(25);
            dNave.setIntrinsicHeight(15);
            drawableNave = dNave;

            setBackgroundColor(Color.BLACK);
            setLayerType(View.LAYER_TYPE_SOFTWARE,null);

            //Creando el misil vectorial
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;
        } else {
            //Código para dibujar los asteroides a partir de archivos png
            drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);
            setLayerType(View.LAYER_TYPE_HARDWARE,null);
            //Nave con gráfico png
            drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);
            //drawableMisil = ContextCompat.getDrawable(context, R.drawable.misil1);
            animationDrawableMisil = (AnimationDrawable) ContextCompat.getDrawable(context,R.drawable.animacion);
            ImageView vista= new ImageView(context);
            vista.setImageDrawable(animationDrawableMisil);
            animationDrawableMisil.start();

        }



        nave = new Grafico(this, drawableNave);

        asteroides = new ArrayList<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }


        misiles= new Vector<Grafico>();
        tiempoMisiles= new Vector<Integer>();



        //Configuración de tipo de entrada en preferencia (pág.261)
        //pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (pref.getString("controles", "1").equals("0")) {
            //Activar control de la nave por sensores
            controlPorSensor=true;
        }else{
            //Activar control por pantalla táctil
            controlPorSensor=false;
        }
    }

    @Override protected void onSizeChanged(int ancho, int alto,
                                           int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        //Nave en el centro de la pantalla
        nave.setCenX((int) ancho/2);
        nave.setCenY((int) alto/2);
        for (Grafico asteroide: asteroides) {
            //Código para evitar que ningún asteroide aparezca de forma inicial en el área que ocupa la nave
            do {
                asteroide.setCenX((int) (Math.random()*ancho));
                asteroide.setCenY((int) (Math.random()*alto));
            } while(asteroide.distancia(nave) < (ancho+alto)/5);

        }
        ultimoProceso= System.currentTimeMillis();
        thread.start();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }

        nave.dibujaGrafico(canvas);

        synchronized (misiles){
            for (Grafico misil: misiles){
                misil.dibujaGrafico(canvas);
            }

        }

    }

    //Código de la unidad 5 - Hilos de ejecución
     protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;    // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;   //factorMov = retardo
        ultimoProceso = ahora; // Para la próxima vez

        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));
        double nIncX = nave.getIncX() + aceleracionNave *
                Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave *
                Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;

        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX,nIncY) <= MAX_VELOCIDAD_NAVE){
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(factorMov); // Actualizamos posición
        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(factorMov);
        }

        // Actualizamos posición de misil

         synchronized (misiles){
            for(int m=0; m < misiles.size(); m++){
                misiles.get(m).incrementaPos(factorMov);
                tiempoMisiles.set(m, tiempoMisiles.get(m)-(int)factorMov);
                if(tiempoMisiles.get(m) <0){
                    misiles.remove(m);
                    tiempoMisiles.remove(m);
                }else{
                    for (int i = 0; i < asteroides.size(); i++)
                        if (misiles.get(m).verificaColision(asteroides.get(i))) {
                            destruyeAsteroide(i);
                            misiles.remove(m);
                            tiempoMisiles.remove(m);
                            break;
                        }
                }
            }

         }

         //Para la puntuación
         for(Grafico asteroide : asteroides){
             if(asteroide.verificaColision(nave)){
                 salir();
             }
         }

    }

    class ThreadJuego extends Thread {
        private boolean pausa,corriendo;

        public synchronized void pausar() {
            pausa = true;
        }

        public synchronized void reanudar() {
            //Esto permite que se pare el movimiento cuando pasamos a segundo plano.
            //Sin esta línea, la app sigue moviendo los asteroides etc.
            ultimoProceso = System.currentTimeMillis();
            pausa = false;
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override   public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    //Código de uso del teclado (5.3)
    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;


            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = +PASO_ACELERACION_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    giroNave = -PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = +PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    activaMisil();
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }

        return procesada;
    }

    @Override public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = 0;
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }
        return procesada;
    }


    //Código para manejo dela nave con pantalla táctil (5.4.2)
    /*
        movimiento horizontal -> gira la nave
        movimiento vertical hacia arriba -> acelera nave
        movimiento vertical hacia abajo -> decelera la nave
     */

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        if(!controlPorSensor) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 1 && dx > 1) {
                        giroNave = Math.round((x - mX) / 2);
                        disparo = false;
                    } else if (dx < 6 && dy > 6) {
                        //Con el valor absoluto logro que no decelere la nave
                        //Sólo se puede decelerar girando primero 180º y acelerando a continuación.
                        aceleracionNave = Math.abs(Math.round((mY - y) / 13));
                        disparo = false;
                    }
                    break;
            }

        }
        //Siempre se dispara tocando la pantalla
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    activaMisil();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    //Implementación de la interfaz SensorEventListener
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private boolean hayValorInicialGiro = false;
    private boolean hayValorInicialAceleracion = false;
    private float valorInicialGiro, valorInicialAceleracion;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(controlPorSensor) {
            float valorGiro = event.values[1]; //Cogemos el eje Y

            if (!hayValorInicialGiro) {
                valorInicialGiro = valorGiro;
                hayValorInicialGiro = true;
            }
            giroNave = (int) Math.round((valorGiro - valorInicialGiro) / 2);

            //Aceleración de la nave con sensores
            float valorAceleracion = event.values[0];  //Eje Z
            if (!hayValorInicialAceleracion) {
                valorInicialAceleracion = valorAceleracion;
                hayValorInicialAceleracion = true;
            }
            aceleracionNave = Math.abs(Math.round((valorAceleracion - valorInicialAceleracion) / 13));
        }
    }

    //Métodos para los misiles
    private void destruyeAsteroide(int i) {
        synchronized (asteroides) {
            asteroides.remove(i);
            //misilActivo = false;
            soundPool.play(idExplosion, 1 ,1 , 0, 0, 1);
            puntuacion += 1000;
        }
        this.postInvalidate();
        if(asteroides.isEmpty()){
            salir();
        }
    }
    private void activaMisil() {

        Grafico misil;

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (pref.getString("graficos", "1").equals("0")) {
            misil= new Grafico(this,drawableMisil);
        }else{
            misil= new Grafico(this,animationDrawableMisil);
        }




        misil.setCenX(nave.getCenX());
        misil.setCenY(nave.getCenY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);

        misiles.add(misil);
        tiempoMisiles.add((int) Math.min(this.getWidth() / Math.abs( misil.
                getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2);

        soundPool.play(idDisparo, 1, 1, 1, 0, 1);

    }

    public ThreadJuego getThread() {
        return thread;
    }

    //Desactivar los sensores con los eventos del ciclo de vida pág. 276
    public void activarSensores(){

        SensorManager mSensorManager= (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors= mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!listSensors.isEmpty()){
            Sensor orientationSensor= listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor,SensorManager.SENSOR_DELAY_GAME);
        }

    }

    public void desactivarSensores(){
        SensorManager mSensorManager= (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
    }

    public void setPadre(Activity padre){
        this.padre = padre;
    }

    //Método para guardar la puntuación al detectar victoria o derrota. (u9 9.2)
    private void salir(){
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }

}
