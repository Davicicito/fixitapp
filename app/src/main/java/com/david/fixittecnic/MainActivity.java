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

public class MainActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogin;

    private ImageView imgLogo;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        imgLogo = findViewById(R.id.imgLlave);

        Animation wiggleAnim = AnimationUtils.loadAnimation(this, R.anim.wiggle);

        Runnable repetirAnimacion = new Runnable() {
            @Override
            public void run() {
                imgLogo.startAnimation(wiggleAnim);
                handler.postDelayed(this, 4000);
            }
        };

        handler.post(repetirAnimacion);

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

    private void hacerLogin(String email, String password) {
        LoginRequest solicitud = new LoginRequest(email, password);

        RetrofitClient.getApiService().login(solicitud).enqueue(new Callback<Tecnico>() {

            @Override
            public void onResponse(Call<Tecnico> call, Response<Tecnico> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tecnico tecnicoLogueado = response.body();
                    Toast.makeText(MainActivity.this, "¡Bienvenido " + tecnicoLogueado.getNombre() + "!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, AvisosActivity.class);

                    intent.putExtra("TECNICO_ID", tecnicoLogueado.getId());
                    intent.putExtra("TECNICO_NOMBRE", tecnicoLogueado.getNombre());

                    // 🔥 NUEVO: Metemos la especialidad en la mochila
                    intent.putExtra("TECNICO_ESPECIALIDAD", tecnicoLogueado.getEspecialidad());

                    startActivity(intent);

                    handler.removeCallbacksAndMessages(null);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tecnico> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}