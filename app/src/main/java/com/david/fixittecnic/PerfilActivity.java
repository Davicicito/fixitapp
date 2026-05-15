package com.david.fixittecnic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla que muestra la informacion personal del operario que tiene la sesion iniciada.
 * Permite visualizar el nombre y la especialidad tecnica del empleado, ademas de
 * proporcionar la opcion de cerrar la sesion para salir de la aplicacion de forma segura.
 */
public class PerfilActivity extends AppCompatActivity {

    /**
     * Configura la interfaz de usuario al cargar la pantalla de perfil.
     * Recupera los datos del tecnico que fueron enviados desde la actividad anterior
     * y los muestra en las etiquetas de texto correspondientes.
     *
     * @param savedInstanceState Estado previo para la recuperacion de datos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView lblNombrePerfil = findViewById(R.id.lblNombrePerfil);
        TextView lblEspecialidadPerfil = findViewById(R.id.lblEspecialidadPerfil);
        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String nombre = getIntent().getStringExtra("TECNICO_NOMBRE");
        String especialidad = getIntent().getStringExtra("TECNICO_ESPECIALIDAD");

        if (nombre != null) {
            lblNombrePerfil.setText(nombre);
        }

        if (especialidad != null && !especialidad.isEmpty()) {
            lblEspecialidadPerfil.setText("Especialista en " + especialidad);
        }

        /**
         * Finaliza la actividad actual para regresar a la pantalla anterior de avisos.
         */
        btnVolver.setOnClickListener(v -> finish());

        /**
         * Gestiona el cierre de sesion del usuario.
         * Limpia todo el historial de pantallas abiertas y redirige al tecnico
         * de vuelta al formulario de entrada para asegurar que nadie mas use su cuenta.
         */
        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}