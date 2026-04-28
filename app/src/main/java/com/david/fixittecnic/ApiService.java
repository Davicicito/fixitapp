package com.david.fixittecnic;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Esta es la ruta exacta que creamos en tu Spring Boot
    @POST("api/auth/login")
    Call<Tecnico> login(@Body LoginRequest request);

}