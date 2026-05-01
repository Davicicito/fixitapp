package com.david.fixittecnic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView lblNombrePerfil = findViewById(R.id.lblNombrePerfil);
        TextView lblEspecialidadPerfil = findViewById(R.id.lblEspecialidadPerfil); // <-- NUEVO
        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Recogemos los datos que nos manda la otra pantalla
        String nombre = getIntent().getStringExtra("TECNICO_NOMBRE");
        String especialidad = getIntent().getStringExtra("TECNICO_ESPECIALIDAD"); // <-- NUEVO

        if (nombre != null) {
            lblNombrePerfil.setText(nombre);
        }

        // Escribimos la especialidad real
        if (especialidad != null && !especialidad.isEmpty()) {
            lblEspecialidadPerfil.setText("Especialista en " + especialidad);
        }

        btnVolver.setOnClickListener(v -> finish());

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}