package com.david.fixittecnic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Actividad principal del proceso de reparacion en la que el operario registra su actividad.
 * Gestiona la toma de fotografias de la averia, el control de stock de los materiales consumidos
 * y la recogida de la firma del cliente antes de enviar el informe final al servidor central.
 */
public class TrabajoProgresoActivity extends AppCompatActivity {

    private List<MaterialUsado> listaMateriales = new ArrayList<>();
    private List<MaterialApi> materialesDelServidor = new ArrayList<>();

    private Bitmap fotoAveria = null;
    private Bitmap firmaCliente = null;

    private Long idAvisoActual;

    // CAPTURA DE FOTO
    /**
     * Receptor encargado de recoger la fotografia tomada con la camara del dispositivo.
     * Al recibir la imagen, la muestra en miniatura dentro de la interfaz y oculta el texto informativo.
     */
    private final ActivityResultLauncher<Intent> lanzadorCamara = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    fotoAveria = (Bitmap) extras.get("data");

                    ImageView imgFotoCapturada = findViewById(R.id.imgFotoCapturada);
                    View panelTextoCamara = findViewById(R.id.panelTextoCamara);

                    imgFotoCapturada.setImageBitmap(fotoAveria);
                    imgFotoCapturada.setVisibility(View.VISIBLE);
                    panelTextoCamara.setVisibility(View.GONE);
                }
            }
    );

    // CAPTURA DE FIRMA
    /**
     * Receptor que obtiene la imagen de la firma capturada en la pantalla de dibujo.
     * Transforma el arreglo de bytes recibido en una imagen visible para confirmar la conformidad.
     */
    private final ActivityResultLauncher<Intent> lanzadorFirma = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    byte[] byteArray = result.getData().getByteArrayExtra("firma_imagen_array");
                    if (byteArray != null) {
                        firmaCliente = android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                        ImageView imgFirmaCapturada = findViewById(R.id.imgFirmaCapturada);
                        View panelTextoFirma = findViewById(R.id.panelTextoFirma);

                        imgFirmaCapturada.setImageBitmap(firmaCliente);
                        imgFirmaCapturada.setVisibility(View.VISIBLE);
                        panelTextoFirma.setVisibility(View.GONE);
                    }
                }
            }
    );

    /**
     * Inicializa todos los componentes del formulario de reparacion.
     * Configura el buscador de materiales, descarga el stock actualizado desde la API y
     * prepara las validaciones necesarias para asegurar que el informe este completo.
     *
     * @param savedInstanceState Estado previo de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajo_progreso);

        idAvisoActual = getIntent().getLongExtra("ID_AVISO", -1L);

        if (idAvisoActual == -1L) {
            Toast.makeText(this, "Error: No se recibió el ID del aviso", Toast.LENGTH_LONG).show();
        }

        View btnVolver = findViewById(R.id.btnVolverProgreso);
        TextView lblCliente = findViewById(R.id.lblClienteProgreso);
        TextView txtContadorMateriales = findViewById(R.id.txtContadorMateriales);
        AutoCompleteTextView txtNombreMaterial = findViewById(R.id.txtNombreMaterial);
        EditText txtCantidadMaterial = findViewById(R.id.txtCantidadMaterial);
        Spinner spinnerUnidadMaterial = findViewById(R.id.spinnerUnidadMaterial);
        View btnAddMaterial = findViewById(R.id.btnAddMaterial);
        LinearLayout contenedorListaMateriales = findViewById(R.id.contenedorListaMateriales);

        View btnTomarFoto = findViewById(R.id.btnTomarFoto);
        View btnRecogerFirma = findViewById(R.id.btnRecogerFirma);
        EditText txtObservaciones = findViewById(R.id.txtObservaciones);
        android.widget.RatingBar ratingBarCliente = findViewById(R.id.ratingBarCliente);
        View btnFinalizarTrabajo = findViewById(R.id.btnFinalizarTrabajo);

        String cliente = getIntent().getStringExtra("CLIENTE");
        if (cliente != null) lblCliente.setText(cliente);

        String[] unidades = {"unidad", "metros", "kg", "litros"};
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidades);
        spinnerUnidadMaterial.setAdapter(adapterUnidades);

        ArrayAdapter<MaterialApi> adapterBuscador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, materialesDelServidor);
        txtNombreMaterial.setAdapter(adapterBuscador);

        // CONTROL DE UNIDADES
        /**
         * Ajusta automaticamente la unidad de medida cuando el tecnico selecciona un material
         * del buscador, evitando errores al registrar el consumo.
         */
        txtNombreMaterial.setOnItemClickListener((parent, view, position, id) -> {
            MaterialApi seleccionado = (MaterialApi) parent.getItemAtPosition(position);
            if (seleccionado != null && seleccionado.getUnidad() != null) {
                // Buscamos la unidad en el spinner
                for (int i = 0; i < spinnerUnidadMaterial.getCount(); i++) {
                    if (spinnerUnidadMaterial.getItemAtPosition(i).toString().equalsIgnoreCase(seleccionado.getUnidad())) {
                        spinnerUnidadMaterial.setSelection(i);
                        break;
                    }
                }
                // Lo bloqueamos para que no puedan cambiar "metros" por "litros"
                spinnerUnidadMaterial.setEnabled(false);
            }
        });

        /**
         * Peticion asincrona para traer los materiales disponibles desde el servidor.
         */
        ApiService apiService = RetrofitClient.getApiService();
        apiService.obtenerListaMateriales().enqueue(new Callback<List<MaterialApi>>() {
            @Override
            public void onResponse(Call<List<MaterialApi>> call, Response<List<MaterialApi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    materialesDelServidor.clear();
                    materialesDelServidor.addAll(response.body());
                    adapterBuscador.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<MaterialApi>> call, Throwable t) {
                Toast.makeText(TrabajoProgresoActivity.this, "Error al conectar al inventario", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Añade un nuevo material a la lista del parte de trabajo.
         * Realiza una comprobacion de stock critica comparando lo solicitado por el tecnico
         * con las existencias reales guardadas en la base de datos.
         */
        btnAddMaterial.setOnClickListener(v -> {
            String nombre = txtNombreMaterial.getText().toString().trim();
            String cantidadStr = txtCantidadMaterial.getText().toString().trim();
            String unidad = spinnerUnidadMaterial.getSelectedItem().toString();

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(this, "Completa nombre y cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidadPedida = Double.parseDouble(cantidadStr);
            Long idReal = null;
            int stockDisponible = 0;

            // Buscamos el material en la lista que nos descargamos de la API
            for (MaterialApi mat : materialesDelServidor) {
                if (mat.getNombre().equalsIgnoreCase(nombre)) {
                    idReal = (long) mat.getId();
                    stockDisponible = mat.getStock();
                    break;
                }
            }

            if (idReal == null) {
                Toast.makeText(this, "Material no válido. Selecciónalo de la lista.", Toast.LENGTH_LONG).show();
                return;
            }


            // Primero comprobamos si ya había añadido ese mismo material a la lista antes
            double cantidadYaAñadida = 0.0;
            for (MaterialUsado mu : listaMateriales) {
                if (mu.idBD.equals(idReal)) {
                    cantidadYaAñadida += mu.cantidad;
                }
            }

            // 2. Comparamos lo que pide y lo que ya había en la lista mirando el Stock Real
            if ((cantidadPedida + cantidadYaAñadida) > stockDisponible) {
                String mensajeAlerta = "¡Stock Insuficiente!\nSolo quedan " + stockDisponible + " " + unidad + " en el almacén.";

                if (cantidadYaAñadida > 0) {
                    mensajeAlerta += "\n(Ya has añadido " + cantidadYaAñadida + " a este aviso).";
                }

                Toast.makeText(this, mensajeAlerta, Toast.LENGTH_LONG).show();
                return;
            }

            MaterialUsado nuevo = new MaterialUsado(idReal, nombre, cantidadPedida, unidad);
            listaMateriales.add(nuevo);

            actualizarListaVisual(contenedorListaMateriales, txtContadorMateriales);

            txtNombreMaterial.setText("");
            txtCantidadMaterial.setText("");
            spinnerUnidadMaterial.setEnabled(true);
            txtNombreMaterial.requestFocus();
        });

        btnTomarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            lanzadorCamara.launch(intent);
        });

        btnRecogerFirma.setOnClickListener(v -> {
            Intent intent = new Intent(this, FirmaActivity.class);
            lanzadorFirma.launch(intent);
        });

        /**
         * Proceso final de envio del informe al servidor central.
         * Verifica que el operario haya cumplido con todos los requisitos: foto de la averia,
         * firma del cliente, observaciones escritas y al menos un material consumido.
         */
        btnFinalizarTrabajo.setOnClickListener(v -> {
            String obs = txtObservaciones.getText().toString().trim();

            // 1. VALIDACIÓN DE FOTO
            if (fotoAveria == null) {
                Toast.makeText(this, "Debes tomar una foto de la avería antes de finalizar.", Toast.LENGTH_LONG).show();
                return;
            }

            // 2. VALIDACIÓN DE FIRMA
            if (firmaCliente == null) {
                Toast.makeText(this, "Es obligatorio recoger la firma del cliente.", Toast.LENGTH_LONG).show();
                return;
            }

            // 3. VALIDACIÓN DE OBSERVACIONES
            if (obs.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe unas breves observaciones sobre el trabajo realizado.", Toast.LENGTH_LONG).show();
                return;
            }

            // 4. VALIDACIÓN DE MATERIALES
            if (listaMateriales.isEmpty()) {
                Toast.makeText(this, "Debes indicar al menos un material utilizado.", Toast.LENGTH_LONG).show();
                return;
            }
            List<MaterialGastado> paraEnviar = new ArrayList<>();
            for (MaterialUsado mu : listaMateriales) {
                paraEnviar.add(new MaterialGastado(mu.idBD, mu.cantidad));
            }

            String fotoBase64 = convertirBitmapABase64(fotoAveria);
            String firmaBase64 = convertirBitmapABase64(firmaCliente);
            int estrellas = (int) ratingBarCliente.getRating();

            FinalizarAvisoRequest request = new FinalizarAvisoRequest(
                    "COMPLETADO", obs, fotoBase64, firmaBase64, estrellas, paraEnviar
            );

            apiService.finalizarAviso(idAvisoActual, request).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TrabajoProgresoActivity.this, "¡Trabajo completado con éxito!", Toast.LENGTH_LONG).show();
                        Intent data = new Intent();
                        data.putExtra("ID_CERRADO", idAvisoActual);
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Toast.makeText(TrabajoProgresoActivity.this, "Error " + response.code() + ": Revisa el servidor", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(TrabajoProgresoActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Refresca la lista de materiales agregados en la pantalla para que el tecnico
     * vea los cambios al añadir o eliminar piezas.
     *
     * @param contenedor Layout donde se insertan las filas de materiales.
     * @param contador Etiqueta de texto que muestra el total de piezas consumidas.
     */
    private void actualizarListaVisual(LinearLayout contenedor, TextView contador) {
        contenedor.removeAllViews();
        for (MaterialUsado m : listaMateriales) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_material_agregado, null);
            ((TextView) item.findViewById(R.id.lblNombreItem)).setText(m.nombre);
            ((TextView) item.findViewById(R.id.lblCantidadItem)).setText(m.cantidad + " " + m.unidad);

            item.findViewById(R.id.btnEliminarItem).setOnClickListener(v -> {
                listaMateriales.remove(m);
                actualizarListaVisual(contenedor, contador);
            });
            contenedor.addView(item);
        }
        contador.setText(String.valueOf(listaMateriales.size()));
    }

    /**
     * Transforma una imagen de mapa de bits en una cadena de texto Base64.
     * Este proceso es esencial para poder enviar archivos multimedia a traves de
     * una peticion estandar de internet hacia la API central.
     *
     * @param bmp La imagen que se desea convertir.
     * @return La cadena de texto resultante lista para su envio.
     */
    private String convertirBitmapABase64(Bitmap bmp) {
        if (bmp == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Clase interna que representa de forma temporal un material añadido al parte.
     */
    public static class MaterialUsado {
        Long idBD;
        String nombre;
        double cantidad;
        String unidad;

        public MaterialUsado(Long idBD, String nombre, double cantidad, String unidad) {
            this.idBD = idBD;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.unidad = unidad;
        }
    }
}