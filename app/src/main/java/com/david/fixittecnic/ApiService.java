package com.david.fixittecnic;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<Tecnico> login(@Body LoginRequest request);

    @GET("api/avisos/tecnico/{id}")
    Call<List<Aviso>> getAvisosPorTecnico(@Path("id") Long idTecnico);

    @GET("materiales")
    Call<List<MaterialApi>> obtenerListaMateriales();

    @POST("avisos/{id}/finalizar")
    Call<Object> finalizarAviso(@Path("id") Long idAviso, @Body FinalizarAvisoRequest peticion);
}