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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla detallada que muestra toda la informacion de un parte de trabajo especifico.
 * Permite al operario consultar la direccion, llamar al cliente por telefono, abrir la ruta
 * en el mapa y finalmente iniciar la reparacion.
 */
public class AvisoDetalleActivity extends AppCompatActivity {

    /**
     * Metodo principal que se ejecuta al abrir la pantalla de detalles.
     * Recupera los datos enviados desde la lista principal y configura la apariencia visual
     * cambiando los colores y los iconos segun la especialidad del trabajo.
     *
     * @param savedInstanceState Estado previo de la actividad para restauracion de datos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_detalle);

        // 1. Enlazamos la interfaz
        View bgCabecera = findViewById(R.id.bgCabeceraDetalle);
        View btnVolver = findViewById(R.id.btnVolverDetalle);
        ImageView iconoOficio = findViewById(R.id.iconoOficioDetalle);
        ImageView imgEstadoPunto = findViewById(R.id.imgEstadoPunto);

        // BOTONES
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
        View btnIniciarTrabajo = findViewById(R.id.btnIniciarTrabajo);

        long idAviso = getIntent().getLongExtra("ID", -1L);

        if (idAviso == -1L) {
            idAviso = getIntent().getIntExtra("ID", -1);
        }
        if (idAviso == -1L) {
            String idStr = getIntent().getStringExtra("ID");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    idAviso = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    idAviso = -1L;
                }
            }
        }

        if (idAviso == -1L) {
            Toast.makeText(this, "El Adapter no está enviando el ID a esta pantalla", Toast.LENGTH_LONG).show();
        }

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

        // 4. TEMAS
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


        // 6. ACCIONES DE LOS BOTONES
        /**
         * Finaliza la actividad actual para regresar a la pantalla anterior del listado.
         */
        btnVolver.setOnClickListener(v -> finish());

        // Botón Llamar
        /**
         * Abre el marcador telefonico del movil con el numero del cliente ya escrito.
         * Limpia el texto del telefono y añade el prefijo nacional para asegurar la llamada.
         */
        if (btnLlamar != null) {
            btnLlamar.setOnClickListener(v -> {
                if (telefono != null && !telefono.isEmpty()) {
                    String numeroLimpio = telefono.replaceAll("[\\s-]", "");
                    if (!numeroLimpio.startsWith("+")) {
                        numeroLimpio = "+34" + numeroLimpio;
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + numeroLimpio));
                    startActivity(intent);
                } else {
                    Toast.makeText(AvisoDetalleActivity.this, "No hay número de teléfono", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Botón Navegar
        /**
         * Abre la aplicacion de Google Maps para guiar al tecnico hasta la direccion del cliente.
         * Si el movil no tiene instalada la aplicacion de mapas intenta abrirla en el navegador.
         */
        if (btnNavegar != null) {
            btnNavegar.setOnClickListener(v -> {
                if (direccion != null && !direccion.isEmpty()) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    try {
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(direccion)));
                        startActivity(browserIntent);
                    }
                } else {
                    Toast.makeText(AvisoDetalleActivity.this, "No hay dirección disponible", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 1. Definimos un "receptor" para cuando volvamos de la pantalla de progreso
        /**
         * Escucha el resultado de la pantalla de reparacion en curso.
         * Si el operario termina el trabajo correctamente esta pantalla se cerrara automaticamente
         * para devolver al usuario a la lista general de tareas.
         */
        ActivityResultLauncher<Intent> receptorProgreso = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Si la pantalla de progreso nos dice que el resultado fue OK
                    if (result.getResultCode() == RESULT_OK) {
                        // Significa que el técnico FINALIZÓ el trabajo.
                        // Cerramos también esta pantalla para volver a la lista principal.
                        finish();
                    }
                }
        );

        final long idAvisoFinal = idAviso;

        /**
         * Inicia el proceso de reparacion abriendo la pantalla de progreso.
         * Envia el identificador de la averia y el nombre del cliente para que se muestren en la nueva actividad.
         */
        btnIniciarTrabajo.setOnClickListener(v -> {
            Intent intent = new Intent(AvisoDetalleActivity.this, TrabajoProgresoActivity.class);
            intent.putExtra("CLIENTE", cliente);
            intent.putExtra("ID_AVISO", idAvisoFinal);

            receptorProgreso.launch(intent);
        });
    }
}