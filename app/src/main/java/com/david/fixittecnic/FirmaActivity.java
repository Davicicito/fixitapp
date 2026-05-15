package com.david.fixittecnic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

/**
 * Pantalla dedicada a la captura de la firma digital del cliente.
 * Esta actividad permite que el usuario dibuje sobre la pantalla tactil,
 * procesa el dibujo para reducir su tamaño y optimizar el envio, y devuelve
 * la imagen resultante a la pantalla de finalizacion de trabajo.
 */
public class FirmaActivity extends AppCompatActivity {

    /**
     * Configura la interfaz de la firma al iniciar la actividad.
     * Enlaza el lienzo de dibujo y los botones para borrar el contenido,
     * cancelar la operacion o guardar el resultado final.
     *
     * @param savedInstanceState Datos guardados de la sesion anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma);

        View btnVolver = findViewById(R.id.btnVolverFirma);
        View btnLimpiar = findViewById(R.id.btnLimpiarFirma);
        View btnGuardar = findViewById(R.id.btnGuardarFirma);

        LienzoFirma lienzo = findViewById(R.id.lienzoDibujo);

        /**
         * Cierra la pantalla actual y regresa a la anterior sin guardar cambios.
         */
        btnVolver.setOnClickListener(v -> finish());

        /**
         * Borra todos los trazos del lienzo para que el cliente pueda repetir su firma.
         */
        btnLimpiar.setOnClickListener(v -> lienzo.limpiarLienzo());

        /**
         * Procesa la firma capturada para convertirla en un formato de datos manejable.
         * Verifica que el lienzo no este vacio, escala la imagen para que no pese demasiado
         * y la transforma en un arreglo de bytes para enviarla de vuelta.
         */
        btnGuardar.setOnClickListener(v -> {
            if (lienzo.estaVacio()) {
                Toast.makeText(this, "El cliente debe firmar primero", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap firmaOriginal = lienzo.obtenerFirma();

                int ancho = 400;
                int alto = 400 * firmaOriginal.getHeight() / firmaOriginal.getWidth();
                Bitmap firmaReducida = Bitmap.createScaledBitmap(firmaOriginal, ancho, alto, true);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                firmaReducida.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("firma_imagen_array", byteArray);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}