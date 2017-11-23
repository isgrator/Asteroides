package org.imgt.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VistaJuego extends View implements SensorEventListener{

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
        Drawable drawableNave, drawableAsteroide, drawableMisil;

        //registro del sensor( TYPE_ORIENTATION si fuese de orientación)
        SensorManager mSensorManager= (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors= mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!listSensors.isEmpty()){
            Sensor orientationSensor= listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor,SensorManager.SENSOR_DELAY_GAME);
        }

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
        } else {
            //Código para dibujar los asteroides a partir de archivos png
            drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);
            setLayerType(View.LAYER_TYPE_HARDWARE,null);
            //Nave con gráfico png
            drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);

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

    @Override synchronized protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Grafico asteroide: asteroides) {
            asteroide.dibujaGrafico(canvas);
        }
        nave.dibujaGrafico(canvas);
    }

    //Código de la unidad 5 - Hilos de ejecución
    synchronized protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;    // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;
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
    }

    class ThreadJuego extends Thread {
        @Override
        public void run() {
            while (true) {
                actualizaFisica();
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
                    //activaMisil();
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
        if(!controlPorSensor) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    disparo = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 1 && dx > 1) {
                        giroNave = Math.round((x - mX) / 2);
                        disparo = false;
                    } else if (dx < 6 && dy > 6) {
                        //Con el valor absoluto logro que no decelere la nave
                        //Sólo se puede decelerar girando primero 180º y acelerando a continuación.
                        aceleracionNave = Math.abs(Math.round((mY - y) / 10));
                        disparo = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    giroNave = 0;
                    aceleracionNave = 0;
                    if (disparo) {
                        //activaMisil();
                    }
                    break;
            }
            mX = x;
            mY = y;
        }
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
                valorInicialAceleracion = valorGiro;
                hayValorInicialAceleracion = true;
            }
            aceleracionNave = Math.abs(Math.round((valorAceleracion - valorInicialAceleracion) / 17));
        }
    }



}
