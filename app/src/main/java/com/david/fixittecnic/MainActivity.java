package com.david.fixittecnic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Declaramos los elementos que tenemos en el XML
    private EditText txtEmail, txtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Buscamos los elementos en la pantalla por su ID
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Opcional: Aquí puedes poner el código de la animación de la llave
        // si se lo pusiste al ImageView (imgLlave) en pasos anteriores.

        // 2. Le decimos al botón qué tiene que hacer al pulsarlo
        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            // Pequeña validación para que no le den al botón en blanco
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamamos al método que se comunica con el servidor
            hacerLogin(email, password);
        });
    }

    // 3. El método que hace la magia de hablar con tu ordenador
    private void hacerLogin(String email, String password) {
        // Empaquetamos los datos
        LoginRequest solicitud = new LoginRequest(email, password);

        // Le damos el paquete al mensajero (Retrofit)
        RetrofitClient.getApiService().login(solicitud).enqueue(new Callback<Tecnico>() {

            @Override
            public void onResponse(Call<Tecnico> call, Response<Tecnico> response) {
                // Si la conexión ha llegado al servidor y este ha respondido
                if (response.isSuccessful() && response.body() != null) {
                    // ¡EXITO! Ha encontrado al técnico en la base de datos
                    Tecnico tecnicoLogueado = response.body();
                    Toast.makeText(MainActivity.this, "¡Bienvenido " + tecnicoLogueado.getNombre() + "!", Toast.LENGTH_LONG).show();

                    // Más adelante, aquí pondremos el código para saltar a la pantalla de Avisos
                } else {
                    // Ha conectado, pero el correo o la contraseña están mal
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tecnico> call, Throwable t) {
                // ERROR GRAVE: No ha podido llegar a tu ordenador
                // (Spring Boot apagado, IP mal puesta, o el móvil no está en el mismo Wi-Fi)
                Toast.makeText(MainActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}