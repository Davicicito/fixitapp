package com.david.fixittecnic;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase encargada de configurar y gestionar la conexion de red de la aplicacion.
 * Implementa el patron de diseño para asegurar que solo exista una instancia de
 * la conexion activa, optimizando el uso de la memoria y la bateria del dispositivo movil.
 */
public class RetrofitClient {

    /**
     * Direccion base del servidor donde esta alojada la API central.
     * La direccion utilizada permite que el emulador de Android se comunique
     * directamente con el servidor de desarrollo instalado en el mismo ordenador.
     */
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    /**
     * Crea y devuelve el objeto encargado de realizar las peticiones a internet.
     * Configura el conversor para que los datos recibidos en formato de texto plano
     * se transformen automaticamente en objetos de Java utilizables por el programa.
     *
     * @return El servicio configurado para hablar con el servidor remoto.
     */
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}