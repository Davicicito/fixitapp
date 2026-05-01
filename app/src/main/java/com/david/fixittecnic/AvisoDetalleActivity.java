package com.david.fixittecnic;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AvisoDetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_detalle);

        // 1. Enlazamos la interfaz
        View bgCabecera = findViewById(R.id.bgCabeceraDetalle);
        View btnVolver = findViewById(R.id.btnVolverDetalle);
        ImageView iconoOficio = findViewById(R.id.iconoOficioDetalle);
        ImageView imgEstadoPunto = findViewById(R.id.imgEstadoPunto);

        // 🔥 NUEVOS BOTONES 🔥
        View btnNavegar = findViewById(R.id.btnNavegarDetalle);
        View btnLlamar = findViewById(R.id.btnLlamarDetalle);

        TextView lblPrioridad = findViewById(R.id.lblPrioridadDetalle);
        TextView lblCategoria = findViewById(R.id.lblCategoriaDetalle);
        TextView lblCliente = findViewById(R.id.lblClienteDetalle);
        TextView txtDescripcion = findViewById(R.id.txtDescripcionDetalle);
        TextView txtDireccion = findViewById(R.id.txtDireccionDetalle);
        TextView txtTelefono = findViewById(R.id.txtTelefonoDetalle);
        TextView txtFecha = findViewById(R.id.txtFechaDetalle);
        TextView txtEstado = findViewById(R.id.txtEstadoDetalle);
        TextView btnIniciarTrabajo = findViewById(R.id.btnIniciarTrabajo);

        // 2. Recibimos los datos de la pantalla anterior
        String categoria = getIntent().getStringExtra("CATEGORIA");
        String cliente = getIntent().getStringExtra("CLIENTE");
        String prioridad = getIntent().getStringExtra("PRIORIDAD");
        String descripcion = getIntent().getStringExtra("DESCRIPCION");
        String direccion = getIntent().getStringExtra("DIRECCION");
        String telefono = getIntent().getStringExtra("TELEFONO");
        String fecha = getIntent().getStringExtra("FECHA");
        String estado = getIntent().getStringExtra("ESTADO");

        // 3. Rellenamos los textos
        lblCategoria.setText(categoria != null ? categoria : "GENERAL");
        lblCliente.setText(cliente != null ? cliente : "Cliente Desconocido");
        lblPrioridad.setText("Prioridad " + (prioridad != null ? prioridad : "MEDIA"));
        txtDescripcion.setText(descripcion != null ? descripcion : "Sin descripción.");
        txtDireccion.setText(direccion != null ? direccion : "Sin dirección");
        txtTelefono.setText(telefono != null ? telefono : "Sin teléfono");
        txtFecha.setText(fecha != null ? fecha : "--/--");
        txtEstado.setText(estado != null ? estado : "PENDIENTE");

        // Color de la etiqueta de prioridad
        if ("ALTA".equalsIgnoreCase(prioridad)) {
            lblPrioridad.setBackgroundResource(R.drawable.bg_badge_alta);
        } else if ("MEDIA".equalsIgnoreCase(prioridad)) {
            lblPrioridad.setBackgroundResource(R.drawable.bg_badge_media);
        } else {
            lblPrioridad.setBackgroundResource(R.drawable.bg_badge_baja);
        }

        // 4. MAGIA DE TEMAS
        String catNormalizada = categoria != null ? categoria.toUpperCase() : "";
        int[] coloresDegradado;

        if (catNormalizada.contains("FONTANER")) {
            coloresDegradado = new int[]{Color.parseColor("#00B4DB"), Color.parseColor("#0083B0")};
            iconoOficio.setImageResource(R.drawable.ic_gota);
        } else if (catNormalizada.contains("ELECTRIC")) {
            coloresDegradado = new int[]{Color.parseColor("#FFB75E"), Color.parseColor("#ED8F03")};
            iconoOficio.setImageResource(R.drawable.ic_rayo);
        } else if (catNormalizada.contains("ASCENSOR")) {
            coloresDegradado = new int[]{Color.parseColor("#DA22FF"), Color.parseColor("#9733EE")};
            iconoOficio.setImageResource(R.drawable.ic_ajustes);
        } else {
            coloresDegradado = new int[]{Color.parseColor("#94A3B8"), Color.parseColor("#475569")};
            iconoOficio.setImageResource(R.drawable.ic_portapapeles);
        }

        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR, coloresDegradado);
        bgCabecera.setBackground(gradient);

        // 5. ANIMACIÓN DEL LED DE ESTADO
        Animation latido = AnimationUtils.loadAnimation(this, R.anim.latido);
        imgEstadoPunto.startAnimation(latido);

        // ==========================================
        // 6. 🔥 ACCIONES DE LOS BOTONES 🔥
        // ==========================================

        btnVolver.setOnClickListener(v -> finish());

        // Botón Llamar
        if (btnLlamar != null) {
            btnLlamar.setOnClickListener(v -> {
                if (telefono != null && !telefono.isEmpty()) {
                    // ACTION_DIAL abre la app de teléfono con el número marcado, pero no llama automáticamente (es más seguro)
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + telefono));
                    startActivity(intent);
                } else {
                    Toast.makeText(AvisoDetalleActivity.this, "No hay número de teléfono", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Botón Navegar (Google Maps)
        if (btnNavegar != null) {
            btnNavegar.setOnClickListener(v -> {
                if (direccion != null && !direccion.isEmpty()) {
                    // geo:0,0?q=  busca esa dirección exacta en la app de mapas
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Le decimos que preferimos usar Google Maps
                    mapIntent.setPackage("com.google.android.apps.maps");

                    try {
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        // Si no tiene Google Maps instalado, lo abrimos en el navegador web
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(direccion)));
                        startActivity(browserIntent);
                    }
                } else {
                    Toast.makeText(AvisoDetalleActivity.this, "No hay dirección disponible", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnIniciarTrabajo.setOnClickListener(v -> {
            Toast.makeText(AvisoDetalleActivity.this, "Iniciando trabajo...", Toast.LENGTH_SHORT).show();
        });
    }
}