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

public class LienzoFirma extends View {

    private Paint pincel;
    private Path trazo;

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(trazo, pincel);
    }

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

    public void limpiarLienzo() {
        trazo.reset();
        invalidate();
    }

    public boolean estaVacio() {
        return trazo.isEmpty();
    }

    public Bitmap obtenerFirma() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        this.draw(canvas);
        return bitmap;
    }
}