package com.david.fixittecnic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class FirmaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma);

        View btnVolver = findViewById(R.id.btnVolverFirma);
        View btnLimpiar = findViewById(R.id.btnLimpiarFirma);
        View btnGuardar = findViewById(R.id.btnGuardarFirma);

        LienzoFirma lienzo = findViewById(R.id.lienzoDibujo);

        btnVolver.setOnClickListener(v -> finish());
        btnLimpiar.setOnClickListener(v -> lienzo.limpiarLienzo());

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