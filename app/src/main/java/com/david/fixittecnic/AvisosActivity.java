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

public class AvisosActivity extends AppCompatActivity {

    private TextView lblNombreTecnico;
    private TextView lblContadorAvisos;
    private TextView lblNotificacionRoja;
    private View btnCampana;
    private RecyclerView recyclerAvisos;
    private AvisosAdapter adapter;
    private View btnPerfil;

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

        // 2. Recogemos quién inició sesión
        String nombre = getIntent().getStringExtra("TECNICO_NOMBRE");
        Long idTecnico = getIntent().getLongExtra("TECNICO_ID", -1);

        // 🔥 NUEVO: Recogemos la especialidad
        String especialidad = getIntent().getStringExtra("TECNICO_ESPECIALIDAD");

        if (nombre != null) {
            lblNombreTecnico.setText(nombre);
        }

        if (idTecnico != -1) {
            cargarAvisosDelServidor(idTecnico);

            if (btnCampana != null) {
                btnCampana.setOnClickListener(v -> {
                    Toast.makeText(AvisosActivity.this, "Actualizando avisos...", Toast.LENGTH_SHORT).show();
                    cargarAvisosDelServidor(idTecnico);
                });
            }
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(AvisosActivity.this, PerfilActivity.class);
                intent.putExtra("TECNICO_NOMBRE", nombre);

                // 🔥 NUEVO: Se la pasamos a la pantalla de Perfil
                intent.putExtra("TECNICO_ESPECIALIDAD", especialidad);

                startActivity(intent);
            });
        }
    }

    private void cargarAvisosDelServidor(Long idTecnico) {
        RetrofitClient.getApiService().getAvisosPorTecnico(idTecnico).enqueue(new Callback<List<Aviso>>() {
            @Override
            public void onResponse(Call<List<Aviso>> call, Response<List<Aviso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Aviso> lista = response.body();

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