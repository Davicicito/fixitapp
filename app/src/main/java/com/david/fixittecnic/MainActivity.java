package com.david.fixittecnic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Actividad inicial que sirve como pantalla de acceso para los operarios.
 * Gestiona el formulario de entrada, la animacion visual del logo corporativo
 * y la comunicacion con el servidor para validar que el usuario tiene permiso
 * para entrar en el sistema de gestion de avisos.
 */
public class MainActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogin;

    private ImageView imgLogo;
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Prepara los elementos de la interfaz al abrir la aplicacion.
     * Enlaza las cajas de texto, configura el boton de acceso e inicia un ciclo
     * infinito para que el logo se mueva ligeramente cada pocos segundos.
     *
     * @param savedInstanceState Estado previo de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        imgLogo = findViewById(R.id.imgLlave);

        Animation wiggleAnim = AnimationUtils.loadAnimation(this, R.anim.wiggle);

        /**
         * Tarea programada que repite la animacion del logo cada cuatro segundos
         * para darle un toque visual mas profesional y moderno a la bienvenida.
         */
        Runnable repetirAnimacion = new Runnable() {
            @Override
            public void run() {
                imgLogo.startAnimation(wiggleAnim);
                handler.postDelayed(this, 4000);
            }
        };

        handler.post(repetirAnimacion);

        /**
         * Controla la accion de pulsar el boton de entrada.
         * Verifica que el operario no deje campos vacios antes de intentar
         * conectar con el servidor central.
         */
        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            hacerLogin(email, password);
        });
    }

    /**
     * Realiza la llamada al servidor para comprobar si el correo y la clave son validos.
     * Si la respuesta es positiva, recupera la informacion del tecnico y abre la pantalla
     * principal de avisos enviando los datos del empleado para personalizar su sesion.
     *
     * @param email Direccion de correo escrita por el usuario.
     * @param password Clave secreta escrita por el usuario.
     */
    private void hacerLogin(String email, String password) {
        LoginRequest solicitud = new LoginRequest(email, password);

        RetrofitClient.getApiService().login(solicitud).enqueue(new Callback<Tecnico>() {

            /**
             * Gestiona la respuesta del servidor tras el intento de entrada.
             * Si las credenciales son correctas, detiene las animaciones secundarias
             * y salta a la lista de trabajos cerrando la pantalla de login definitivamente.
             */
            @Override
            public void onResponse(Call<Tecnico> call, Response<Tecnico> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tecnico tecnicoLogueado = response.body();
                    Toast.makeText(MainActivity.this, "¡Bienvenido " + tecnicoLogueado.getNombre() + "!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, AvisosActivity.class);

                    intent.putExtra("TECNICO_ID", tecnicoLogueado.getId());
                    intent.putExtra("TECNICO_NOMBRE", tecnicoLogueado.getNombre());
                    intent.putExtra("TECNICO_ESPECIALIDAD", tecnicoLogueado.getEspecialidad());

                    startActivity(intent);

                    handler.removeCallbacksAndMessages(null);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Muestra un mensaje de error si el movil no tiene internet o si el
             * servidor central no esta disponible en ese momento.
             */
            @Override
            public void onFailure(Call<Tecnico> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}