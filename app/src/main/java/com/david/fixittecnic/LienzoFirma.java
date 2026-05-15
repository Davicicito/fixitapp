package com.david.fixittecnic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Componente visual personalizado que funciona como un lienzo de dibujo tactil.
 * Esta clase detecta los movimientos del dedo sobre la pantalla para dibujar trazos
 * suaves que representan la firma del cliente, permitiendo tambien limpiar el contenido
 * o convertir el resultado en una imagen de tipo mapa de bits.
 */
public class LienzoFirma extends View {

    private Paint pincel;
    private Path trazo;

    /**
     * Configura las herramientas de dibujo al crear el lienzo.
     * Define el color oscuro, el grosor del trazo y suaviza los bordes para que la firma
     * parezca hecha con un boligrafo real sobre papel.
     *
     * @param context El entorno de la aplicacion.
     * @param attrs Atributos de diseño definidos en el archivo XML.
     */
    public LienzoFirma(Context context, AttributeSet attrs) {
        super(context, attrs);
        trazo = new Path();
        pincel = new Paint();
        pincel.setAntiAlias(true);
        pincel.setColor(Color.parseColor("#0F172A"));
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeJoin(Paint.Join.ROUND);
        pincel.setStrokeCap(Paint.Cap.ROUND);
        pincel.setStrokeWidth(8f);
    }

    /**
     * Dibuja los trazos acumulados en la pantalla del dispositivo.
     * @param canvas El lienzo sobre el que se realiza el dibujo visual.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(trazo, pincel);
    }

    /**
     * Gestiona los eventos tactiles del usuario.
     * Registra cuando el dedo toca la pantalla para iniciar un trazo y cuando se desliza
     * para dibujar las lineas de la firma, refrescando la vista constantemente.
     *
     * @param event El objeto que contiene la informacion de la posicion del dedo.
     * @return Verdadero si el evento ha sido procesado correctamente.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                trazo.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                trazo.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    /**
     * Borra por completo todos los dibujos realizados en el lienzo.
     */
    public void limpiarLienzo() {
        trazo.reset();
        invalidate();
    }

    /**
     * Comprueba si el lienzo todavia no contiene ningun trazo.
     * @return Verdadero si no se ha dibujado nada sobre el componente.
     */
    public boolean estaVacio() {
        return trazo.isEmpty();
    }

    /**
     * Transforma el dibujo realizado en una imagen digital con fondo blanco.
     * Este metodo crea un mapa de bits con las mismas dimensiones que el componente
     * para que pueda ser guardado o enviado a traves de internet.
     *
     * @return Un objeto de tipo imagen con la firma capturada.
     */
    public Bitmap obtenerFirma() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        this.draw(canvas);
        return bitmap;
    }
}