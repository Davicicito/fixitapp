package com.david.fixittecnic;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interfaz que define todos los puntos de conexion con el servidor remoto.
 * Utiliza la libreria Retrofit para transformar las rutas de internet en funciones de Java
 * que el tecnico puede usar desde su telefono para descargar o enviar informacion.
 */
public interface ApiService {

    /**
     * Envia las credenciales del operario al servidor para verificar su identidad.
     * Si los datos son correctos el sistema permitira la entrada a la aplicacion.
     *
     * @param request Paquete que contiene el correo y la clave del trabajador.
     * @return Una llamada que devolvera los datos del tecnico si el acceso es concedido.
     */
    @POST("api/auth/login")
    Call<Tecnico> login(@Body LoginRequest request);

    /**
     * Solicita al servidor el listado de averias que tiene asignadas el empleado actualmente.
     *
     * @param idTecnico El numero de identificador unico del operario que consulta su agenda.
     * @return Una llamada con la lista de avisos pendientes de realizar.
     */
    @GET("api/avisos/tecnico/{id}")
    Call<List<Aviso>> getAvisosPorTecnico(@Path("id") Long idTecnico);

    /**
     * Descarga el catalogo completo de productos y repuestos disponibles en el almacen central.
     * Sirve para que el operario pueda seleccionar que piezas ha gastado en cada reparacion.
     *
     * @return Una llamada con el listado de todos los materiales del inventario.
     */
    @GET("materiales")
    Call<List<MaterialApi>> obtenerListaMateriales();

    /**
     * Notifica al sistema que una reparacion ha sido terminada con exito.
     * Envia al servidor todo el informe final incluyendo las fotos de la averia y la firma del cliente.
     *
     * @param idAviso El numero del parte de trabajo que se desea cerrar.
     * @param peticion El paquete completo con los datos del informe y los materiales consumidos.
     * @return Una respuesta generica que confirma si el guardado ha sido correcto.
     */
    @POST("avisos/{id}/finalizar")
    Call<Object> finalizarAviso(@Path("id") Long idAviso, @Body FinalizarAvisoRequest peticion);
}