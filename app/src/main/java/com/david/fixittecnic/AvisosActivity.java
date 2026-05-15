package com.david.fixittecnic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla principal de la aplicacion que muestra el listado de tareas asignadas al tecnico.
 * Se encarga de conectar con la API para descargar los avisos, ordenarlos por fecha y
 * gestionar el acceso al perfil del usuario.
 */
public class AvisosActivity extends AppCompatActivity {

    private TextView lblNombreTecnico;
    private TextView lblContadorAvisos;
    private TextView lblNotificacionRoja;
    private View btnCampana;
    private RecyclerView recyclerAvisos;
    private AvisosAdapter adapter;
    private View btnPerfil;
    private Long idTecnicoGlobal;

    /**
     * Prepara la interfaz de usuario al iniciar la actividad.
     * Configura el adaptador para la lista, recupera los datos del tecnico enviados desde el login
     * y programa los botones para actualizar la lista o ver el perfil.
     *
     * @param savedInstanceState Estado de la instancia para recuperar datos previos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos);

        lblNombreTecnico = findViewById(R.id.lblNombreTecnico);
        lblContadorAvisos = findViewById(R.id.lblContadorAvisos);
        lblNotificacionRoja = findViewById(R.id.lblNotificacionRoja);
        btnCampana = findViewById(R.id.btnCampana);
        btnPerfil = findViewById(R.id.btnPerfil);
        recyclerAvisos = findViewById(R.id.recyclerAvisos);

        recyclerAvisos.setLayoutManager(new LinearLayoutManager(this));

        String nombre = getIntent().getStringExtra("TECNICO_NOMBRE");

        idTecnicoGlobal = getIntent().getLongExtra("TECNICO_ID", -1L);

        // Recogemos la especialidad
        String especialidad = getIntent().getStringExtra("TECNICO_ESPECIALIDAD");

        if (nombre != null) {
            lblNombreTecnico.setText(nombre);
        }

        if (idTecnicoGlobal != -1L) {
            if (btnCampana != null) {
                /**
                 * Accion manual para refrescar el listado de trabajos pendientes.
                 */
                btnCampana.setOnClickListener(v -> {
                    Toast.makeText(AvisosActivity.this, "Actualizando avisos...", Toast.LENGTH_SHORT).show();
                    cargarAvisosDelServidor(idTecnicoGlobal);
                });
            }
        }

        if (btnPerfil != null) {
            /**
             * Abre la pantalla de perfil del tecnico enviando su nombre y especialidad.
             */
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(AvisosActivity.this, PerfilActivity.class);
                intent.putExtra("TECNICO_NOMBRE", nombre);
                intent.putExtra("TECNICO_ESPECIALIDAD", especialidad);
                startActivity(intent);
            });
        }
    }

    /**
     * Se ejecuta cada vez que el usuario vuelve a esta pantalla.
     * Sirve para asegurar que la lista de trabajos este siempre actualizada, por ejemplo
     * despues de haber finalizado una reparacion.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Si tenemos el ID válido, forzamos a que vuelva a pedir la lista al servidor
        if (idTecnicoGlobal != null && idTecnicoGlobal != -1L) {
            cargarAvisosDelServidor(idTecnicoGlobal);
        }
    }

    /**
     * Realiza una peticion al servidor para obtener las averias vinculadas al empleado.
     * Una vez recibida la informacion, ordena los avisos de mas reciente a mas antiguo
     * y actualiza los contadores de notificaciones de la pantalla.
     *
     * @param idTecnico El numero identificador del operario.
     */
    private void cargarAvisosDelServidor(Long idTecnico) {
        RetrofitClient.getApiService().getAvisosPorTecnico(idTecnico).enqueue(new Callback<List<Aviso>>() {
            @Override
            public void onResponse(Call<List<Aviso>> call, Response<List<Aviso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Aviso> lista = response.body();

                    // Filtramos para que los avisos completados no estorben o se ordenen bien
                    Collections.sort(lista, new Comparator<Aviso>() {
                        @Override
                        public int compare(Aviso a1, Aviso a2) {
                            String fecha1 = a1.getFechaCreacion() != null ? a1.getFechaCreacion() : "";
                            String fecha2 = a2.getFechaCreacion() != null ? a2.getFechaCreacion() : "";
                            return fecha2.compareTo(fecha1);
                        }
                    });

                    String totalTrabajos = String.valueOf(lista.size());
                    lblContadorAvisos.setText(totalTrabajos);

                    if (lblNotificacionRoja != null) {
                        if (lista.isEmpty()) {
                            lblNotificacionRoja.setVisibility(View.GONE);
                        } else {
                            lblNotificacionRoja.setVisibility(View.VISIBLE);
                            lblNotificacionRoja.setText(totalTrabajos);
                        }
                    }

                    adapter = new AvisosAdapter(lista);
                    recyclerAvisos.setAdapter(adapter);
                    recyclerAvisos.scrollToPosition(0);

                } else {
                    Toast.makeText(AvisosActivity.this, "Error al cargar trabajos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Aviso>> call, Throwable t) {
                Toast.makeText(AvisosActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}