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

public class TrabajoProgresoActivity extends AppCompatActivity {

    private List<MaterialUsado> listaMateriales = new ArrayList<>();
    private List<MaterialApi> materialesDelServidor = new ArrayList<>();

    private Bitmap fotoAveria = null;
    private Bitmap firmaCliente = null;

    private Long idAvisoActual;

    // CAPTURA DE FOTO
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

        btnAddMaterial.setOnClickListener(v -> {
            String nombre = txtNombreMaterial.getText().toString().trim();
            String cantidadStr = txtCantidadMaterial.getText().toString().trim();
            String unidad = spinnerUnidadMaterial.getSelectedItem().toString();

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(this, "Completa nombre y cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            Long idReal = null;
            for (MaterialApi mat : materialesDelServidor) {
                if (mat.getNombre().equalsIgnoreCase(nombre)) {
                    idReal = (long) mat.getId();
                    break;
                }
            }

            if (idReal == null) {
                Toast.makeText(this, "Material no válido. Selecciónalo de la lista.", Toast.LENGTH_LONG).show();
                return;
            }

            double cantidad = Double.parseDouble(cantidadStr);
            MaterialUsado nuevo = new MaterialUsado(idReal, nombre, cantidad, unidad);
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

        btnFinalizarTrabajo.setOnClickListener(v -> {
            String obs = txtObservaciones.getText().toString().trim();

            if (listaMateriales.isEmpty() && obs.isEmpty()) {
                Toast.makeText(this, "Añade materiales u observaciones", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TrabajoProgresoActivity.this, "¡Trabajo completado!", Toast.LENGTH_LONG).show();
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

    private String convertirBitmapABase64(Bitmap bmp) {
        if (bmp == null) return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

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